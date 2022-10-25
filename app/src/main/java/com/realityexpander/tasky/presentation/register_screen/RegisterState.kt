package com.realityexpander.tasky.presentation.register_screen

import com.realityexpander.tasky.common.UiText

data class RegisterState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,

    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isError: Boolean = false,

    val isInvalidEmail: Boolean = false,
    val isInvalidPassword: Boolean = false,
    val isInvalidConfirmPassword: Boolean = false,
    val isPasswordsMatch: Boolean = true,

    val errorMessage: UiText = UiText.None,
    val statusMessage: UiText = UiText.None,
)