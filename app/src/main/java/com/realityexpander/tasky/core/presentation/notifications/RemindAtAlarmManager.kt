package com.realityexpander.tasky.core.presentation.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.realityexpander.tasky.MainActivity
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.presentation.common.enums.toAgendaItemType
import com.realityexpander.tasky.core.util.toUtcMillis
import com.realityexpander.tasky.core.util.toZonedDateTime
import logcat.logcat
import java.time.ZonedDateTime
import com.realityexpander.tasky.core.presentation.notifications.RemindAtAlarmNotificationManager as Notifications

interface IRemindAtAlarmManager {

    fun setAlarmsForAgendaItems(
        context: Context,
        agendaItems: List<AgendaItem>
    )

    fun cancelAllAlarms(
        context: Context,
        onFinished: () -> Unit = {},
    )
}

object RemindAtAlarmManager : IRemindAtAlarmManager {

    private const val CURRENT_ALARM_PENDING_INTENTS = "CURRENT_ALARM_PENDING_INTENTS"
    private const val CURRENT_ALARM_TITLES = "CURRENT_ALARM_TITLES"

    private const val REMIND_AT_ALARM_SUPERVISOR_REQUEST_CODE = 0

    override fun setAlarmsForAgendaItems(
        context: Context,
        agendaItems: List<AgendaItem>
    ) {
        // Only include upcoming `Remind At` items (RemindAt is in the future)
        val futureItems =
            agendaItems.filter {
                it.remindAtTime.toUtcMillis() >= ZonedDateTime.now().toUtcMillis()
            }

        if (futureItems.isEmpty()) {
            return
        }

        // Create an Alarm PendingIntent for each AgendaItem's RemindAt time
        val alarmPendingIntents = mutableListOf<PendingIntent>()
        futureItems.forEachIndexed { alarmIndex, agendaItem ->
            val pendingIntent =
                createAlarmPendingIntentForAgendaItem(context, agendaItem, alarmIndex + 1)
            pendingIntent.let {
                alarmPendingIntents.add(it)
            }
        }

        // Save all current Alarm PendingIntents to an "Alarm Supervisor" PendingIntent
        //   that will be used to cancel all alarms, when needed.
        PendingIntent.getBroadcast(
            context,
            REMIND_AT_ALARM_SUPERVISOR_REQUEST_CODE,
            Intent(context, MainActivity::class.java).also {
                it.putExtra(
                    CURRENT_ALARM_PENDING_INTENTS,
                    arrayOf<PendingIntent>(*alarmPendingIntents.toTypedArray())
                )
                it.putStringArrayListExtra( // for debugging
                    CURRENT_ALARM_TITLES,
                    futureItems.map { agendaItem ->
                        when (agendaItem) {
                            is AgendaItem.Event -> agendaItem.title
                            is AgendaItem.Task -> agendaItem.title
                            is AgendaItem.Reminder -> agendaItem.title
                            else -> {
                                "UNKNOWN_AGENDA_ITEM_TYPE"
                            }
                        }
                    }.toList().toCollection(ArrayList())
                )
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    // Based on: https://stackoverflow.com/questions/4315611/android-get-all-pendingintents-set-with-alarmmanager
    override fun cancelAllAlarms(
        context: Context,
        onFinished: () -> Unit,
    ) {
        // Acquire the Alarm Supervisor PendingIntent with all the current Alarms PendingIntents.
        val alarmSupervisorPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_NO_CREATE
        )

        logcat { "cancelAllAlarms alarmSupervisorPendingIntent=$alarmSupervisorPendingIntent" }
        if (alarmSupervisorPendingIntent != null) {
            try {
                // Send the "cancel Alarm" command to each Alarm PendingIntent in the Supervisor PendingIntent.
                alarmSupervisorPendingIntent.send(
                    context,
                    0,
                    null,
                    // This "onFinished" Lambda is called to cancel all supervised Alarm PendingIntents.
                    { _, alarmSupervisorIntent, _, _, _ ->

                        val currentAlarmIntents =
                            alarmSupervisorIntent.getParcelableArrayExtra(CURRENT_ALARM_PENDING_INTENTS)
                        currentAlarmIntents?.forEachIndexed { index, alarmIntent ->
                            logcat { "cancelAllAlarms: cancel() item=${alarmSupervisorIntent.getStringArrayListExtra(CURRENT_ALARM_TITLES)?.get(index)}" }

                            // Cancel each Alarm PendingIntent.
                            (alarmIntent as PendingIntent).cancel()
                        }

                        onFinished()
                    },
                    null
                )
                alarmSupervisorPendingIntent.cancel()
            } catch (e: PendingIntent.CanceledException) {
                e.printStackTrace()
            }
        } else
            onFinished()
    }

    private fun createAlarmPendingIntentForAgendaItem(
        context: Context,
        agendaItem: AgendaItem,
        alarmId: Int,
    ): PendingIntent {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmTime = agendaItem.remindAtTime.toUtcMillis()
        val alarmPendingIntent = createAlarmPendingIntent(context, alarmId, agendaItem)

        logcat { "createAgendaItemAlarmPendingIntent ${agendaItem.title}, alarmTime: ${alarmTime.toZonedDateTime()}, alarmId: $alarmId" }

        // Set the Alarm
//    alarmManager.setExactAndAllowWhileIdle(
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            alarmPendingIntent
        )

        return alarmPendingIntent
    }

    private fun createAlarmPendingIntent(
        context: Context,
        alarmId: Int,
        agendaItem: AgendaItem,
    ): PendingIntent {
        val alarmIntent = Intent(context, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(Notifications.ALARM_NOTIFICATION_INTENT_EXTRA_ALARM_ID, alarmId)
            putExtra(Notifications.ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_ID, agendaItem.id)
            putExtra(Notifications.ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_TYPE, agendaItem.toAgendaItemType().typeNameStr)
            putExtra(Notifications.ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_TITLE, agendaItem.title)
            putExtra(Notifications.ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_DESCRIPTION, agendaItem.description)
            putExtra(Notifications.ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_START_DATETIME_UTC_MILLIS, agendaItem.startTime.toUtcMillis())
            action = Notifications.ALARM_NOTIFICATION_INTENT_ACTION_ALARM_TRIGGER
            setClass(context, MainActivity::class.java)
        }

        logcat("Tasky RemindAt Alarm") { "Set Alarm intent: $alarmIntent, alarmId: $alarmId, remindAtTime: ${agendaItem.remindAtTime}" }

        return PendingIntent.getActivity(
            context,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}


