package com.realityexpander.tasky.data.repository.remote

import com.realityexpander.tasky.data.repository.AuthInfo


interface IAuthApi {
    suspend fun login(email: String, password: String): AuthInfo

    suspend fun register(
        username: String,
        email: String,
        password: String
    ): AuthInfo
}