package com.realityexpander.tasky.presentation.login_screen

import com.realityexpander.tasky.common.AuthToken
import com.realityexpander.tasky.common.UiText

sealed class LoginEvent {
    data class UpdateEmail(val email: String) : LoginEvent()
    data class UpdatePassword(val password: String) : LoginEvent()
    data class TogglePasswordVisibility(val isPasswordVisible: Boolean) : LoginEvent()

    object ValidateEmail : LoginEvent()
    object ValidatePassword : LoginEvent()

    data class IsValidEmail(val isValid: Boolean) : LoginEvent()
    data class IsValidPassword(val isValid: Boolean) : LoginEvent()

    data class Login(val email: String, val password: String) : LoginEvent()
    data class Loading(val isLoading: Boolean) : LoginEvent()
    data class LoginSuccess(val authToken: AuthToken) : LoginEvent()
    data class LoginError(val message: UiText) : LoginEvent()

    data class UnknownError(val message: UiText) : LoginEvent()
}
