package com.realityexpander.tasky.presentation.login_screen

import com.realityexpander.tasky.common.UiText

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,

    val isInvalidEmail: Boolean = false,
    val isInvalidPassword: Boolean = false,

    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val statusMessage: UiText = UiText.None,

    val isError: Boolean = false,
    val errorMessage: UiText = UiText.None,
)