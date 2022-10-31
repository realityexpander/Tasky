package com.realityexpander.tasky.auth_feature.domain

import com.realityexpander.tasky.auth_feature.domain.validation.ValidatePassword
import com.realityexpander.tasky.auth_feature.domain.validation.ValidateUsername
import com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.IValidateEmail
import com.realityexpander.tasky.core.common.AuthToken
import com.realityexpander.tasky.core.common.Email
import com.realityexpander.tasky.core.common.Password
import com.realityexpander.tasky.core.common.Username


interface IAuthRepository {

    val validateEmail: IValidateEmail
    val validatePassword: ValidatePassword
    val validateUsername: ValidateUsername

    fun validateEmail(email: Email): Boolean {
        return validateEmail.validate(email)
    }
    fun validatePassword(password: Password): Boolean {
        return validatePassword.validate(password)
    }
    fun validateUsername(username: Username): Boolean {
        return validateUsername.validate(username)
    }

    suspend fun login(
        email: Email,
        password: Password
    ): AuthInfo

    suspend fun register(
        username: Username,
        email: Email,
        password: Password
    )

    suspend fun getAuthToken(): AuthToken?

    suspend fun getAuthInfo(): AuthInfo?

    suspend fun clearAuthInfo()

    suspend fun authenticateAuthInfo(authInfo: AuthInfo?): Boolean
}