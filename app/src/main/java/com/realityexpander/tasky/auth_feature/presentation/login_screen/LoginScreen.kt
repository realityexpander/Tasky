package com.realityexpander.tasky.auth_feature.presentation.login_screen

import android.content.res.Configuration
import android.view.ViewTreeObserver
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.realityexpander.tasky.MainActivity
import com.realityexpander.tasky.R
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.auth_feature.presentation.components.EmailField
import com.realityexpander.tasky.auth_feature.presentation.components.PasswordField
import com.realityexpander.tasky.core.common.settings.saveAuthInfo
import com.realityexpander.tasky.core.presentation.common.modifiers.*
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.dataStore
import com.realityexpander.tasky.destinations.AgendaScreenDestination
import com.realityexpander.tasky.destinations.RegisterScreenDestination
import kotlinx.coroutines.launch

@Composable
@Destination
fun LoginScreen(
    username: String? = null,
    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
    email: String? = null,
    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
    password: String? = null,
    confirmPassword: String? = null,
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val loginState by viewModel.loginState.collectAsState()

    LoginScreenContent(
        username = username,                // passed to/from RegisterScreen (not used in LoginScreen)
        confirmPassword = confirmPassword,  // passed to/from RegisterScreen (not used in LoginScreen)
        state = loginState,
        onAction = viewModel::sendEvent,
        navigator = navigator,
    )

}

@Composable
fun LoginScreenContent(
    username: String? = null,
    confirmPassword: String? = null,
    state: LoginState,
    onAction: (LoginEvent) -> Unit,
    navigator: DestinationsNavigator,
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun performLogin() {
        onAction(LoginEvent.Login(
            email = state.email,
            password = state.password,
        ))
        focusManager.clearFocus()
    }

    fun navigateToRegister() {
        navigator.navigate(
            RegisterScreenDestination(
                username = username,
                email = state.email,
                password = state.password,
                confirmPassword = confirmPassword
            )
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToAgenda(authInfo: AuthInfo) {
        scope.launch {

            // Save the AuthInfo in the dataStore
            context.dataStore.saveAuthInfo(authInfo)

            navigator.navigate(
                AgendaScreenDestination(
                    // authInfo = authInfo
                )
            ) {
                launchSingleTop = true
                restoreState = true
            }
        }
    }


    BackHandler(true) {
        // todo: should we ask the user to quit?
        (context as MainActivity).exitApp()
    }

    // Check keyboard open/closed (how to make this a function?)
    val view = LocalView.current
    var isKeyboardOpen by remember { mutableStateOf(false) }
    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            isKeyboardOpen = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.onSurface)
    ) col1@ {
        Spacer(modifier = Modifier.largeHeight())
        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.surface,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.mediumHeight())

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .taskyScreenTopCorners(color = MaterialTheme.colors.surface)
                .weight(1f)
        ) {
            Spacer(modifier = Modifier.mediumHeight())

            // • EMAIL
            EmailField(
                value = state.email,
                label = null,
                isError = state.isInvalidEmail,
                onValueChange = {
                    onAction(LoginEvent.UpdateEmail(it))
                }
            )
            if (state.isInvalidEmail && state.isShowInvalidEmailMessage) {
                Text(text = stringResource(R.string.error_invalid_email), color = Color.Red)
            }
            Spacer(modifier = Modifier.smallHeight())

            // • PASSWORD
            PasswordField(
                value = state.password,
                label = null,
                isError = state.isInvalidPassword,
                onValueChange = {
                    onAction(LoginEvent.UpdatePassword(it))
                },
                isPasswordVisible = state.isPasswordVisible,
                clickTogglePasswordVisibility = {
                    onAction(
                        LoginEvent.SetIsPasswordVisible(!state.isPasswordVisible)
                    )
                },
                imeAction = ImeAction.Done,
                doneAction = {
                    performLogin()
                }
            )
            if (state.isInvalidPassword && state.isShowInvalidPasswordMessage) {
                Text(text = stringResource(R.string.error_invalid_password), color = Color.Red)
            }
            Spacer(modifier = Modifier.mediumHeight())

            // • LOGIN BUTTON
            Button(
                onClick = {
                    performLogin()
                },
                modifier = Modifier
                    .taskyWideButton(color = MaterialTheme.colors.primary)
                    .align(alignment = Alignment.CenterHorizontally),
                enabled = !state.isLoading,
            ) {
                Text(
                    text = stringResource(R.string.login_button),
                    fontSize = MaterialTheme.typography.button.fontSize,
                )
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(start = DP.small)
                            .size(DP.small)
                            .align(alignment = CenterVertically)
                    )
                }
            }
            Spacer(modifier = Modifier.mediumHeight())

            // STATUS //////////////////////////////////////////

            state.errorMessage.getOrNull?.let { errorMessage ->
                Text(
                    text = "Error: $errorMessage",
                    color = Color.Red,
                )
                Spacer(modifier = Modifier.extraSmallHeight())
            }
            if (state.isLoggedIn) {
                //Text(text = stringResource(R.string.login_logged_in)) // keep for debugging
                //Spacer(modifier = Modifier.extraSmallHeight())

                state.authInfo?.let { authInfo ->
                    navigateToAgenda(authInfo)
                } ?: run {
                    onAction(LoginEvent.LoginError(UiText.Res(R.string.error_login_error, "authInfo is null")))
                }
            }
            state.statusMessage.getOrNull?.let { message ->
                Text(text = message)
                Spacer(modifier = Modifier.extraSmallHeight())
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(color = MaterialTheme.colors.surface)
                    .padding(bottom = DP.large)
            ) {
                this@col1.AnimatedVisibility(
                    visible = !isKeyboardOpen,
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { it }
                    ),
                    exit = fadeOut(),
                    modifier = Modifier
                        .background(color = MaterialTheme.colors.surface)
                        .align(alignment = Alignment.BottomCenter)
                ) {
                    // • REGISTER TEXT BUTTON
                    Text(
                        text = stringResource(R.string.login_not_a_member_sign_up),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.primaryVariant,
                        modifier = Modifier
                            .align(alignment = Alignment.BottomCenter)
                            .clickable(onClick = {
                                navigateToRegister()
                            })
                    )
                }
            }
        }
    }
}

@Composable
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "Night mode=true"
)
fun LoginScreenPreview() {
    TaskyTheme {
        Surface {
            LoginScreenContent(
                navigator = EmptyDestinationsNavigator,
                username = "NOT_USED_IN_THIS_SCREEN_UI",
                confirmPassword = "NOT_USED_IN_THIS_SCREEN_UI",
                state = LoginState(
                    email = "chris@demo.com",
                    password = "123456Aa",
                    isInvalidEmail = false,
                    isInvalidPassword = false,
                    isShowInvalidEmailMessage = false,
                    isShowInvalidPasswordMessage = true,
                    isPasswordVisible = true,
                    isLoading = false,
                    isLoggedIn = false,
                    errorMessage = UiText.None,  // UiText.Res(R.string.error_invalid_email),
                    statusMessage = UiText.None, // UiText.Res(R.string.login_logged_in),
                ),
                onAction = {},
            )
        }
    }
}


@Composable
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    group = "Night mode=false"
)
fun LoginScreenPreview_NightMode() {
    LoginScreenPreview()
}
