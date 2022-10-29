package com.realityexpander.tasky.presentation.login_screen

import com.realityexpander.tasky.domain.AuthInfo
import com.realityexpander.tasky.ui.util.UiText

sealed class LoginEvent {
    data class UpdateEmail(val email: String) : LoginEvent()
    data class UpdatePassword(val password: String) : LoginEvent()
    data class SetIsPasswordVisible(val isPasswordVisible: Boolean) : LoginEvent()

    object ValidateEmail : LoginEvent()
    object ValidatePassword : LoginEvent()

    data class SetIsValidEmail(val isValid: Boolean) : LoginEvent()
    object ShowInvalidEmailMessage : LoginEvent()
    data class SetIsValidPassword(val isValid: Boolean) : LoginEvent()
    object ShowInvalidPasswordMessage : LoginEvent()

    data class Login(val email: String, val password: String) : LoginEvent()
    data class SetIsLoading(val isLoading: Boolean) : LoginEvent()
    data class LoginSuccess(val authInfo: AuthInfo) : LoginEvent()
    data class LoginError(val message: UiText) : LoginEvent()

    data class UnknownError(val message: UiText) : LoginEvent()
}
