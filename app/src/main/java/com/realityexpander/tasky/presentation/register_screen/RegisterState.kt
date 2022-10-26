package com.realityexpander.tasky.presentation.register_screen

import com.realityexpander.tasky.ui.util.UiText

data class RegisterState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,

    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isError: Boolean = false,

    val isInvalidUsername: Boolean = false,
    val isShowInvalidUsernameMessage: Boolean = false,

    val isInvalidEmail: Boolean = false,
    val isShowInvalidEmailMessage: Boolean = false,

    val isInvalidPassword: Boolean = false,
    val isShowInvalidPasswordMessage: Boolean = false,

    val isInvalidConfirmPassword: Boolean = false,
    val isShowInvalidConfirmPasswordMessage: Boolean = false,

    val isPasswordsMatch: Boolean = true,

    val errorMessage: UiText = UiText.None,
    val statusMessage: UiText = UiText.None,
)