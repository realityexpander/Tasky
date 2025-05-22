package com.realityexpander.tasky.auth_feature.presentation.login_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.realityexpander.tasky.core.util.internetConnectivityObserver.IInternetConnectivityObserver
import com.realityexpander.tasky.core.util.internetConnectivityObserver.InternetAvailabilityIndicator

@Composable
@Destination
@RootNavGraph(start = true) // Known bug that interferes with Compose Previews & Code Analysis, so preview are in LoginScreenContent.kt
fun LoginScreen(
    username: String? = "Chris Athanas",
    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
    email: String? = "chris3@demo.com",
    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
    password: String? = "Password1",
    confirmPassword: String? = "Password1",
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val loginState by viewModel.loginState.collectAsState()
    val connectivityState by viewModel.onlineState.collectAsState(
        initial = IInternetConnectivityObserver.OnlineStatus.OFFLINE // must start as Offline
    )
    val appSettingsRepository = viewModel.appSettingsRepository

    LoginScreenContent(
        username = username,                // passed to/from RegisterScreen (not used in LoginScreen)
        confirmPassword = confirmPassword,  // passed to/from RegisterScreen (not used in LoginScreen)
        state = loginState,
        onAction = viewModel::sendEvent,
        navigator = navigator,
        appSettingsRepository = appSettingsRepository,
    )

    InternetAvailabilityIndicator(connectivityState)
}
