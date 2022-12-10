package com.realityexpander.tasky

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.dataStore
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.realityexpander.tasky.agenda_feature.presentation.common.enums.AgendaItemType
import com.realityexpander.tasky.agenda_feature.presentation.common.enums.toAgendaItemType
import com.realityexpander.tasky.auth_feature.presentation.splash_screen.MainActivityViewModel
import com.realityexpander.tasky.core.data.settings.AppSettingsSerializer
import com.realityexpander.tasky.core.data.settings.saveSettingsInitialized
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.core.util.*
import com.realityexpander.tasky.destinations.AgendaScreenDestination
import com.realityexpander.tasky.destinations.LoginScreenDestination
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import logcat.logcat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.system.exitProcess


val Context.dataStore by
dataStore(
    "app-settings.data",
    AppSettingsSerializer(encrypted = true)
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
//        if (false) {
//            waitForDebugger() // leave for testing process death
//        }
        super.onCreate(savedInstanceState)

        createAlarmNotificationChannel()

        // Check for Alarm Intent
        if (intent?.getBooleanExtra("com.realityexpander.tasky.ALARM_TRIGGER", false) == true) {
            logcat { "From onCreate: Alarm Triggered" }
            onNewIntent(intent)
        }

        // Main app
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.splashState.value.isLoading
            }
        }

//        showAlarmNotification("Tasky Title", "TEST Triggered", ZonedDateTime.now().toInstant().toEpochMilli())

        setContent {
            TaskyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.tasky_green)
                ) {
                    val splashState by viewModel.splashState.collectAsState()
                    val context = LocalContext.current

                    val navController = rememberNavController()
                    val navHostEngine = rememberNavHostEngine()

                    // Load Settings (or initialize them)
                    LaunchedEffect(true) {
                        val appSettings = context.dataStore.data.first()

                        // Confirm the settings file is created and initialized
                        if (!appSettings.isSettingsInitialized) {
                            context.dataStore.saveSettingsInitialized(true)
                        }

                        // Set logged-in user Authentication status
                        viewModel.onSetAuthInfo(appSettings.authInfo)
                    }

                    // Display any errors
                    LaunchedEffect(splashState.error) {
                        if (splashState.error != null) {
                            Toast.makeText(context, splashState.error, Toast.LENGTH_LONG).show()
                            delay(1000)
                            viewModel.onSetAuthInfo(null)
                        }
                    }

                    if (!splashState.isLoading) {

                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            navController = navController,
                            engine = navHostEngine,
                            startRoute =
                            if (splashState.authInfo != null) {
                                AgendaScreenDestination
                            } else {
                                LoginScreenDestination
                            },
                        )
                    }
                }
            }
        }
    }

    fun exitApp() {
        finish()
        exitProcess(0)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        logcat { "onNewIntent: $intent" }

        intent?.dumpIntentExtras()

        // Handle Alarm Manager
        if (intent?.action == "com.realityexpander.tasky.ALARM_TRIGGER") {
            showAlarmNotification(
                title = intent.getStringExtra("TITLE") ?: "Tasky",
                description = intent.getStringExtra("DESCRIPTION") ?: "Alarm Triggered",
                fromDateTimeUtcMillis = intent.getLongExtra("FROM_DATETIME_UTC_MILLIS", ZonedDateTime.now().toUtcMillis()),
                agendaItemType = intent.getStringExtra("AGENDA_ITEM_TYPE") ?: "Task",
                itemUuidStr = intent.getStringExtra("AGENDA_ITEM_ID") ?: "",
            )

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(
                PendingIntent.getActivity(this,
                intent.extras?.getInt("ALARM_ID") ?: return,
                    intent,
                    0
            ))
        }
    }

    fun createAlarmNotificationChannel() {
        // Register the channel with the system
        val channel = NotificationChannel(
            "ALARM_NOTIFICATION_CHANNEL",
            "Alarm Notifications",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "For 'Remind At' Alarm Notifications"
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // draw a block of text
    fun Canvas.drawTextBlock(
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

    private fun showAlarmNotification(
        title: String,
        description: String,
        fromDateTimeUtcMillis: Long,
        agendaItemType: String = "Task",
        itemUuidStr: UuidStr = UUID.randomUUID().toString(),
    ) {
        logcat { "showAlarmNotification: $title, $description, from=${fromDateTimeUtcMillis.toZonedDateTime()}" }

        val width = 400
        val height = 300
        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())

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
                    backgroundColor = resources.getColor(R.color.tasky_green, null)
                    textColor = Color.WHITE
                }
                AgendaItemType.Task -> {
                    backgroundColor = Color.LTGRAY
                    textColor = Color.BLACK
                }
                AgendaItemType.Reminder -> {
                    backgroundColor = resources.getColor(R.color.purple_200, null)
                    textColor = Color.WHITE
                }
                else -> resources.getColor(R.color.tasky_green, null)
            }

        // Make rounded corners (Card)
        paint.color = backgroundColor
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 10f
        canvas.drawRoundRect(
            0f, 50f,
            width.toFloat(), height.toFloat()-50f,
            30f, 30f,
            paint
        )

        // Set up for drawing text
        paint.color = textColor
        paint.textSize = 24f
        paint.isAntiAlias = true

        // Draw the description text on the Canvas
        canvas.drawTextBlock(
            "⚪️ $title\n" +
                    "• Starting at ${fromDateTimeUtcMillis.toZonedDateTime()
                        .format(DateTimeFormatter.ofPattern("h:mm a, E MMM d"))}" +"\n\n" +
                    "• $description",
            20f, 70f, width, height, paint
        )

        val notification = NotificationCompat.Builder(this, "ALARM_NOTIFICATION_CHANNEL")
            .setSmallIcon(R.drawable.ic_notification_reminder_foreground)
            .setColor(resources.getColor(R.color.tasky_green, null))
            .setUsesChronometer(true)
            .setChronometerCountDown(true)
            .setShowWhen(true)
            .setWhen(fromDateTimeUtcMillis)
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigPictureStyle().also {
                it.setBigContentTitle(title)
                it.bigPicture(bitmap)
                it.bigLargeIcon(getBitmapFromVectorDrawable(R.drawable.tasky_logo_for_splash))
            })
            .setContentText(
                "$description at " + fromDateTimeUtcMillis.toZonedDateTime().format(
                    DateTimeFormatter.ofPattern("hh:mm a"))
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(Intent(this@MainActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                }.let { intent ->
                    PendingIntent.getActivity(this, 0, intent, 0)
                })
            .setAutoCancel(true)
            .setChannelId("ALARM_NOTIFICATION_CHANNEL")
            .setSubText("Agenda item starting soon")
            .setOnlyAlertOnce(true)
            .build()

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
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
            notify(itemUuidStr.toIntegerHashCodeOfUUIDString(), notification)
        }
    }

    fun Context.getBitmapFromVectorDrawable(@DrawableRes drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(this, drawableId)
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
