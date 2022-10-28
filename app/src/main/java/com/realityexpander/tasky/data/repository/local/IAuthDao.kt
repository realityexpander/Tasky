package com.realityexpander.tasky.data.repository.local

import com.realityexpander.tasky.common.AuthToken
import com.realityexpander.tasky.common.UserId
import com.realityexpander.tasky.common.Username
import com.realityexpander.tasky.data.repository.AuthInfo

interface IAuthDao {
    suspend fun getAuthToken(): AuthToken

    suspend fun setAuthToken(authToken: AuthToken)

    suspend fun getAuthUsername(): Username

    suspend fun setAuthUsername(username: Username)

    suspend fun getAuthUserId(): UserId

    suspend fun setAuthUserId(userId: UserId)

    suspend fun clearAuthToken()

    suspend fun getAuthInfo(): AuthInfo

    suspend fun setAuthInfo(authInfo: AuthInfo)

    suspend fun clearAuthInfo()
}