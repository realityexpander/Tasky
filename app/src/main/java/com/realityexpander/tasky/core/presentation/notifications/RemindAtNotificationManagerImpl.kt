package com.realityexpander.tasky.core.presentation.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.realityexpander.tasky.BuildConfig
import com.realityexpander.tasky.MainActivity
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.IRemindAtNotificationManager
import com.realityexpander.tasky.agenda_feature.presentation.common.enums.AgendaItemType
import com.realityexpander.tasky.agenda_feature.presentation.common.enums.toAgendaItemType
import com.realityexpander.tasky.agenda_feature.presentation.common.enums.toAgendaItemTypeStr
import com.realityexpander.tasky.core.presentation.broadcastReceivers.CompleteTaskBroadcastReceiver
import com.realityexpander.tasky.core.presentation.util.getBitmapFromVectorDrawable
import com.realityexpander.tasky.core.util.UuidStr
import com.realityexpander.tasky.core.util.toIntegerHashCodeOfUUIDString
import com.realityexpander.tasky.core.util.toUtcMillis
import com.realityexpander.tasky.core.util.toZonedDateTime
import logcat.logcat
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class RemindAtNotificationManagerImpl @Inject constructor(
    val context: Context
) : IRemindAtNotificationManager {

    companion object {
        // Notification Channel
        const val ALARM_NOTIFICATION_CHANNEL_ID =
            "ALARM_NOTIFICATION_CHANNEL_ID"
        private const val ALARM_NOTIFICATION_CHANNEL_NAME =
            "'Remind At' Alarms"
        private const val ALARM_NOTIFICATION_CHANNEL_DESCRIPTION =
            "Alarm Notifications for 'Remind At' setting"

        // Intent Actions
        const val ALARM_NOTIFICATION_INTENT_ACTION_ALARM_TRIGGER =
            "${BuildConfig.APPLICATION_ID}.ALARM_TRIGGER"
        const val ALARM_NOTIFICATION_INTENT_ACTION_COMPLETE_TASK =
            "${BuildConfig.APPLICATION_ID}.COMPLETE_TASK"

        // Intent Extras
        const val ALARM_NOTIFICATION_INTENT_EXTRA_ALARM_ID =
            "ALARM_NOTIFICATION_INTENT_EXTRA_ALARM_ID"
        const val ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM =
            "ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM"
        const val ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ID =
            "ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ID"
    }

    override fun createNotificationChannel() {
        // Register the channel with the system
        val channel = NotificationChannel(
            ALARM_NOTIFICATION_CHANNEL_ID,
            ALARM_NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = ALARM_NOTIFICATION_CHANNEL_DESCRIPTION
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun showNotification(
        alarmIntent: Intent
    ) {
        alarmIntent.apply {
            val agendaItem = getParcelableExtra(ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM) as? AgendaItem

            agendaItem?.let { item ->
                createNotification(
                    alarmId = alarmIntent.getIntExtra(ALARM_NOTIFICATION_INTENT_EXTRA_ALARM_ID, 0),
                    agendaItemTypeStr = item.toAgendaItemTypeStr(),
                    itemUuidStr = item.id,
                    title = item.title,
                    description = item.description,
                    startDateTimeUtcMillis = item.startTime.toUtcMillis()
                )
            }
        }

        // After notification is shown, cancel the alarm.
        clearAlarm(alarmIntent)
    }

    //////////////////////////////////
    ////// HELPER FUNCTIONS //////////

    // Clear this alarm Intent from the AlarmManager
    private fun clearAlarm(alarmIntent: Intent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(
            PendingIntent.getActivity(
                context,
                alarmIntent.getIntExtra(ALARM_NOTIFICATION_INTENT_EXTRA_ALARM_ID, 0),
                alarmIntent,
                0
            )
        )
    }

    private fun createNotification(
        alarmId: Int,
        agendaItemTypeStr: String,
        itemUuidStr: UuidStr = UUID.randomUUID().toString(),
        title: String,
        description: String,
        startDateTimeUtcMillis: Long,
    ) {
        logcat { "showAlarmNotification: $title, $description, startDateTime=${startDateTimeUtcMillis.toZonedDateTime()}" }

        val completeTaskAction: NotificationCompat.Action? =
            createActionForCompleteTask(context, itemUuidStr, alarmId, agendaItemTypeStr)

        val infoCardBitmap = getInfoCardBitmap(
            agendaItemTypeStr,
            title,
            description,
            startDateTimeUtcMillis,
        )

        val notification = NotificationCompat.Builder(context, ALARM_NOTIFICATION_CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setStyle(NotificationCompat.BigPictureStyle().also {
                it.setBigContentTitle(title)
                it.bigPicture(infoCardBitmap)
                it.bigLargeIcon(context.getBitmapFromVectorDrawable(R.drawable.tasky_logo_for_splash))
            })
            .setSmallIcon(R.drawable.ic_notification_reminder_foreground)
            .setColor(context.resources.getColor(R.color.tasky_green, null))
            .setContentTitle(title)
            .setContentText(
                "$description at " + startDateTimeUtcMillis.toZonedDateTime().format(
                    DateTimeFormatter.ofPattern("hh:mm a")
                )
            )
            .setSubText(context.getString(R.string.notifications_alarm_remind_at_subtext, agendaItemTypeStr))
            .setShowWhen(true)
            .setWhen(startDateTimeUtcMillis)
            .setUsesChronometer(true)
            .setChronometerCountDown(true)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .addAction(completeTaskAction)
            .setContentIntent(
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }.let { intent ->
                    PendingIntent.getActivity(
                        context,
                        alarmId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE //0
                    )
                })
            .build()

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }

            logcat { "showAlarmNotification: notify.id=${itemUuidStr.toIntegerHashCodeOfUUIDString()}" }

            notify(
                /* id = */ itemUuidStr.toIntegerHashCodeOfUUIDString(),
                notification
            )
        }
    }

    private fun createActionForCompleteTask(
        context: Context,
        itemUuidStr: UuidStr,
        alarmId: Int,
        agendaItemType: String
    ): NotificationCompat.Action? {
        // Setup action to create "Complete task" button
        val completeTaskIntent =
            Intent(context, CompleteTaskBroadcastReceiver::class.java).apply {
                putExtra(ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ID, itemUuidStr)
                action = ALARM_NOTIFICATION_INTENT_ACTION_COMPLETE_TASK
            }
        val completeTaskPendingIntent: PendingIntent =
            getBroadcast(
                context,
                alarmId,
                completeTaskIntent,
                FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
            )
        val completeTaskAction: NotificationCompat.Action? =
            if (agendaItemType == AgendaItemType.Task.typeNameStr) {
                NotificationCompat.Action
                    .Builder(
                        0,
                        context.getString(R.string.agenda_notifications_complete_task_button),
                        completeTaskPendingIntent
                    ).build()
            } else {
                null
            }

        return completeTaskAction
    }

    private fun getInfoCardBitmap(
        agendaItemTypeStr: String,
        title: String,
        description: String,
        startDateTimeUtcMillis: Long,
    ): Bitmap? {
        val width = 400
        val height = 300

        // Create a Bitmap and a Canvas for the Bitmap
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()

        // Draw the background
        canvas.drawColor(Color.BLACK)

        // Setup colors depending on Card Type
        var backgroundColor = Color.WHITE
        var textColor = Color.BLACK
        when (agendaItemTypeStr.toAgendaItemType()) {
            AgendaItemType.Event -> {
                backgroundColor = context.resources.getColor(R.color.tasky_green, null)
                textColor = Color.WHITE
            }
            AgendaItemType.Task -> {
                backgroundColor = Color.LTGRAY
                textColor = Color.BLACK
            }
            AgendaItemType.Reminder -> {
                backgroundColor = context.resources.getColor(R.color.purple_200, null)
                textColor = Color.WHITE
            }
            else -> context.resources.getColor(R.color.tasky_green, null)
        }

        val offsetYForTask = if (agendaItemTypeStr == AgendaItemType.Task.typeNameStr) 25 else 0

        // Make rounded corners (Card)
        paint.color = backgroundColor
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 10f
        canvas.drawRoundRect(
            0f, 50f + offsetYForTask,
            width.toFloat(), height.toFloat() - (50f + offsetYForTask),
            30f, 30f,
            paint
        )

        // Set up for drawing text
        paint.color = textColor
        paint.textSize = 24f

        val offsetY2ForTask = if (agendaItemTypeStr == AgendaItemType.Task.typeNameStr) 15 else 0

        // Draw the description text on the Canvas
        canvas.drawTextBlock(
            "⏰ $title\n" +
                    "• Starting at ${
                        startDateTimeUtcMillis.toZonedDateTime()
                            .format(DateTimeFormatter.ofPattern("h:mm a, E MMM d"))
                    }\n\n" +
                    "• $description",
            20f,
            70f + offsetY2ForTask,
            width,
            height,
            paint
        )

        return bitmap
    }

    // Draw a block of text that wraps words to the next line if they are too long
    private fun Canvas.drawTextBlock(
        text: String,
        x: Float,
        y: Float,
        width: Int,
        height: Int,
        paint: Paint
    ) {
        val textPaint = TextPaint(paint)
        textPaint.textSize = 20f
        textPaint.isAntiAlias = true

        val textLayout = StaticLayout.Builder
            .obtain(text, 0, text.length, textPaint, width - 30)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
            .build()

        this.save()
        this.translate(x, y)
        textLayout.draw(this)
        this.restore()
    }
}
