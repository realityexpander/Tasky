package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.presentation.common.enums.AgendaItemType
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.UuidStr

sealed interface AgendaEvent {
    data class SetIsLoaded(val isLoaded: Boolean) : AgendaEvent
    data class ShowProgressIndicator(val isShowing: Boolean) : AgendaEvent

    object Logout : AgendaEvent

    data class SetSelectedDayIndex(val dayIndex: Int) : AgendaEvent

    // • Agenda Item - Create
    data class CreateAgendaItem(val agendaItemType: AgendaItemType) : AgendaEvent
    data class CreateAgendaItemSuccess(val message: UiText) : AgendaEvent
    data class CreateAgendaItemError(val message: UiText) : AgendaEvent

    data class TaskToggleCompleted(val agendaItemId: UuidStr) : AgendaEvent

    // • Errors
    data class Error(val message: UiText) : AgendaEvent

    // • Stateful One-time events
    sealed interface StatefulOneTimeEvent {
        object ResetScrollTo                                        : StatefulOneTimeEvent, AgendaEvent
        object ScrollToTop                                          : StatefulOneTimeEvent, AgendaEvent
        object ScrollToBottom                                       : StatefulOneTimeEvent, AgendaEvent
        data class ScrollToItemId(val agendaItemId: UuidStr)        : StatefulOneTimeEvent, AgendaEvent
    }

    // • One Time Events
    sealed interface OneTimeEvent {
        // • Event - Navigate to Create/Open/Edit Event Screen
        object NavigateToCreateEvent : AgendaEvent, OneTimeEvent
        data class NavigateToOpenEvent(val eventId: EventId) : AgendaEvent, OneTimeEvent
        data class NavigateToEditEvent(val eventId: EventId) : AgendaEvent, OneTimeEvent

        // • Event - Confirm delete
        data class ConfirmDeleteEvent(val eventId: EventId) : AgendaEvent, OneTimeEvent
    }

}

