package com.realityexpander.tasky.agenda_feature.domain

import android.content.Intent

interface IRemindAtNotificationManager {

    fun showNotification(
        alarmIntent: Intent
    )

    fun createNotificationChannel()
}