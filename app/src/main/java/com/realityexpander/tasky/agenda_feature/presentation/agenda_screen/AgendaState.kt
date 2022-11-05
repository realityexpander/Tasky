package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.Email
import com.realityexpander.tasky.core.util.Username
import com.realityexpander.tasky.core.util.UuidStr

data class AgendaState(
    val username: Username = "",
    val email: Email = "",
    val authInfo: AuthInfo? = null,

    val isLoaded: Boolean = false,
    val isLoading: Boolean = false,

    val errorMessage: UiText? = null,

    // Dropdown Menus
    val isLogoutDropdownVisible: Boolean = false,
    val agendaItemIdForMenu: UuidStr? = null,

    val agendaItems: List<AgendaItem> = emptyList(),
)