package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

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

    val errorMessage: UiText = UiText.None,

    // Dropdown Menus
    val isLogoutDropdownShowing: Boolean = false,

    val isAgendaItemMenuDropdownShowing: Boolean = false,
    val agendaItemIdForMenuShowing: UuidStr? = null,

)