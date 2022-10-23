package com.realityexpander.tasky.data.repository.remote

import com.realityexpander.tasky.common.AuthToken

interface IAuthDao {
    suspend fun getAuthToken(): AuthToken

    suspend fun setAuthToken(authToken: AuthToken)
}