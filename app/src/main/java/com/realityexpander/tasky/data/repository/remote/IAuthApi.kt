package com.realityexpander.tasky.data.repository.remote

import com.realityexpander.tasky.common.AuthToken
import com.realityexpander.tasky.common.Email
import com.realityexpander.tasky.common.Password
import com.realityexpander.tasky.common.Username


interface IAuthApi {
    suspend fun login(
        email: Email,
        password: Password
    ): AuthInfoDTO

    suspend fun register(
        username: Username,
        email: Email,
        password: Password
    )

    suspend fun authenticate(authToken: AuthToken? = null): Boolean
}