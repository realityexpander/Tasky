package com.realityexpander.tasky.presentation.login_screen

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.realityexpander.tasky.R
import com.realityexpander.tasky.common.UiText
import com.realityexpander.tasky.data.repository.AuthRepositoryFakeImpl
import com.realityexpander.tasky.data.repository.local.AuthDaoFakeImpl
import com.realityexpander.tasky.data.repository.remote.AuthApiFakeImpl
import com.realityexpander.tasky.domain.validation.ValidateEmailImpl
import com.realityexpander.tasky.domain.validation.ValidatePassword
import com.realityexpander.tasky.presentation.destinations.RegisterScreenDestination
import com.realityexpander.tasky.ui.components.EmailField
import com.realityexpander.tasky.ui.components.PasswordField
import com.realityexpander.tasky.ui.theme.TaskyTheme
import com.realityexpander.tasky.ui.theme.modifiers.*

@Composable
@Destination
@RootNavGraph(start = true)
fun LoginScreen(
    email: String? = null,
    password: String? = null,
    confirmPassword: String? = null,
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val loginState by viewModel.loginState.collectAsState()
    val focusManager = LocalFocusManager.current

    fun performLogin() {
        viewModel.sendEvent(LoginEvent.Login(loginState.email, loginState.password))
        focusManager.clearFocus()
    }

    fun navigateToRegister() {
        navigator.navigate(
            RegisterScreenDestination(
                email = loginState.email,
                password = loginState.password,
                confirmPassword = confirmPassword
            )
        ) {
            popUpTo("LoginScreen") {
                inclusive = true
            }
        }
    }

    BackHandler(true) {
        navigateToRegister()
    }

//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        if(loginState.isLoading) {
//            Spacer(modifier = Modifier.mediumHeight())
//            CircularProgressIndicator(
//                modifier = Modifier.align(alignment = Alignment.Center)
//            )
//        }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.onSurface)
    ) {
        Spacer(modifier = Modifier.mediumHeight())
        Text(
            text = UiText.Res(R.string.login_title).get(),
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.surface,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.mediumHeight())

        Column(
            modifier = Modifier
                .taskyScreenTopCorners(color = MaterialTheme.colors.surface)
        ) {

            // • EMAIL
            EmailField(
                value = loginState.email,
                isError = loginState.isInvalidEmail,
                onValueChange = {
                    viewModel.sendEvent(LoginEvent.UpdateEmail(it))
                }
            )
            if (loginState.isInvalidEmail && loginState.isShowInvalidEmailMessage) {
                Text(text = UiText.Res(R.string.error_invalid_email).get(), color = Color.Red)
            }
            Spacer(modifier = Modifier.extraSmallHeight())

            // • PASSWORD
            PasswordField(
                value = loginState.password,
                isError = loginState.isInvalidPassword,
                onValueChange = {
                    viewModel.sendEvent(LoginEvent.UpdatePassword(it))
                },
                isPasswordVisible = loginState.isPasswordVisible,
                clickTogglePasswordVisibility = {
                    viewModel.sendEvent(
                        LoginEvent.TogglePasswordVisibility(loginState.isPasswordVisible)
                    )
                },
                imeAction = ImeAction.Done,
                doneAction = {
                    performLogin()
                }
            )
            if (loginState.isInvalidPassword && loginState.isShowInvalidPasswordMessage) {
                Text(text = UiText.Res(R.string.error_invalid_password).get(), color = Color.Red)
                Spacer(modifier = Modifier.extraSmallHeight())
            }

            // • LOGIN BUTTON
            Spacer(modifier = Modifier.smallHeight())
            Button(
                onClick = {
                    performLogin()
                },
                modifier = Modifier
                    .taskyWideButton(color = MaterialTheme.colors.primary)
                    .align(alignment = Alignment.CenterHorizontally),
                enabled = !loginState.isLoading,
            ) {
                Text(text = UiText.Res(R.string.login_button).get())
                if (loginState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(start = DP.tiny)
                            .size(DP.small)
                            .align(alignment = CenterVertically)
                    )
                }
            }
            Spacer(modifier = Modifier.largeHeight())

            // • REGISTER TEXT BUTTON
            Text(
                text = UiText.Res(R.string.login_not_a_member_sign_up).get(),
                color = Color.Cyan,
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .clickable(onClick = {
                        navigateToRegister()
                    })
            )
            Spacer(modifier = Modifier.smallHeight())

            // STATUS //////////////////////////////////////////

            loginState.errorMessage.getOrNull()?.let { errorMessage ->
                Text(
                    text = "Error: $errorMessage",
                    color = Color.Red,
                )
                Spacer(modifier = Modifier.extraSmallHeight())
            }
            if (loginState.isLoggedIn) {
                Text(text = UiText.Res(R.string.login_logged_in).get())
                Spacer(modifier = Modifier.extraSmallHeight())
            }
            loginState.statusMessage.asStrOrNull()?.let { message ->
                Text(text = message)
                Spacer(modifier = Modifier.extraSmallHeight())
            }
        }
    }
}

@Composable
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
fun LoginScreenPreview() {
    TaskyTheme {
        androidx.compose.material.Surface {
            LoginScreen(
                navigator = EmptyDestinationsNavigator,
                viewModel = LoginViewModel(
                    authRepository = AuthRepositoryFakeImpl(
                        authApi = AuthApiFakeImpl(),
                        authDao = AuthDaoFakeImpl(),
                        validateEmail = ValidateEmailImpl(),
                    ),
                    validateEmail = ValidateEmailImpl(),
                    validatePassword = ValidatePassword(),
                    savedStateHandle = SavedStateHandle().apply {
                        // For Live Preview
                        set("email", "chris@demo.com")
                        set("password", "123456Aa")
                        set("confirmPassword", "123456Aa")
                    }
                )
            )
        }
    }
}

@Composable
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
fun LoginScreenPreview_NightMode() {
    LoginScreenPreview()
}