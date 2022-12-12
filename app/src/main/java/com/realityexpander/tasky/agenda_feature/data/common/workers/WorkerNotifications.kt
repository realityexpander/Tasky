package com.realityexpander.tasky.agenda_feature.data.common.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat

object WorkerNotifications {

    fun createNotificationChannel(
        context: Context,
        channel: String,
        channelDescription: String
    ) {
        val channel = NotificationChannel(
            channel,
            channelDescription,
            NotificationManager.IMPORTANCE_LOW,
        )
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    fun createNotification(
        context: Context,
        title: String,
        description: String,
        @DrawableRes icon: Int,
        @ColorInt iconTintColor: Int,
        largeIcon: Bitmap?,
    ): Notification {
        return NotificationCompat.Builder(context, WORKER_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(icon)
            .setColor(iconTintColor)
            .setLargeIcon(largeIcon)
            .setAutoCancel(true)
            .build()
    }

    fun showNotification(
        context: Context,
        notification: Notification,
        notificationId: Int
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    fun clearNotification(
        context: Context,
        notificationId: Int
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }
}