package com.realityexpander.tasky.agenda_feature.presentation.add_event_screen

import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.Email
import com.realityexpander.tasky.core.util.Username

data class AddEventState(
    val username: Username = "",
    val email: Email = "",
    val authInfo: AuthInfo? = null,

    val isLoaded: Boolean = false, // true only after init() is called
    val isProgressVisible: Boolean = false,

    val errorMessage: UiText? = null,

    // Stateful One-time events
//    val scrollToItemId: UuidStr? = null,

    val isEditMode: Boolean = false,

    // Event Details
//    val title: String = "",
//    val description: String = "",
//    val fromDateTime: ZonedDateTime = ZonedDateTime.now(),
//    val toDateTime: ZonedDateTime = ZonedDateTime.now(),
//    val remindAt: ZonedDateTime = ZonedDateTime.now(),
//    val isEventCreator: Boolean = true,
//    val isGoing: Boolean = true,
//    val photos: List<PhotoId> = emptyList(),

    // Event to view or edit
    val event: AgendaItem.Event? = null,
)
