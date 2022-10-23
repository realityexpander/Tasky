package com.realityexpander.tasky.presentation.login_screen

import com.realityexpander.tasky.common.AuthToken

sealed class LoginEvent {
    data class UpdateEmail(val email: String) : LoginEvent()
    data class UpdatePassword(val password: String) : LoginEvent()
    data class Login(val email: String, val password: String) : LoginEvent()
    data class ValidateEmail(val email: String) : LoginEvent()
    data class ValidatePassword(val password: String) : LoginEvent()
    data class LoginSuccess(val authToken: AuthToken) : LoginEvent()
    data class LoginError(val message: String) : LoginEvent()
    object IsInvalidEmail : LoginEvent()
    object IsInvalidPassword : LoginEvent()
    object IsValidEmail : LoginEvent()
    object IsValidPassword : LoginEvent()
    data class UnknownError(val message: String) : LoginEvent()
}
