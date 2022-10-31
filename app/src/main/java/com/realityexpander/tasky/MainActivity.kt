package com.realityexpander.tasky

import android.content.Context
import android.os.Bundle
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
import com.ramcosta.composedestinations.DestinationsNavHost
import com.realityexpander.tasky.auth_feature.presentation.splash_screen.SplashScreenViewModel
import com.realityexpander.tasky.core.common.settings.AppSettings
import com.realityexpander.tasky.core.common.settings.AppSettingsSerializer
import com.realityexpander.tasky.core.common.settings.setSettingsInitialized
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.destinations.AgendaScreenDestination
import com.realityexpander.tasky.destinations.LoginScreenDestination
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

val Context.dataStore by dataStore("app-settings.json", AppSettingsSerializer)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SplashScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

//        waitForDebugger() // for testing process death

        super.onCreate(savedInstanceState)

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

                    val appSettings = context.dataStore.data.collectAsState( // .data is a Flow
                        initial = AppSettings()
                    ).value

                    // Load settings from data store
                    LaunchedEffect(key1 = appSettings) {
                        // ignore first data (initial emit is always default state, due to it being a flow)
                        if (!appSettings.isSettingsInitialized) {
                            context.dataStore.setSettingsInitialized(true)
                            return@LaunchedEffect
                        }

                        // Settings data is now loaded, so we can set if user is logged-in (authInfo != null)
                        viewModel.onSetAuthInfo(appSettings.authInfo)
                    }

                    if (!splashState.isLoading) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
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
}