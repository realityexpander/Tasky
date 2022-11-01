package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import com.realityexpander.tasky.core.presentation.common.util.UiText

sealed interface AgendaEvent {
    data class ShowProgressIndicator(val isShowing: Boolean) : AgendaEvent

    data class UpdateUsername(val username: String) : AgendaEvent
    data class UpdateEmail(val email: String) : AgendaEvent
    data class UpdatePassword(val password: String) : AgendaEvent
    data class UpdateConfirmPassword(val confirmPassword: String) : AgendaEvent

    object ValidateUsername : AgendaEvent

    data class IsPasswordsMatch(val isMatch: Boolean) : AgendaEvent

    object ShowInvalidUsernameMessage : AgendaEvent

    data class Register(
            val username: String,
            val email: String,
            val password: String,
            val confirmPassword: String
        ) : AgendaEvent
    data class RegisterSuccess(val message: UiText) : AgendaEvent
    data class RegisterError(val message: UiText) : AgendaEvent

    data class UnknownError(val message: UiText) : AgendaEvent



    object ToggleLogoutDropdown : AgendaEvent
    object Logout : AgendaEvent
    data class SetIsLoaded(val isLoaded: Boolean) : AgendaEvent
}
