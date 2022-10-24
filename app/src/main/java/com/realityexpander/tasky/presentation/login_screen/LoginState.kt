package com.realityexpander.tasky.presentation.login_screen

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,

    val isInvalidEmail: Boolean = false,
    val isInvalidPassword: Boolean = false,

    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val statusMessage: String = "",

    val isError: Boolean = false,
    val errorMessage: String = "",
)