package com.realityexpander.tasky.auth_feature.data.repository.remote

import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import logcat.logcat

@Suppress("RemoveRedundantQualifierName", "RedundantCompanionReference")
interface IAuthApi {
    suspend fun login(
        email: Email,
        password: Password
    ): AuthInfoDTO?

    suspend fun register(
        username: Username,
        email: Email,
        password: Password
    )

    suspend fun authenticate(): Boolean // uses the AuthToken stored in this companion object
    suspend fun authenticateAccessToken(accessToken: AccessToken?): Boolean
//    suspend fun refreshAccessToken()

    fun setAuthInfo(authInfo: AuthInfo?) {
        if(authInfo == null) {
            clearAuthInfo()
            return
        }

        IAuthApi.Companion.setAuthInfo(authInfo)
    }

    fun getAuthInfo(): AuthInfo? {
        return IAuthApi.Companion.getAuthInfo()
    }

    fun clearAuthInfo() {
        IAuthApi.Companion.clearAuthInfo()
    }

    suspend fun logout(
        // uses the AuthToken stored in this companion object
    )

    companion object {
        var accessToken: String? = null
        var accessTokenExpirationTimestampEpochMilli: Long? = null
        var refreshToken: String? = null
        var authenticatedUserId: UserId? = null

        // 1. Check for a valid AuthToken in the IAuthApi Companion object.
        //   2a. If valid, return it.
        //   2b. If not valid, attempt to fetch it from the authTokenRetriever (usually an AuthDao fun).
        //       Set authToken in Companion object, for faster access.
        fun getAccessToken(accessTokenRetriever: (suspend () -> AccessToken?)? = null ): AccessToken? {
            // 2a - if valid, return it
            IAuthApi.Companion.accessToken?.let { return it }

            // 2b - Attempt to fetch it using passed in function
            return runBlocking(Dispatchers.IO) {
                logcat { "AuthToken invalid - Attempt Retrieve AuthToken from repo" }
                accessTokenRetriever?.run {
                    IAuthApi.Companion.accessToken = accessTokenRetriever()
                }

                IAuthApi.Companion.accessToken
            }
        }

        fun createAuthorizationAccessTokenString(accessToken: String?): String {
            return "Bearer ${accessToken ?: "NULL_ACCESS_TOKEN"}"
        }

        fun setAuthInfo(authInfo: AuthInfo?) {
            if(authInfo == null) {
                clearAuthInfo()
                return
            }

            IAuthApi.Companion.accessToken = authInfo.accessToken
            IAuthApi.Companion.refreshToken = authInfo.refreshToken
            IAuthApi.Companion.accessTokenExpirationTimestampEpochMilli = authInfo.accessTokenExpirationTimestampEpochMilli
            IAuthApi.Companion.authenticatedUserId = authInfo.userId
        }

        fun getAuthInfo(): AuthInfo? {
            return if(
                accessToken != null &&
                refreshToken != null &&
                accessTokenExpirationTimestampEpochMilli != null &&
                authenticatedUserId != null
            ) {
                AuthInfo(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    accessTokenExpirationTimestampEpochMilli = accessTokenExpirationTimestampEpochMilli!!,
                    userId = authenticatedUserId
                )
            } else {
                null
            }
        }

        fun clearAuthInfo() {
            IAuthApi.Companion.accessToken = null
            IAuthApi.Companion.authenticatedUserId = null
            IAuthApi.Companion.refreshToken = null
            IAuthApi.Companion.accessTokenExpirationTimestampEpochMilli = null
        }

        fun clearRefreshToken() {
            IAuthApi.Companion.refreshToken = null
        }
    }

    suspend fun refreshAccessToken(userId: UserId, refreshToken: RefreshToken) { /* THIS IS NO-OP STUB FOR TESTING */ }
}
