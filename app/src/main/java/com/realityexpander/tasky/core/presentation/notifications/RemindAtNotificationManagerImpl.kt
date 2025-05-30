package com.realityexpander.tasky.core.presentation.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getBroadcast
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
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
import com.realityexpander.tasky.core.presentation.broadcastReceivers.CompleteTaskBroadcastReceiver
import com.realityexpander.tasky.core.presentation.util.getBitmapFromVectorDrawable
import com.realityexpander.tasky.core.util.UuidStr
import com.realityexpander.tasky.core.util.toEpochMilli
import com.realityexpander.tasky.core.util.toIntegerHashCodeOfUUIDString
import com.realityexpander.tasky.core.util.toZonedDateTime
import logcat.logcat
import java.time.format.DateTimeFormatter
import java.util.UUID
import androidx.core.graphics.createBitmap

class RemindAtNotificationManagerImpl(val context: Context) : IRemindAtNotificationManager {

    init {
        createNotificationChannel()
    }

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

    override fun showNotification(
        alarmIntent: Intent
    ) {
        alarmIntent.apply {
            val agendaItem =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    getParcelableExtra(
                        ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM,
                        AgendaItem::class.java
                    )
                } else
                    @Suppress("DEPRECATION")
                    getParcelableExtra(ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM) as? AgendaItem?

            agendaItem?.let { item ->
                createNotification(
                    alarmId = alarmIntent.getIntExtra(ALARM_NOTIFICATION_INTENT_EXTRA_ALARM_ID, 0),
                    agendaItem = item,
                    itemUuidStr = item.id,
                    title = item.title,
                    description = item.description,
                    startDateTimeUtcMillis = item.startTime.toEpochMilli()
                )
            }
        }

        // After notification is shown, cancel the alarm.
        clearAlarm(alarmIntent)
    }

    //////////////////////////////////
    ////// HELPER FUNCTIONS //////////

    private fun createNotificationChannel() {
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

    // Clear this alarm Intent from the AlarmManager
    private fun clearAlarm(alarmIntent: Intent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        @Suppress("RemoveRedundantQualifierName") // Make PendingIntent explicit
        alarmManager.cancel(
            PendingIntent.getActivity(
                context,
                alarmIntent.getIntExtra(ALARM_NOTIFICATION_INTENT_EXTRA_ALARM_ID, 0),
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    private fun createNotification(
        alarmId: Int,
        agendaItem: AgendaItem,
        itemUuidStr: UuidStr = UUID.randomUUID().toString(),
        title: String,
        description: String,
        startDateTimeUtcMillis: Long,
    ) {
        logcat { "showAlarmNotification: $title, $description, startDateTime=${startDateTimeUtcMillis.toZonedDateTime()}" }

        val completeTaskAction: NotificationCompat.Action? =
            createActionForCompleteTask(context, itemUuidStr, alarmId, agendaItem)

        val infoCardBitmap = createInfoCardBitmap(
            agendaItem,
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
            .setSubText(context.getString(R.string.notifications_alarm_remind_at_subtext,
                agendaItem.toAgendaItemType().typeNameStr
            ))
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
                    @Suppress("RemoveRedundantQualifierName") // Make PendingIntent explicit
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
        agendaItem: AgendaItem
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
            if (agendaItem.toAgendaItemType() == AgendaItemType.Task) {
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

    @Suppress("VariableInitializerIsRedundant")  // for backgroundColor = Color.WHITE
    private fun createInfoCardBitmap(
        agendaItem: AgendaItem,
        title: String,
        description: String,
        startDateTimeUtcMillis: Long,
    ): Bitmap? {
        val width = 400
        val height = 300

        // Create a Bitmap and a Canvas for the Bitmap
        // val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        val paint = Paint()

        // Draw the background
        canvas.drawColor(Color.BLACK)

        // Setup colors depending on Card Type
        var backgroundColor = Color.WHITE
        var textColor = Color.BLACK
        when (agendaItem.toAgendaItemType()) {
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
            //else -> context.resources.getColor(R.color.tasky_green, null)
        }

        val offsetYForTask = if (agendaItem.toAgendaItemType() == AgendaItemType.Task) 25 else 0

        // Make rounded corner Card
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
        val offsetY2ForTask = if (agendaItem.toAgendaItemType() == AgendaItemType.Task) 15 else 0
        val descriptionText = description.ifBlank { "<No description>" }

        // Draw the `title`, `startTime` & `description` text
        canvas.drawTextBlock(
            "⏰ $title\n" +
                    "• Starting at ${
                        startDateTimeUtcMillis.toZonedDateTime()
                            .format(DateTimeFormatter.ofPattern("h:mm a, E MMM d"))
                    }\n\n" +
                    "• $descriptionText",
            agendaItem.toAgendaItemType().typeNameStr,
            20f,
            70f + offsetY2ForTask,
            width,
            paint
        )

        return bitmap
    }

    // Draw a block of text that wraps words to the next line if they are too long
    private fun Canvas.drawTextBlock(
        text: String,
        agendaItemTypeStr: String,
        x: Float,
        y: Float,
        width: Int,
        paint: Paint
    ) {
        val textPaint = TextPaint(paint)
        textPaint.textSize = 20f
        textPaint.isAntiAlias = true
        val paddingEnd = 40

        val textLayout = StaticLayout.Builder
            .obtain(text, 0, text.length, textPaint, width - paddingEnd)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
            .setEllipsizedWidth(width - paddingEnd)
            .setMaxLines(
                if (agendaItemTypeStr.toAgendaItemType() == AgendaItemType.Task) 5 else 7
            )
            .setEllipsize(TextUtils.TruncateAt.END)
            .build()

        logcat { "textLayout.height = ${textLayout.height}" }
        this.save()
        this.translate(x, y)
        textLayout.draw(this)
        this.restore()
    }
}
