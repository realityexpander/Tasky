package com.realityexpander.tasky.presentation.register_screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.realityexpander.tasky.presentation.destinations.LoginScreenDestination
import com.realityexpander.tasky.presentation.login_screen.LoginEvent
import kotlinx.coroutines.launch

@Composable
@Destination("RegisterScreen")
fun RegisterScreen(
    email: String? = null,
    password: String? = null,
    navigator: DestinationsNavigator,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    BackHandler(true) { /* We want to disable back clicks */ }

    val registerState by viewModel.registerStateFlow.collectAsState()
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if(registerState.isLoading) {
            Spacer(modifier = Modifier.height(8.dp))
            CircularProgressIndicator(
                modifier = Modifier.align(alignment = Alignment.TopCenter)
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
        OutlinedTextField(
            value = registerState.email,
            onValueChange = {
                scope.launch {
                    viewModel.onEvent(RegisterEvent.UpdateEmail(it))
                }
            },
            isError = registerState.isInvalidEmail,
            label = { Text(text = "Email") },
            placeholder = { Text(text = "Enter your Email") },
            modifier = Modifier.fillMaxWidth()
        )
        if(registerState.isInvalidEmail) {
            Text(text = "Invalid email", color = Color.Red)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // PASSWORD
        OutlinedTextField(
            value = registerState.password,
            onValueChange = {
                scope.launch {
                    viewModel.onEvent(RegisterEvent.UpdatePassword(it))
                }
            },
            isError = registerState.isInvalidPassword,
            label = { Text(text = "Password") },
            placeholder = { Text(text = "Enter your Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        if(registerState.isInvalidPassword) {
            Text(text = "Invalid password", color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            scope.launch {
                viewModel.onEvent(RegisterEvent.ValidateEmail(registerState.email))
                viewModel.onEvent(RegisterEvent.ValidatePassword(registerState.password))
                viewModel.onEvent(RegisterEvent.Register(registerState.email, registerState.password))
            }
        },
            enabled = !registerState.isLoading,
            modifier = Modifier
                .align(alignment = Alignment.End)
        ) {
            Text(text = "Login")
        }
        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Are you a member? Tap to sign in",
            color = Color.Cyan,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .clickable(onClick = {
                    navigator.clearBackStack("RegistrationScreenDestination")
                    navigator.navigate(
                        LoginScreenDestination(
                            email = registerState.email,
                            password = registerState.password
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