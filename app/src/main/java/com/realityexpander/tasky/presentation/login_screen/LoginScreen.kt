package com.realityexpander.tasky.presentation.login_screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.realityexpander.tasky.R
import com.realityexpander.tasky.common.UiText
import com.realityexpander.tasky.presentation.components.EmailField
import com.realityexpander.tasky.presentation.components.PasswordField
import com.realityexpander.tasky.presentation.destinations.RegisterScreenDestination
import kotlinx.coroutines.launch

@Composable
@Destination(start = true)
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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if(loginState.isLoading) {
            Spacer(modifier = Modifier.height(8.dp))
            CircularProgressIndicator(
                modifier = Modifier.align(alignment = Alignment.Center)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(text = UiText.Res(R.string.login_title).get())
        Spacer(modifier = Modifier.height(8.dp))

        // EMAIL
        EmailField(
            value = loginState.email,
            isError = loginState.isInvalidEmail,
            onValueChange = {
                viewModel.sendEvent(LoginEvent.UpdateEmail(it))
            }
        )
        if(loginState.isInvalidEmail) {
            Text(text = UiText.Res(R.string.error_invalid_email).get(), color = Color.Red)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // PASSWORD
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
        if(loginState.isInvalidPassword) {
            Text(text = UiText.Res(R.string.error_invalid_password).get(), color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // LOGIN BUTTON
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
                performLogin()
            },
            enabled = !loginState.isLoading,
            modifier = Modifier
                .align(alignment = Alignment.End)
        ) {
            Text(text = UiText.Res(R.string.login_button).get())
            if(loginState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(16.dp)
                        .align(alignment = CenterVertically)
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        // REGISTER TEXT BUTTON
        Text(text = UiText.Res(R.string.login_not_a_member_sign_up).get(),
            color = Color.Cyan,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .clickable(onClick = {
                    navigateToRegister()
                })
        )
        Spacer(modifier = Modifier.height(16.dp))

        // STATUS //////////////////////////////////////////

        loginState.errorMessage.getOrNull()?.let { errorMessage ->
            Text(
                text = "Error: $errorMessage",
                color = Color.Red,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        if(loginState.isLoggedIn) {
            Text(text = UiText.Res(R.string.login_logged_in).get())
            Spacer(modifier = Modifier.height(8.dp))
        }
        loginState.statusMessage.asStrOrNull()?.let { message ->
            Text(text = message)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

}