package com.realityexpander.tasky.auth_feature.presentation.register_screen

import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.common.util.UiText

data class RegisterState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,

    val isLoading: Boolean = false,
    val isError: Boolean = false,

    val isInvalidUsername: Boolean = false,
    val isShowInvalidUsernameMessage: Boolean = false,

    val isInvalidEmail: Boolean = false,
    val isShowInvalidEmailMessage: Boolean = false,

    val isInvalidPassword: Boolean = false,
    val isShowInvalidPasswordMessage: Boolean = false,

    val isInvalidConfirmPassword: Boolean = false,
    val isShowInvalidConfirmPasswordMessage: Boolean = false,

    val isPasswordsMatch: Boolean = true,

    val errorMessage: UiText = UiText.None,
    val statusMessage: UiText = UiText.None,

    val authInfo: AuthInfo? = null,  // when not-null, the user is logged in
)