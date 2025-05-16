package com.realityexpander.tasky.core.presentation.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.realityexpander.tasky.MainActivity
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.IRemindAtAlarmManager
import com.realityexpander.tasky.core.presentation.broadcastReceivers.AlarmBroadcastReceiver
import com.realityexpander.tasky.core.presentation.notifications.RemindAtNotificationManagerImpl.Companion.ALARM_NOTIFICATION_INTENT_ACTION_ALARM_TRIGGER
import com.realityexpander.tasky.core.presentation.notifications.RemindAtNotificationManagerImpl.Companion.ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM
import com.realityexpander.tasky.core.presentation.notifications.RemindAtNotificationManagerImpl.Companion.ALARM_NOTIFICATION_INTENT_EXTRA_ALARM_ID
import com.realityexpander.tasky.core.util.toEpochMilli
import com.realityexpander.tasky.core.util.toZonedDateTime
import logcat.logcat
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class RemindAtAlarmManagerImpl @Inject constructor(
    val context: Context,
) : IRemindAtAlarmManager {

    companion object {
        private const val CURRENT_ALARMS_PENDING_INTENTS = "CURRENT_ALARMS_PENDING_INTENTS"
        private const val CURRENT_ALARMS_TITLES = "CURRENT_ALARMS_TITLES"

        private const val REMIND_AT_ALARM_SUPERVISOR_REQUEST_CODE = 1
    }

    @androidx.annotation.RequiresPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun setAlarmsForAgendaItems(
        agendaItems: List<AgendaItem>
    ) {

        // Only include upcoming `Remind At` items (RemindAt is in the future)
        val futureAgendaItems =
            agendaItems.filter {
                it.remindAt.toEpochMilli() >= ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                    .toEpochMilli()
            }

        if (futureAgendaItems.isEmpty()) {
            return
        }

        logcat {
            "setAlarmsForAgendaItems: futureAgendaItems = ${
                futureAgendaItems.map { it.title }
                    .joinToString { it }
            }"
        }

        // Create an "Alarm PendingIntent" for each AgendaItem's `.remindAt` time
        val alarmPendingIntents = mutableListOf<PendingIntent>()
        futureAgendaItems.forEachIndexed { alarmIndex, agendaItem ->
            val newAlarmPendingIntent =
                createAlarmPendingIntentAndSetAlarm(agendaItem, alarmIndex)
            alarmPendingIntents.add(newAlarmPendingIntent)
        }

        // Save all current "Alarm PendingIntents" to this "Alarm Supervisor PendingIntent"
        //   - used to cancel all alarms, when needed.
        PendingIntent.getBroadcast(
            context,
            REMIND_AT_ALARM_SUPERVISOR_REQUEST_CODE,
            Intent(context, MainActivity::class.java).also {
                it.putExtra(
                    CURRENT_ALARMS_PENDING_INTENTS,
                    arrayOf<PendingIntent>(*alarmPendingIntents.toTypedArray())
                )
                it.putStringArrayListExtra( // for debugging
                    CURRENT_ALARMS_TITLES,
                    futureAgendaItems.map { agendaItem ->
                        agendaItem.title
                    }.toCollection(ArrayList())
                )
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun cancelAllAlarms(
        onFinished: () -> Unit,  // optional callback after all alarms are cancelled.
    ) {
        // Based on: https://stackoverflow.com/questions/4315611/android-get-all-pendingintents-set-with-alarmmanager

        // Acquire the "Alarm Supervisor PendingIntent" that contains all the current "Alarm PendingIntents".
        val alarmSupervisorPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        logcat { "cancelAllAlarms alarmSupervisorPendingIntent=$alarmSupervisorPendingIntent" }
        if (alarmSupervisorPendingIntent != null) {
            try {
                // Send the "cancel Alarm" command to each Alarm PendingIntent in the Supervisor PendingIntent.
                alarmSupervisorPendingIntent.send(
                    context,
                    REMIND_AT_ALARM_SUPERVISOR_REQUEST_CODE,
                    null,
                    // This "onFinished" Handler Lambda cancels all supervised Alarm PendingIntents.
                    { _, alarmSupervisorIntent, _, _, _ ->

                        // Get all the supervised Alarm PendingIntents from the Alarm Supervisor Intent.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val currentAlarmIntents =
                                alarmSupervisorIntent.getParcelableArrayListExtra(
                                    CURRENT_ALARMS_PENDING_INTENTS,
                                    PendingIntent::class.java
                                )
                            currentAlarmIntents?.forEachIndexed { index, alarmIntent ->
                                logcat {
                                    "cancelAllAlarms: cancel() item=${
                                        alarmSupervisorIntent.getStringArrayListExtra(CURRENT_ALARMS_TITLES)?.get(index)
                                    }"
                                }

                                // Cancel each Alarm PendingIntent.
                                (alarmIntent as PendingIntent).cancel()
                            }
                        } else {
                            @Suppress("DEPRECATION")
                            val currentAlarmIntents =
                                alarmSupervisorIntent.getParcelableArrayExtra(CURRENT_ALARMS_PENDING_INTENTS)
                            currentAlarmIntents?.forEachIndexed { index, alarmIntent ->
                                logcat {
                                    "cancelAllAlarms: cancel() item=${
                                        alarmSupervisorIntent.getStringArrayListExtra(CURRENT_ALARMS_TITLES)?.get(index)
                                    }"
                                }

                                // Cancel each Alarm PendingIntent.
                                (alarmIntent as PendingIntent).cancel()
                            }
                        }



                        onFinished()
                    },
                    null
                )
                alarmSupervisorPendingIntent.cancel() // runs the "onFinished" Handler Lambda that cancels all supervised Alarm PendingIntents.
            } catch (e: PendingIntent.CanceledException) {
                e.printStackTrace()
            }
        } else
            onFinished()
    }

    //////////////////////////////////////
    ////////// HELPER FUNCTIONS //////////

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private fun createAlarmPendingIntentAndSetAlarm(
        agendaItem: AgendaItem,
        alarmId: Int,
    ): PendingIntent {
        val alarmBroadcastReceiverIntent =
            Intent(context, AlarmBroadcastReceiver::class.java).apply {
                putExtra(ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM, agendaItem as Parcelable)
                putExtra(ALARM_NOTIFICATION_INTENT_EXTRA_ALARM_ID, alarmId)
                action = ALARM_NOTIFICATION_INTENT_ACTION_ALARM_TRIGGER
            }

        val broadcastAlarmPendingIntent =
            PendingIntent.getBroadcast(
                context,
                alarmId,
                alarmBroadcastReceiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val mainActivityIntent = Intent(context, MainActivity::class.java)
        val mainActivityPendingIntent =
            PendingIntent.getActivity(
                context,
                alarmId,
                mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        val alarmClockInfo =
            AlarmManager.AlarmClockInfo(
                agendaItem.remindAt.toEpochMilli(),
                mainActivityPendingIntent
            )

        logcat { "createAgendaItemAlarmPendingIntent ${agendaItem.title}, alarmTime: ${alarmClockInfo.triggerTime.toZonedDateTime()}, alarmId: $alarmId" }

        // Set the Alarm.
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAlarmClock(alarmClockInfo, broadcastAlarmPendingIntent)

        return broadcastAlarmPendingIntent
    }
}


