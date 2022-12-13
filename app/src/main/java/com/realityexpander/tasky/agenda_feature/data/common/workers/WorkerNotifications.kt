package com.realityexpander.tasky.agenda_feature.data.common.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import com.realityexpander.tasky.agenda_feature.domain.IWorkerNotifications

class WorkerNotificationsImpl(val context: Context): IWorkerNotifications {

    init {
        createNotificationChannel()
    }

    override fun showNotification(
        channelId: String,
        notificationId: Int,
        title: String,
        description: String,
        @DrawableRes icon: Int,
        @ColorInt iconTintColor: Int,
        largeIcon: Bitmap?,
    ) {
        val notification =
            createNotification(
                channelId,
                title,
                description,
                icon,
                iconTintColor,
                largeIcon
            )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    override fun createNotification(
        channelId: String,
        title: String,
        description: String,
        @DrawableRes icon: Int,
        @ColorInt iconTintColor: Int,
        largeIcon: Bitmap?,
    ): Notification {
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(icon)
            .setColor(iconTintColor)
            .setLargeIcon(largeIcon)
            .setAutoCancel(true)
            .build()
    }

    override fun clearNotification(
        notificationId: Int
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    /////////////////////////////
    //// HELPERS ////////////////

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            WORKER_NOTIFICATION_CHANNEL_ID,
            WORKER_NOTIFICATION_CHANNEL_DESCRIPTION,
            NotificationManager.IMPORTANCE_LOW,
        )
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}