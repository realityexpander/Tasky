package com.realityexpander.tasky.presentation.login_screen

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination(start = true)
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {

    Text(text = "Login")

//    TextField(value = viewModel.state.email, onValueChange ={
//        viewModel.state.email = it
//    }) {
//        Text(text = "Email")
//    }
}