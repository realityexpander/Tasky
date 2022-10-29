package com.realityexpander.tasky.domain

import com.realityexpander.tasky.common.Email
import com.realityexpander.tasky.common.Password
import com.realityexpander.tasky.common.Username
import com.realityexpander.tasky.domain.validation.ValidatePassword
import com.realityexpander.tasky.domain.validation.ValidateUsername
import com.realityexpander.tasky.domain.validation.validateEmail.IValidateEmail


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
}