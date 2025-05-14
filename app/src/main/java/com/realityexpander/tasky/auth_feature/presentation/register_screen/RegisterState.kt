@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.realityexpander.tasky.auth_feature.presentation.register_screen

import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.util.UiText

data class RegisterState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,

    val isLoading: Boolean = false,

    val isInvalidUsername: Boolean = false,
    val isInvalidUsernameMessageVisible: Boolean = false,

    val isInvalidEmail: Boolean = false,
    val isInvalidEmailMessageVisible: Boolean = false,

    val isInvalidPassword: Boolean = false,
    val isInvalidPasswordMessageVisible: Boolean = false,

    val isInvalidConfirmPassword: Boolean = false,
    val isInvalidConfirmPasswordMessageVisible: Boolean = false,

    val isPasswordsMatch: Boolean = true,

    val errorMessage: UiText? = null,
    val statusMessage: UiText? = null,

    val authInfo: AuthInfo? = null,  // when not-null, the user is logged in
)
