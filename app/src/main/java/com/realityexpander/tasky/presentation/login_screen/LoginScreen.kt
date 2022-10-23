package com.realityexpander.tasky.presentation.login_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(text = "Login:")
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = loginState.email,
            onValueChange = {
                scope.launch {
                    viewModel.onEvent(LoginEvent.UpdateEmail(it))
                }
            },
            isError = loginState.isInvalidEmail,
            label = { Text(text = "Email") },
            placeholder = { Text(text = "Enter your Email") },
            modifier = Modifier.fillMaxWidth()
        )
        if(loginState.isInvalidEmail) {
            Text(text = "Invalid email", color = Color.Red)
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = loginState.password,
            onValueChange = {
                scope.launch {
                    viewModel.onEvent(LoginEvent.UpdatePassword(it))
                }
            },
            isError = loginState.isInvalidPassword,
            label = { Text(text = "Password") },
            placeholder = { Text(text = "Enter your Password") },
            modifier = Modifier.fillMaxWidth()
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
        }
        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Not a member? Sign up",
            color = Color.Cyan,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Forgot password?",
            color = Color.Cyan,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

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
    }

}