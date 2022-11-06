package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.Email
import com.realityexpander.tasky.core.util.Username

data class AgendaState(
    val username: Username = "",
    val email: Email = "",
    val authInfo: AuthInfo? = null,

    val isLoaded: Boolean = false,
    val isLoading: Boolean = false,

    val errorMessage: UiText? = null,

    // One-time events
    val oneTimeEvent: AgendaEvent? = null,

    val agendaItems: List<AgendaItem> = emptyList(),
    val selectedDayIndex: Int = 0,
)