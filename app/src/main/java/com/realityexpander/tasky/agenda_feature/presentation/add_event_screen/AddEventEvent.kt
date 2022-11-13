package com.realityexpander.tasky.agenda_feature.presentation.add_event_screen

import com.realityexpander.tasky.core.presentation.common.util.UiText

sealed interface AddEventEvent {
    data class SetIsLoaded(val isLoaded: Boolean) : AddEventEvent
    data class ShowProgressIndicator(val isShowing: Boolean) : AddEventEvent

    // • Errors
    data class Error(val message: UiText) : AddEventEvent

    // • One-time events
    sealed interface StatefulOneTimeEvent {
//        object ResetScrollTo                                        : StatefulOneTimeEvent, AddEventEvent
    }
}