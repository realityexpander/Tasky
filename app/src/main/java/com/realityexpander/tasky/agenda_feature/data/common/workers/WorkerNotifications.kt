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
import javax.inject.Inject

class WorkerNotificationsImpl @Inject constructor(
    val context: Context
): IWorkerNotifications {

    override fun createNotificationChannel(
        channelId: String,
        channelDescription: String
    ) {
        val channel = NotificationChannel(
            channelId,
            channelDescription,
            NotificationManager.IMPORTANCE_LOW,
        )
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    override fun createNotification(
        channelId: String,
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

    override fun showNotification(
        notification: Notification,
        notificationId: Int
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    override fun clearNotification(
        notificationId: Int
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }
}