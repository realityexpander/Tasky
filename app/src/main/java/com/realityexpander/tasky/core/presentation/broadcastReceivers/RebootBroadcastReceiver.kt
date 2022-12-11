package com.realityexpander.tasky.core.presentation.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.agendaRepositoryImpls.AgendaRepositoryImpl
import com.realityexpander.tasky.core.presentation.notifications.RemindAtAlarmManager.cancelAllAlarms
import com.realityexpander.tasky.core.presentation.notifications.RemindAtAlarmManager.setAlarmsForAgendaItems
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import logcat.logcat
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@AndroidEntryPoint
class RebootBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var repository: AgendaRepositoryImpl

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return

        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                logcat { "RebootBroadcastReceiver: ACTION_BOOT_COMPLETED" }
                CoroutineScope(Main).launch {
                    val agendaItems =
                        repository.getLocalAgendaItemsWithRemindAtInDateTimeRangeFlow(
                            ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS),
                            ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(14)
                        ).first()

                    setAlarmsForAgendaItems(context, agendaItems)
                }
            }
            Intent.ACTION_SHUTDOWN -> {
                cancelAllAlarms(context) // just in case
            }
        }
    }
}
