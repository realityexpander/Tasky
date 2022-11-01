package com.realityexpander.tasky.auth_feature.presentation.splash_screen

import com.realityexpander.tasky.auth_feature.domain.AuthInfo

data class SplashState(
    val authInfo: AuthInfo? = null,
    val isLoading: Boolean = true,
)