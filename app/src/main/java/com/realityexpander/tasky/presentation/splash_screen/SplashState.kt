package com.realityexpander.tasky.presentation.splash_screen

import com.realityexpander.tasky.domain.AuthInfo
import com.realityexpander.tasky.presentation.common.util.UiText

data class SplashState(
    val authInfo: AuthInfo? = null,
    val authInfoChecked: Boolean = false,
    val statusMessage: UiText = UiText.None,
)