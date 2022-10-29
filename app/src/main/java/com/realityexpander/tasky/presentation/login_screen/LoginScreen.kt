package com.realityexpander.tasky.presentation.login_screen

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
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.realityexpander.tasky.ExitActivity
import com.realityexpander.tasky.R
import com.realityexpander.tasky.destinations.RegisterScreenDestination
import com.realityexpander.tasky.presentation.common.modifiers.*
import com.realityexpander.tasky.presentation.components.EmailField
import com.realityexpander.tasky.presentation.components.PasswordField
import com.realityexpander.tasky.presentation.ui.theme.TaskyTheme
import com.realityexpander.tasky.presentation.util.UiText

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
        loginState = loginState,
        onAction = viewModel::sendEvent,
        navigator = navigator,
    )

}

@Composable
fun LoginScreenContent(
    username: String? = null,
    confirmPassword: String? = null,
    loginState: LoginState,
    onAction: (LoginEvent) -> Unit,
    navigator: DestinationsNavigator,
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    fun performLogin() {
        onAction(LoginEvent.Login(
            email = loginState.email,
            password = loginState.password,
        ))
        focusManager.clearFocus()
    }

    fun navigateToRegister() {
        navigator.navigate(
            RegisterScreenDestination(
                username = username,
                email = loginState.email,
                password = loginState.password,
                confirmPassword = confirmPassword
            )
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    BackHandler(true) {
        // todo: should we ask the user to quit?
        ExitActivity.exit(context)
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
                value = loginState.email,
                label = null,
                isError = loginState.isInvalidEmail,
                onValueChange = {
                    onAction(LoginEvent.UpdateEmail(it))
                }
            )
            if (loginState.isInvalidEmail && loginState.isShowInvalidEmailMessage) {
                Text(text = stringResource(R.string.error_invalid_email), color = Color.Red)
            }
            Spacer(modifier = Modifier.smallHeight())

            // • PASSWORD
            PasswordField(
                value = loginState.password,
                label = null,
                isError = loginState.isInvalidPassword,
                onValueChange = {
                    onAction(LoginEvent.UpdatePassword(it))
                },
                isPasswordVisible = loginState.isPasswordVisible,
                clickTogglePasswordVisibility = {
                    onAction(
                        LoginEvent.SetIsPasswordVisible(!loginState.isPasswordVisible)
                    )
                },
                imeAction = ImeAction.Done,
                doneAction = {
                    performLogin()
                }
            )
            if (loginState.isInvalidPassword && loginState.isShowInvalidPasswordMessage) {
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
                enabled = !loginState.isLoading,
            ) {
                Text(
                    text = stringResource(R.string.login_button),
                    fontSize = MaterialTheme.typography.button.fontSize,
                )
                if (loginState.isLoading) {
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

            loginState.errorMessage.getOrNull?.let { errorMessage ->
                Text(
                    text = "Error: $errorMessage",
                    color = Color.Red,
                )
                Spacer(modifier = Modifier.extraSmallHeight())
            }
            if (loginState.isLoggedIn) {
                Text(text = stringResource(R.string.login_logged_in))
                Spacer(modifier = Modifier.extraSmallHeight())
            }
            loginState.statusMessage.getOrNull?.let { message ->
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
                loginState = LoginState(
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

