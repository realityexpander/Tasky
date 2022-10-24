package com.realityexpander.tasky.presentation.register_screen

import com.realityexpander.tasky.common.AuthToken

sealed class RegisterEvent {
    data class UpdateEmail(val email: String) : RegisterEvent()
    data class UpdatePassword(val password: String) : RegisterEvent()
    data class UpdateConfirmPassword(val confirmPassword: String) : RegisterEvent()
    data class Register(val email: String, val password: String, val confirmPassword: String) : RegisterEvent()
    data class ValidateEmail(val email: String) : RegisterEvent()
    data class ValidatePassword(val password: String) : RegisterEvent()
    data class ValidateConfirmPassword(val confirmPassword: String) : RegisterEvent()
    data class RegisterSuccess(val authToken: AuthToken) : RegisterEvent()
    data class RegisterError(val message: String) : RegisterEvent()

    object EmailAlreadyExists : RegisterEvent()
    object IsInvalidEmail : RegisterEvent()
    object IsInvalidPassword : RegisterEvent()
    object IsInvalidConfirmPassword : RegisterEvent()
    object IsValidEmail : RegisterEvent()
    object IsValidPassword : RegisterEvent()
    object IsValidConfirmPassword : RegisterEvent()
    data class UnknownError(val message: String) : RegisterEvent()
    data class Loading(val isLoading: Boolean) : RegisterEvent()

    data class TogglePasswordVisibility(val isVisible: Boolean) : RegisterEvent()
}
