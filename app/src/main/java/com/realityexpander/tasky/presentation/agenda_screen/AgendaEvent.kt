package com.realityexpander.tasky.presentation.agenda_screen

import com.realityexpander.tasky.presentation.util.UiText

sealed class AgendaEvent {
    data class SetIsLoading(val isLoading: Boolean) : AgendaEvent()

    data class UpdateUsername(val username: String) : AgendaEvent()
    data class UpdateEmail(val email: String) : AgendaEvent()
    data class UpdatePassword(val password: String) : AgendaEvent()
    data class UpdateConfirmPassword(val confirmPassword: String) : AgendaEvent()
    data class SetIsPasswordVisible(val isVisible: Boolean) : AgendaEvent()

    object ValidateUsername : AgendaEvent()
    object ValidateEmail : AgendaEvent()
    object ValidatePassword : AgendaEvent()
    object ValidateConfirmPassword : AgendaEvent()
    object ValidatePasswordsMatch : AgendaEvent()

    data class IsValidUsername(val isValid: Boolean) : AgendaEvent()
    data class IsValidEmail(val isValid: Boolean) : AgendaEvent()
    data class IsValidPassword(val isValid: Boolean) : AgendaEvent()
    data class IsValidConfirmPassword(val isValid: Boolean) : AgendaEvent()
    data class IsPasswordsMatch(val isMatch: Boolean) : AgendaEvent()

    object ShowInvalidUsernameMessage : AgendaEvent()
    object ShowInvalidEmailMessage : AgendaEvent()
    object ShowInvalidPasswordMessage : AgendaEvent()
    object ShowInvalidConfirmPasswordMessage : AgendaEvent()

    data class Register(
            val username: String,
            val email: String,
            val password: String,
            val confirmPassword: String
        ) : AgendaEvent()
    data class RegisterSuccess(val message: UiText) : AgendaEvent()
    data class RegisterError(val message: UiText) : AgendaEvent()

    object EmailAlreadyExists : AgendaEvent()
    data class UnknownError(val message: UiText) : AgendaEvent()


}
