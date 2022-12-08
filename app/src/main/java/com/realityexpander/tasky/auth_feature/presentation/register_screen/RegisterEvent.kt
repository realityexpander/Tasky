package com.realityexpander.tasky.auth_feature.presentation.register_screen

import com.realityexpander.tasky.core.presentation.util.UiText

sealed interface RegisterEvent {
    data class SetIsLoading(val isLoading: Boolean) : RegisterEvent

    data class UpdateUsername(val username: String) : RegisterEvent
    data class UpdateEmail(val email: String) : RegisterEvent
    data class UpdatePassword(val password: String) : RegisterEvent
    data class UpdateConfirmPassword(val confirmPassword: String) : RegisterEvent
    data class SetIsPasswordVisible(val isVisible: Boolean) : RegisterEvent

    object ValidateUsername : RegisterEvent
    object ValidateEmail : RegisterEvent
    object ValidatePassword : RegisterEvent
    object ValidateConfirmPassword : RegisterEvent
    object ValidatePasswordsMatch : RegisterEvent

    object ShowInvalidUsernameMessage : RegisterEvent
    object ShowInvalidEmailMessage : RegisterEvent
    object ShowInvalidPasswordMessage : RegisterEvent
    object ShowInvalidConfirmPasswordMessage : RegisterEvent

    data class Register(
            val username: String,
            val email: String,
            val password: String,
            val confirmPassword: String
        ) : RegisterEvent
    data class RegisterSuccess(val message: UiText) : RegisterEvent
    data class RegisterError(val message: UiText) : RegisterEvent

    object EmailAlreadyExists : RegisterEvent
    data class UnknownError(val message: UiText) : RegisterEvent
}