package com.realityexpander.tasky.agenda_feature.presentation.reminder_screen

import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.Email
import com.realityexpander.tasky.core.util.Username

data class ReminderScreenState(
    val username: Username = "",
    val email: Email = "",
    val authInfo: AuthInfo? = null,

    val isLoaded: Boolean = false, // true only after init() is called
    val isProgressVisible: Boolean = false,

    val errorMessage: UiText? = null,

    val isEditable: Boolean = false,

    val editMode: ReminderScreenEvent.EditMode? = null,

    // Reminder to view or edit
    val reminder: AgendaItem.Reminder? = null,

    // Confirm Action Dialog (Delete/Join/Leave)
    val showAlertDialog: ReminderScreenEvent.ShowAlertDialog? = null,
)
