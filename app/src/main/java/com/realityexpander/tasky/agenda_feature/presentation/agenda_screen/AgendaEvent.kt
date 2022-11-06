package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

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

    // • One-time events
    sealed interface OneTimeEvent {
        object Reset : OneTimeEvent, AgendaEvent
        data class ScrollToItem( val agendaItemId: UuidStr) : OneTimeEvent, AgendaEvent
        object ScrollToTop : OneTimeEvent, AgendaEvent
        object ScrollToBottom : OneTimeEvent, AgendaEvent
    }
}

