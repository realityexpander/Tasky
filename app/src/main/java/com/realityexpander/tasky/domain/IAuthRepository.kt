package com.realityexpander.tasky.domain

import com.realityexpander.tasky.common.AuthToken

interface IAuthRepository {

    suspend fun login(email: String, password: String): AuthToken?

    suspend fun register(email: String, password: String): AuthToken?
}