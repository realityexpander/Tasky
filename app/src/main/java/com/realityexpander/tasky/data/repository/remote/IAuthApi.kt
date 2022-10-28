package com.realityexpander.tasky.data.repository.remote

import com.realityexpander.tasky.common.AuthToken

interface IAuthApi {
    suspend fun login(email: String, password: String): AuthToken

    suspend fun register(
        username: String,
        email: String,
        password: String
    ): AuthToken
}