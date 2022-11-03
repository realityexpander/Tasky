package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.common.util.UiText

data class AgendaState(
    val username: String = "",
    val email: String = "",
    val authInfo: AuthInfo? = null,

    val isLoaded: Boolean = false,
    val isLoading: Boolean = false,

    val errorMessage: UiText = UiText.None,

    // Dropdown Menus
    val isLogoutDropdownShowing: Boolean = false,
    val isTaskDropdownShowing: Boolean = false,

)