package com.realityexpander.tasky.agenda_feature.presentation.event_screen

import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.Email
import com.realityexpander.tasky.core.util.Username

data class EventScreenState(
    val username: Username = "",
    val email: Email = "",
    val authInfo: AuthInfo? = null,

    val isLoaded: Boolean = false, // true only after init() is called
    val isProgressVisible: Boolean = false,

    val errorMessage: UiText? = null,

    val addAttendeeDialogErrorMessage: UiText? = null,
    val isAttendeeEmailValid: Boolean? = null,

    // Stateful One-time events
//    val scrollToItemId: UuidStr? = null,  // todo add one-time events

    val isEditable: Boolean = false,

    val editMode: EventScreenEvent.EditMode? = null,

    // Event to view or edit
    val event: AgendaItem.Event? = null,

    // Confirm Action Dialog (Delete/Join/Leave)
    val showAlertDialog: EventScreenEvent.ShowAlertDialog? = null,
)
