package com.realityexpander.tasky.auth_feature.data.repository.local

import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.util.AccessToken
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.Username
import kotlinx.serialization.InternalSerializationApi


@OptIn(InternalSerializationApi::class)
interface IAuthDao {
    suspend fun getAccessToken(): AccessToken?

    suspend fun setAccessToken(accessToken: AccessToken?)

    suspend fun getAccessTokenExpirationTimestampEpochMilli(): Long?

    suspend fun setAccessTokenExpirationTimestampEpochMilli(accessTokenExpirationTimestampEpochMilli: Long)

    suspend fun getRefreshToken(): String?

    suspend fun setRefreshToken(refreshToken: String?)

    suspend fun getAuthUsername(): Username?

    suspend fun setAuthUsername(username: Username?)

    suspend fun getAuthUserId(): UserId?

    suspend fun setAuthUserId(userId: UserId?)

    suspend fun clearAuthToken()

    suspend fun getAuthInfo(): AuthInfo?

    suspend fun setAuthInfo(authInfo: AuthInfo?)

    suspend fun clearAuthInfo()
}
