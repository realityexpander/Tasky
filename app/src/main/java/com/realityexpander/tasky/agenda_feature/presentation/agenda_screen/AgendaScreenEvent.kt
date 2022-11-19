package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.presentation.common.enums.AgendaItemType
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.UuidStr

sealed interface AgendaScreenEvent {
    data class SetIsLoaded(val isLoaded: Boolean) : AgendaScreenEvent
    data class ShowProgressIndicator(val isShowing: Boolean) : AgendaScreenEvent

    object Logout : AgendaScreenEvent

    data class SetSelectedDayIndex(val dayIndex: Int) : AgendaScreenEvent

    // • Agenda Item
    data class CreateAgendaItem(val agendaItemType: AgendaItemType) : AgendaScreenEvent
    data class ConfirmDeleteAgendaItem(val agendaItem: AgendaItem) : AgendaScreenEvent
    object DismissDeleteAgendaItem : AgendaScreenEvent
    data class DeleteAgendaItem(val agendaItem: AgendaItem) : AgendaScreenEvent

    data class ToggleTaskCompleted(val agendaItemId: UuidStr) : AgendaScreenEvent

    // • Errors
    data class Error(val message: UiText) : AgendaScreenEvent

    // • Stateful One-time events
    sealed interface StatefulOneTimeEvent {
        object ResetScrollTo                                 : StatefulOneTimeEvent, AgendaScreenEvent
        object ScrollToTop                                   : StatefulOneTimeEvent, AgendaScreenEvent
        object ScrollToBottom                                : StatefulOneTimeEvent, AgendaScreenEvent
        data class ScrollToItemId(val agendaItemId: UuidStr) : StatefulOneTimeEvent, AgendaScreenEvent
    }

    // • One Time Events
    sealed interface OneTimeEvent {
        // • Event - Navigate to Create/Open/Edit Event Screen
        object NavigateToCreateEvent : AgendaScreenEvent, OneTimeEvent
        data class NavigateToOpenEvent(val eventId: EventId) : AgendaScreenEvent, OneTimeEvent
        data class NavigateToEditEvent(val eventId: EventId) : AgendaScreenEvent, OneTimeEvent
    }

}