package com.realityexpander.tasky.presentation.login_screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
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
    BackHandler(true) { /* We want to disable back clicks */ }

    val loginState by viewModel.loginStateFlow.collectAsState()
    val scope = rememberCoroutineScope()

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
        Text(text = "Login:")
        Spacer(modifier = Modifier.height(8.dp))

        // EMAIL
        OutlinedTextField(
            value = loginState.email,
            singleLine = true,
            onValueChange = {
                scope.launch {
                    viewModel.onEvent(LoginEvent.UpdateEmail(it))
                }
            },
            isError = loginState.isInvalidEmail,
            label = { Text(text = "Email") },
            placeholder = { Text(text = "Enter your Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        if(loginState.isInvalidEmail) {
            Text(text = "Invalid email", color = Color.Red)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // PASSWORD
        OutlinedTextField(
            value = loginState.password,
            singleLine = true,
            onValueChange = {
                scope.launch {
                    viewModel.onEvent(LoginEvent.UpdatePassword(it))
                }
            },
            isError = loginState.isInvalidPassword,
            label = { Text(text = "Password") },
            placeholder = { Text(text = "Enter your Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        if(loginState.isInvalidPassword) {
            Text(text = "Invalid password", color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
                scope.launch {
                    viewModel.onEvent(LoginEvent.ValidateEmail(loginState.email))
                    viewModel.onEvent(LoginEvent.ValidatePassword(loginState.password))
                    viewModel.onEvent(LoginEvent.Login(loginState.email, loginState.password))
                }
            },
            enabled = !loginState.isLoading,
            modifier = Modifier
                .align(alignment = Alignment.End)
        ) {
            Text(text = "Login")
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

        Text(text = "Not a member? Sign up",
            color = Color.Cyan,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .clickable(onClick = {
                    navigator.clearBackStack("LoginScreenDestination")
                    navigator.navigate(
                        RegisterScreenDestination(
                            email = loginState.email,
                            password = loginState.password,
                            confirmPassword = confirmPassword
                        )
                    )
                })
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Forgot password?",
            color = Color.Cyan,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // STATUS //////////////////////////////////////////

        if(loginState.isError) {
            Text(
                text = "Error: ${loginState.errorMessage}",
                color = Color.Red,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        if(loginState.isLoggedIn) {
            Text(text = "Logged in!")
            Spacer(modifier = Modifier.height(8.dp))
        }
        if(loginState.isLoading) {
            Text(text = "Loading...")
            Spacer(modifier = Modifier.height(8.dp))
        }
        if(loginState.statusMessage.isNotEmpty()) {
            Text(text = loginState.statusMessage)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

}