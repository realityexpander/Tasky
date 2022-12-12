package com.realityexpander.tasky.core.presentation.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.realityexpander.tasky.agenda_feature.domain.IRemindAtNotificationManager
import com.realityexpander.tasky.core.presentation.notifications.RemindAtNotificationManagerImpl.Companion.ALARM_NOTIFICATION_INTENT_ACTION_ALARM_TRIGGER
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AlarmBroadcastReceiver(
    private val remindAtNotificationManager: IRemindAtNotificationManager
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, alarmIntent: Intent?) {
        alarmIntent ?: return
        context ?: return

        // Guard if the intent is not an "Alarm Trigger" intent
        if (alarmIntent.action != ALARM_NOTIFICATION_INTENT_ACTION_ALARM_TRIGGER) {
            return
        }

        remindAtNotificationManager.showNotification(alarmIntent)
    }
}