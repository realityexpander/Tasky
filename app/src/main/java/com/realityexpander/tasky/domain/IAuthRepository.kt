package com.realityexpander.tasky.domain

import com.realityexpander.tasky.data.repository.AuthInfo


interface IAuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): AuthInfo

    suspend fun register(
        username: String,
        email: String,
        password: String
    ): AuthInfo
}