package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.UuidStr

sealed interface AgendaEvent {
    data class SetIsLoaded(val isLoaded: Boolean) : AgendaEvent
    data class ShowProgressIndicator(val isShowing: Boolean) : AgendaEvent

    object ValidateUsername : AgendaEvent

    object ShowInvalidUsernameMessage : AgendaEvent

    data class UnknownError(val message: UiText) : AgendaEvent

    data class CreateEvent(
            val username: String,
            val email: String,
            val password: String,
            val confirmPassword: String
        ) : AgendaEvent
    data class CreateEventSuccess(val message: UiText) : AgendaEvent
    data class CreateEventError(val message: UiText) : AgendaEvent


    object ToggleLogoutDropdown : AgendaEvent
    object Logout : AgendaEvent

    data class ShowAgendaItemDropdown(val agendaItemId: UuidStr? = null) : AgendaEvent

    data class ToggleTaskCompleted(val agendaItemId: UuidStr) : AgendaEvent

}
