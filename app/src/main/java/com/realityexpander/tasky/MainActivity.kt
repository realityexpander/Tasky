package com.realityexpander.tasky

import android.content.Context
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
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.destinations.AgendaScreenDestination
import com.realityexpander.tasky.destinations.LoginScreenDestination
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
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

                        // Set user logged-in status
                        viewModel.onSetAuthInfo(appSettings.authInfo)
                    }

                    if (!splashState.isLoading) {

                        // Check for errors
                        if (splashState.error != null) {
                            Toast.makeText(context, splashState.error, Toast.LENGTH_LONG).show()
                            Thread.sleep(1000)
                            viewModel.onSetAuthInfo(null)
                        }

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
}
