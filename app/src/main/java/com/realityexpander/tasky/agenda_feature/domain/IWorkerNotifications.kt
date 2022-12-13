package com.realityexpander.tasky.agenda_feature.domain

import android.app.Notification
import android.graphics.Bitmap
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

interface IWorkerNotifications {

    fun showNotification(
        channelId: String,
        notificationId: Int,
        title: String,
        description: String,
        @DrawableRes icon: Int,
        @ColorInt iconTintColor: Int,
        largeIcon: Bitmap?,
    )

    fun createNotification(
        channelId: String,
        title: String,
        description: String,
        @DrawableRes icon: Int,
        @ColorInt iconTintColor: Int,
        largeIcon: Bitmap?,
    ): Notification

    fun clearNotification(
        notificationId: Int
    )    
}
