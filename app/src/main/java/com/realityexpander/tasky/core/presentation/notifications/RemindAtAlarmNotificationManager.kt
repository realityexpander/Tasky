package com.realityexpander.tasky.core.presentation.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import com.realityexpander.tasky.agenda_feature.presentation.common.enums.AgendaItemType
import com.realityexpander.tasky.agenda_feature.presentation.common.enums.toAgendaItemType
import com.realityexpander.tasky.core.presentation.util.getBitmapFromVectorDrawable
import com.realityexpander.tasky.core.util.UuidStr
import com.realityexpander.tasky.core.util.toIntegerHashCodeOfUUIDString
import com.realityexpander.tasky.core.util.toUtcMillis
import com.realityexpander.tasky.core.util.toZonedDateTime
import logcat.logcat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*



interface IRemindAtAlarmNotificationManager {

    fun showNotification(
        context: Context,
        alarmIntent: Intent
    )

    fun createNotificationChannel(context: Context)
}

object RemindAtAlarmNotificationManager : IRemindAtAlarmNotificationManager {
    private const val ALARM_NOTIFICATION_CHANNEL_ID =
        "ALARM_NOTIFICATION_CHANNEL_ID"
    private const val ALARM_NOTIFICATION_CHANNEL_NAME =
        "'Remind At' Alarms"
    private const val ALARM_NOTIFICATION_CHANNEL_DESCRIPTION =
        "Alarm Notifications for 'Remind At' setting"

    const val ALARM_NOTIFICATION_INTENT_ACTION_ALARM_TRIGGER =
        "${BuildConfig.APPLICATION_ID}.ALARM_TRIGGER"
    const val ALARM_NOTIFICATION_INTENT_EXTRA_ALARM_ID =
        "ALARM_NOTIFICATION_INTENT_EXTRA_ALARM_ID"
    const val ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_ID =
        "ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_ID"
    const val ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_TYPE =
        "ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_TYPE"
    const val ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_TITLE =
        "ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_TITLE"
    const val ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_DESCRIPTION =
        "ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_DESCRIPTION"
    const val ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_START_DATETIME_UTC_MILLIS =
        "ALARM_NOTIFICATION_INTENT_EXTRA_START_DATETIME_UTC_MILLIS"

    override fun createNotificationChannel(context: Context) {
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
        context: Context,
        alarmIntent: Intent
    ) {
        alarmIntent.apply {
            showNotification(
                context,
                itemUuidStr = getStringExtra(ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_ID)
                    ?: "",
                agendaItemType = getStringExtra(ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_TYPE)
                    ?: "Task",
                title = getStringExtra(ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_TITLE)
                    ?: "Tasky",
                description = getStringExtra(ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_DESCRIPTION)
                    ?: "Alarm Triggered",
                startDateTimeUtcMillis = getLongExtra(
                    ALARM_NOTIFICATION_INTENT_EXTRA_AGENDA_ITEM_START_DATETIME_UTC_MILLIS,
                    ZonedDateTime.now().toUtcMillis()
                ),
            )
        }
        clearAlarm(context, alarmIntent)
    }

    ////// HELPER FUNCTIONS //////////

    // Clear this alarm Intent from the AlarmManager
    private fun clearAlarm(context: Context, alarmIntent: Intent) {
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

    private fun showNotification(
        context: Context,
        itemUuidStr: UuidStr = UUID.randomUUID().toString(),
        agendaItemType: String = "Task",
        title: String,
        description: String,
        startDateTimeUtcMillis: Long,
    ) {
        logcat { "showAlarmNotification: $title, $description, from=${startDateTimeUtcMillis.toZonedDateTime()}" }

        val bitmap = getInfoCardBitmap(
            agendaItemType,
            context,
            title,
            startDateTimeUtcMillis,
            description
        )

        val notification = NotificationCompat.Builder(context, ALARM_NOTIFICATION_CHANNEL_ID)
            .setChannelId(ALARM_NOTIFICATION_CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigPictureStyle().also {
                it.setBigContentTitle(title)
                it.bigPicture(bitmap)
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
            .setSubText(context.getString(R.string.notifications_alarm_remind_at_subtext))
            .setShowWhen(true)
            .setWhen(startDateTimeUtcMillis)
            .setUsesChronometer(true)
            .setChronometerCountDown(true)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setContentIntent(Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
            }.let { intent ->
                PendingIntent.getActivity(context, 0, intent, 0)
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

    private fun getInfoCardBitmap(
        agendaItemType: String,
        context: Context,
        title: String,
        fromDateTimeUtcMillis: Long,
        description: String
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
        when (agendaItemType.toAgendaItemType()) {
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

        // Make rounded corners (Card)
        paint.color = backgroundColor
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 10f
        canvas.drawRoundRect(
            0f, 50f,
            width.toFloat(), height.toFloat() - 50f,
            30f, 30f,
            paint
        )

        // Set up for drawing text
        paint.color = textColor
        paint.textSize = 24f

        // Draw the description text on the Canvas
        canvas.drawTextBlock(
            "🔘 $title\n" +
                    "• Starting at ${
                        fromDateTimeUtcMillis.toZonedDateTime()
                            .format(DateTimeFormatter.ofPattern("h:mm a, E MMM d"))
                    }" + "\n\n" +
                    "• $description",
            20f, 70f, width, height, paint
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
