package com.realityexpander.tasky.auth_feature.data.repository.remote

import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
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
    suspend fun authenticateAuthToken(authToken: AuthToken?): Boolean

    suspend fun logout(
        // uses the AuthToken stored in this companion object
    )

    fun setAuthToken(authToken: AuthToken?) {

        IAuthApi.Companion.authToken = authToken
    }

    fun clearAuthToken() {
        IAuthApi.Companion.authToken = null
        IAuthApi.Companion.authUserId = null
    }

    fun setAuthUserId(authUserId: UserId?) {
        IAuthApi.Companion.authUserId = authUserId
    }

    companion object {

        // 1. Check for a valid AuthToken in the IAuthApi Companion object.
        //   2a. If valid, return it.
        //   2b. If not valid, attempt to fetch it from the authTokenRetriever (usually an AuthDao fun).
        //       Set authToken in Companion object, for faster access.
        fun getAuthToken(authTokenRetriever: (suspend () -> AuthToken?)? = null ): AuthToken? {
            // 2a - if valid, return it
            IAuthApi.Companion.authToken?.let { return it }

            // 2b - Attempt to fetch it
            return runBlocking(Dispatchers.IO) {
                logcat { "AuthToken invalid - Attempt Retrieve AuthToken from repo" }
                authTokenRetriever?.run {
                    IAuthApi.Companion.authToken = authTokenRetriever()
                }

                IAuthApi.Companion.authToken
            }
        }

        fun createBearerTokenString(authToken: String?): String {
            return "Bearer ${authToken ?: "NULL_AUTH_TOKEN"}"
        }

        var authToken: String? = null
        var authUserId: UserId? = null
    }
}