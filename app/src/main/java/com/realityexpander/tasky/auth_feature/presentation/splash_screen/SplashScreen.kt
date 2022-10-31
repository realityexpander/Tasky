package com.realityexpander.tasky.auth_feature.presentation.splash_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.realityexpander.tasky.R
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.common.settings.AppSettings
import com.realityexpander.tasky.core.common.settings.setSettingsLoaded
import com.realityexpander.tasky.core.presentation.common.modifiers.largeHeight
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.dataStore
import com.realityexpander.tasky.destinations.AgendaScreenDestination
import com.realityexpander.tasky.destinations.LoginScreenDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
@Destination
@RootNavGraph(start = true)
fun SplashScreen(
    navigator: DestinationsNavigator,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val splashState by viewModel.splashState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val appSettings = context.dataStore.data.collectAsState( // .data is a Flow
        initial = AppSettings()
    ).value

    // Load settings from data store
    LaunchedEffect(key1 = appSettings) {
        // ignore first data (initial emit is always default state, due to it being a flow)
        if(!appSettings.settingsLoaded) {
            context.dataStore.setSettingsLoaded(true)
            return@LaunchedEffect
        }

        // Settings data is now loaded, so we can set if user is logged-in (authInfo != null)
        viewModel.onSetAuthInfo(appSettings.authInfo)
    }

    // After settings are Loaded (or not) - Navigate to correct screen
    if (splashState.authInfoChecked) {
        splashState.authInfo?.authToken?.let { authToken ->
            if (authToken != AuthInfo.NOT_LOGGED_IN.authToken) {
                navigator.navigate(
                    AgendaScreenDestination(
//                         authInfo = splashState.authInfo // todo add authInfo to agenda screen
                    )
                )
            }
        } ?: run {
            scope.launch {
                delay(500) // show logo for .5 second before going to login screen

                navigator.navigate(
                    LoginScreenDestination(
                        username = "Chris Athanas",     // TESTING ONLY
                        email = "chris3@demo.com",      // TESTING ONLY
                        password = "Password1",         // TESTING ONLY
                        confirmPassword = "Password1",  // TESTING ONLY
                    )
                ) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    SplashScreenContent(
        statusMessage = splashState.statusMessage,
    )
}

@Composable
fun SplashScreenContent(
    statusMessage: UiText,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.tasky_green)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.tasky_logo),
                contentDescription = "Tasky Logo",
                modifier = Modifier
                    .width(200.dp)
                    .height(200.dp)
                    .offset {
                        IntOffset(0, -10)
                    } // slight difference in position between Android Theme and this composable when centering logo
            )
            Spacer(modifier = Modifier.largeHeight())

            Text(
                text = statusMessage.get,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.onSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    TaskyTheme {
        SplashScreenContent(
            statusMessage = UiText.Res(R.string.splash_logged_in)
        )
    }
}