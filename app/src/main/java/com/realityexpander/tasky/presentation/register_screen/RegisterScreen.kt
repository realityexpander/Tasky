package com.realityexpander.tasky.presentation.register_screen

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun RegisterScreen(
    vieModel: RegisterViewModel = hiltViewModel()
) {
    Text(text = "Register")
}