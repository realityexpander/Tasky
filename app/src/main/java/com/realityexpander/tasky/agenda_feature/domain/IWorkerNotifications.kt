package com.realityexpander.tasky.agenda_feature.domain

import android.app.Notification
import android.graphics.Bitmap
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

interface IWorkerNotifications {

    fun createNotification(
        channelId: String,
        title: String,
        description: String,
        @DrawableRes icon: Int,
        @ColorInt iconTintColor: Int,
        largeIcon: Bitmap?,
    ): Notification

    fun showNotification(
        notification: Notification,
        notificationId: Int
    )

    fun clearNotification(
        notificationId: Int
    )    
}
