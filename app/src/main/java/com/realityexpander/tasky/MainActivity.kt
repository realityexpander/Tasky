package com.realityexpander.tasky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.realityexpander.tasky.data.repository.remote.IAuthApi
import com.realityexpander.tasky.data.repository.remote.authApiImpls.TaskyApi
import com.realityexpander.tasky.destinations.AgendaScreenDestination
import com.realityexpander.tasky.destinations.LoginScreenDestination
import com.realityexpander.tasky.domain.AuthInfo
import com.realityexpander.tasky.domain.IAuthRepository
import com.realityexpander.tasky.presentation.common.UIConstants
import com.realityexpander.tasky.presentation.common.modifiers.largeHeight
import com.realityexpander.tasky.presentation.ui.theme.TaskyTheme
import com.realityexpander.tasky.presentation.util.UiText
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity() : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

//        waitForDebugger() // for testing process death

        super.onCreate(savedInstanceState)

        setContent {
            TaskyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.tasky_green)
                ) {
                    DestinationsNavHost(navGraph = NavGraphs.root)
                }

            }
        }
    }
}

data class SplashState(
    val authInfo: AuthInfo? = null,
    val authInfoChecked: Boolean = false,
    val statusMessage: UiText = UiText.None,
)

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val authInfo: AuthInfo? =
        savedStateHandle[UIConstants.SAVED_STATE_authInfo]
    private val statusMessage: UiText =
        savedStateHandle[UIConstants.SAVED_STATE_statusMessage] ?: UiText.None

        private val _splashState = MutableStateFlow(SplashState())
        val splashState = _splashState.onEach { state ->
            // save state for process death
            // is this needed for splash screen?
            savedStateHandle[UIConstants.SAVED_STATE_authInfo] = state.authInfo
            savedStateHandle[UIConstants.SAVED_STATE_statusMessage] = state.statusMessage

        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SplashState())

        init {
            viewModelScope.launch {
                yield() // allow the splashState to be initialized

                // restore state after process death -- // is this needed for splash screen?
                _splashState.value = SplashState(
                    authInfo = authInfo,
                    authInfoChecked = false,
                    statusMessage = statusMessage,
                )
                yield() // allow the splashState to be restored

                // Check if there is AuthInfo/AuthToken in the AuthRepository
                val authInfo = authRepository.getAuthInfo()

                // set the AuthInfo and AuthToken for this user
                TaskyApplication.authInfoGlobal = authInfo // todo should replace with DataStore?
                IAuthApi.setAuthToken(authInfo?.authToken)

                if(authInfo?.authToken != AuthInfo.NOT_LOGGED_IN.authToken
                    && authRepository.authenticateAuthInfo(authInfo)
                ) {
                    _splashState.value = SplashState(
                        authInfo = authInfo,
                        authInfoChecked = true,
                        statusMessage = UiText.None, //UiText.Res(R.string.splash_logged_in)
                    )
                } else {
                    _splashState.value = SplashState(
                        authInfo = AuthInfo.NOT_LOGGED_IN,
                        authInfoChecked = true,
                        statusMessage = UiText.None, //UiText.Res(R.string.splash_not_logged_in)
                    )
                }
            }
        }
}

@Composable
@Destination
@RootNavGraph(start = true)
fun SplashScreen(
    navigator: DestinationsNavigator,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val splashState by viewModel.splashState.collectAsState()
    val scope = rememberCoroutineScope()

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
                delay(1000) // show logo for 1 second before going to login screen

                navigator.navigate(
                    LoginScreenDestination(
                        username = "Chris Athanas",     // TESTING ONLY
                        email = "chris@demo.com",       // TESTING ONLY
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
                        IntOffset(0, -10) } // slight difference in position between Android Theme and this composable when centering logo
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