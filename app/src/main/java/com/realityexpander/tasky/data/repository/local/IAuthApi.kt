package com.realityexpander.tasky.data.repository.local

import com.realityexpander.tasky.common.AuthToken

interface IAuthApi {
    suspend fun login(email: String, password: String): AuthToken

    suspend fun register(email: String, password: String): AuthToken
}