package com.realityexpander.tasky.auth_feature.presentation.splash_screen

import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.common.util.UiText

data class SplashState(
    val authInfo: AuthInfo? = null,
    val authInfoChecked: Boolean = false,
    val statusMessage: UiText? = null,
)