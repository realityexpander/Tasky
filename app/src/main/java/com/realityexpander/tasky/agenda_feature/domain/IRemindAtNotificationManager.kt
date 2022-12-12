package com.realityexpander.tasky.agenda_feature.domain

import android.content.Context
import android.content.Intent

interface IRemindAtNotificationManager {

    fun showNotification(
        context: Context,
        alarmIntent: Intent
    )

    fun createNotificationChannel(context: Context)
}