package com.realityexpander.tasky.auth_feature.data.repository.local

import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.util.AuthToken
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.Username

interface IAuthDao {
    suspend fun getAuthToken(): AuthToken?

    suspend fun setAuthToken(authToken: AuthToken?)

    suspend fun getAuthUsername(): Username?

    suspend fun setAuthUsername(username: Username?)

    suspend fun getAuthUserId(): UserId?

    suspend fun setAuthUserId(userId: UserId?)

    suspend fun clearAuthToken()

    suspend fun getAuthInfo(): AuthInfo?

    suspend fun setAuthInfo(authInfo: AuthInfo?)

    suspend fun clearAuthInfo()
}