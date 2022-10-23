package com.realityexpander.tasky.presentation.login_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch

@Composable
@Destination(start = true)
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginStateFlow.collectAsState()
    val scope = rememberCoroutineScope()

    Column {
        Text(text = "Login:")
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = loginState.email,
            onValueChange = {
                scope.launch {
                    viewModel.onEvent(LoginEvent.UpdateEmail(it))
                    viewModel.onEvent(LoginEvent.ValidateEmail(it))
                }
            },
            isError = loginState.isInvalidEmail,
            label = { Text(text = "Email") },
            placeholder = { Text(text = "Enter your Email") }
        )
        if(loginState.isInvalidEmail) {
            Text(text = "Invalid email")
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = loginState.password,
            onValueChange = {
                scope.launch {
                    viewModel.onEvent(LoginEvent.UpdatePassword(it))
                    viewModel.onEvent(LoginEvent.ValidatePassword(it))
                }
            },
            isError = loginState.isInvalidPassword,
            label = { Text(text = "Password") },
            placeholder = { Text(text = "Enter your Password") }
        )
        if(loginState.isInvalidPassword) {
            Text(text = "Invalid password")
            Spacer(modifier = Modifier.height(8.dp))
        }

        if(loginState.isError) {
            Text(text = "Error: ${loginState.errorMessage}")
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

        Text(text = "Not a member? Sign up")
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Forgot password?")
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            scope.launch {
                viewModel.onEvent(LoginEvent.Login(loginState.email, loginState.password))
            }
        }) {
            Text(text = "Login")
        }
    }

}