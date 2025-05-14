package com.realityexpander.tasky

import android.content.Intent
import android.os.Bundle
import android.os.Debug.waitForDebugger
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.realityexpander.tasky.auth_feature.presentation.splash_screen.MainActivityViewModel
import com.realityexpander.tasky.core.presentation.notifications.RemindAtNotificationManagerImpl.Companion.ALARM_NOTIFICATION_INTENT_ACTION_ALARM_TRIGGER
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.core.util.dumpIntentExtras
import com.realityexpander.tasky.destinations.AgendaScreenDestination
import com.realityexpander.tasky.destinations.LoginScreenDestination
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.serialization.InternalSerializationApi
import logcat.logcat
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class,
        ExperimentalMaterialNavigationApi::class,
        InternalSerializationApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        // user-initiated process death: adb shell am force-stop com.realityexpander.tasky
        // system process death: adb shell am kill com.realityexpander.tasky
        // check app running: adb shell ps | grep tasky travel
        if (false) {
            waitForDebugger() // leave for testing process death
        }
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
                    color = Color.Transparent, //colorResource(id = R.color.tasky_green)
                ) {
                    val splashState by viewModel.splashState.collectAsState()
                    val context = LocalContext.current

                    val navController = rememberAnimatedNavController()
                    val navHostEngine = rememberAnimatedNavHostEngine(
                        navHostContentAlignment = Alignment.TopCenter,
                        rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING, // default `rootDefaultAnimations` means no animations
                        defaultAnimationsForNestedNavGraph = mapOf(),
                    )

                    // Load Settings (or initialize them)
                    LaunchedEffect(true) {
                        val appSettings =
                            viewModel.appSettingsRepository.getAppSettings()

                        // Confirm the settings file is created and initialized
                        if (!appSettings.isSettingsInitialized) {
                            viewModel.appSettingsRepository.saveIsSettingsInitialized(true)
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
                            modifier = Modifier
                                .background(MaterialTheme.colors.background)
                                .fillMaxSize(),
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
