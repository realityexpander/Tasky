package com.realityexpander.tasky.presentation.login_screen

sealed class LoginEvent {
    data class Login(val email: String, val password: String) : LoginEvent()
    data class ValidateEmail(val email: String) : LoginEvent()
    data class ValidatePassword(val password: String) : LoginEvent()
    object LoginSuccess : LoginEvent()
    object LoginError : LoginEvent()
    object InvalidEmail : LoginEvent()
    object InvalidPassword : LoginEvent()
    data class UnknownError(val message: String) : LoginEvent()
}
