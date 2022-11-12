package com.realityexpander.tasky.agenda_feature.presentation.add_event_screen

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

)
