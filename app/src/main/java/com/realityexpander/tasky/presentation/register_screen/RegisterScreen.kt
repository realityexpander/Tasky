package com.realityexpander.tasky.presentation.register_screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.realityexpander.tasky.presentation.components.EmailField
import com.realityexpander.tasky.presentation.components.PasswordField
import com.realityexpander.tasky.presentation.destinations.LoginScreenDestination
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Destination("RegisterScreen")
fun RegisterScreen(
    email: String? = null,
    password: String? = null,
    confirmPassword: String? = null,
    navigator: DestinationsNavigator,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    BackHandler(true) { /* We want to disable back clicks */ }

    val registerState by viewModel.registerState.collectAsState()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    fun performRegister() {
        scope.launch {
            viewModel.onEvent(RegisterEvent.ValidateEmail(registerState.email))
            viewModel.onEvent(RegisterEvent.ValidatePassword(registerState.password))
            viewModel.onEvent(RegisterEvent.ValidateConfirmPassword(registerState.confirmPassword))
            viewModel.onEvent(RegisterEvent.Register(
                registerState.email,
                registerState.password,
                registerState.confirmPassword
            ))

            focusManager.clearFocus()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if(registerState.isLoading) {
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
        Text(text = "Register:")
        Spacer(modifier = Modifier.height(8.dp))

        // EMAIL
        EmailField(
            email = registerState.email,
            isError = registerState.isInvalidEmail,
            onEmailChange = {
                scope.launch {
                    viewModel.onEvent(RegisterEvent.UpdateEmail(it))
                }
            }
        )
        if(registerState.isInvalidEmail) {
            Text(text = "Invalid email", color = Color.Red)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // PASSWORD
        PasswordField(
            password = registerState.password,
            isError = registerState.isInvalidPassword,
            onPasswordChange = {
                scope.launch {
                    viewModel.onEvent(RegisterEvent.UpdatePassword(it))
                }
            },
            isPasswordVisible = registerState.isPasswordVisible,
            clickTogglePasswordVisibility = {
                viewModel.sendEvent(RegisterEvent.TogglePasswordVisibility(registerState.isPasswordVisible))
            },
            imeAction = ImeAction.Next,
        )
        if (registerState.isInvalidPassword) {
            Text(text = "Invalid password", color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // CONFIRM PASSWORD
        PasswordField(
            label = "Confirm Password",
            placeholder = "Confirm your password",
            password = registerState.confirmPassword,
            isError = registerState.isInvalidConfirmPassword,
            onPasswordChange = {
                scope.launch {
                    viewModel.onEvent(RegisterEvent.UpdateConfirmPassword(it))
                }
            },
            isPasswordVisible = registerState.isPasswordVisible,
            clickTogglePasswordVisibility = {
                viewModel.sendEvent(RegisterEvent.TogglePasswordVisibility(registerState.isPasswordVisible))
            },
            imeAction = ImeAction.Done,
            doneAction = {
                performRegister()
            },
        )
        if (registerState.isInvalidConfirmPassword) {
            Text(text = "Invalid confirm password", color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // VALIDATE MATCHING PASSWORDS
//        if( !registerState.isInvalidPassword
//            && !registerState.isInvalidConfirmPassword
//            && registerState.password.isNotEmpty()
//            && registerState.confirmPassword.isNotEmpty()
//            && registerState.password != registerState.confirmPassword
//        )
        if(!registerState.passwordsMatch)
        {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Passwords do not match", color = Color.Red)
        }

        // REGISTER BUTTON
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            performRegister()
        },
            enabled = !registerState.isLoading,
            modifier = Modifier
                .align(alignment = Alignment.End)
        ) {
            Text(text = "Register")
            if(registerState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(16.dp)
                        .align(alignment = Alignment.CenterVertically)
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        // SIGN IN BUTTON
        Text(text = "Are you a member? Tap to sign in",
            color = Color.Cyan,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .clickable(onClick = {
                    navigator.clearBackStack("RegistrationScreenDestination")
                    navigator.navigate(
                        LoginScreenDestination(
                            email = registerState.email,
                            password = registerState.password,
                            confirmPassword = registerState.confirmPassword
                        )
                    )
                })
        )
        Spacer(modifier = Modifier.height(16.dp))

        // STATUS //////////////////////////////////////////

        if(registerState.isError) {
            Text(
                text = "Error: ${registerState.errorMessage}",
                color = Color.Red,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        if(registerState.isLoggedIn) {
            Text(text = "Registered and Logged in!")
            Spacer(modifier = Modifier.height(8.dp))
        }
        if(registerState.isLoading) {
            Text(text = "Loading...")
            Spacer(modifier = Modifier.height(8.dp))
        }
        if(registerState.statusMessage.isNotEmpty()) {
            Text(text = registerState.statusMessage)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}