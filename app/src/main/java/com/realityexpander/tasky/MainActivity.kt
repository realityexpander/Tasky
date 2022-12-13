package com.realityexpander.tasky

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.dataStore
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.realityexpander.tasky.auth_feature.presentation.splash_screen.MainActivityViewModel
import com.realityexpander.tasky.core.data.settings.AppSettingsSerializer
import com.realityexpander.tasky.core.data.settings.saveSettingsInitialized
import com.realityexpander.tasky.core.presentation.notifications.RemindAtNotificationManagerImpl.Companion.ALARM_NOTIFICATION_INTENT_ACTION_ALARM_TRIGGER
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.core.util.dumpIntentExtras
import com.realityexpander.tasky.destinations.AgendaScreenDestination
import com.realityexpander.tasky.destinations.LoginScreenDestination
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import logcat.logcat
import kotlin.system.exitProcess


val Context.dataStore by
dataStore(
    "app-settings.data",
    AppSettingsSerializer(encrypted = true)
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    var isAlreadyRefreshed = false

    override fun onCreate(savedInstanceState: Bundle?) {
//        if (true) {
//            waitForDebugger() // leave for testing process death
//        }
        super.onCreate(savedInstanceState)

        // Check for Alarm Intent
        if (intent?.action == ALARM_NOTIFICATION_INTENT_ACTION_ALARM_TRIGGER) {
            logcat { "From onCreate: Alarm Triggered" }
            onNewIntent(intent)
        }

        // Main app
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.splashState.value.isLoading
            }
        }

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

        // Handle Alarm Notification
        viewModel.onIntentReceived(intent)
    }

}
