package com.realityexpander.tasky.core.presentation.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.agendaRepositoryImpls.AgendaRepositoryImpl
import com.realityexpander.tasky.core.presentation.notifications.RemindAtNotificationManagerImpl
import com.realityexpander.tasky.core.presentation.notifications.RemindAtNotificationManagerImpl.ALARM_NOTIFICATION_INTENT_ACTION_COMPLETE_TASK
import com.realityexpander.tasky.core.util.UuidStr
import com.realityexpander.tasky.core.util.toIntegerHashCodeOfUUIDString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CompleteTaskBroadcastReceiver : BroadcastReceiver() {
    @Inject lateinit var repository: AgendaRepositoryImpl

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return
        if(intent.action != ALARM_NOTIFICATION_INTENT_ACTION_COMPLETE_TASK) return

        val agendaItemId = intent.getStringExtra(RemindAtNotificationManagerImpl.ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ID) as? UuidStr
        agendaItemId ?: return

        // If AgendaItem.Task, Set `isDone` to true
        agendaItemId.let { uuidStr ->
            CoroutineScope(IO).launch {
                agendaItemId.let {
                    val task = repository.getTask(uuidStr)
                    task?.let {
                        repository.updateTask(task.copy(isDone = true))
                    }
                }
            }
        }

        // Cancel the notification
        NotificationManagerCompat.from(context).cancel(null, agendaItemId.toIntegerHashCodeOfUUIDString())
    }
}