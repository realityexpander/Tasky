package com.realityexpander.tasky.auth_feature.data.repository.remote

import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.util.createAuthorizationHeader
import com.realityexpander.tasky.core.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import logcat.logcat

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
        //   2b. If not valid, attempt to get it from the authTokenRetriever (usually an AuthDao fun).
        //       Set authToken in Companion object, for faster access.
        fun getAuthToken(authTokenRetriever: (suspend () -> AuthToken?)? = null ): AuthToken? {
            return runBlocking(Dispatchers.IO) {

                // If valid, return it.
                IAuthApi.Companion.authToken ?: let {
                logcat { "AuthToken invalid - Attempt Retrieve AuthToken from repo" }

                    authTokenRetriever?.run {
                        IAuthApi.Companion.authToken = authTokenRetriever()
                    }
                }

                IAuthApi.Companion.authToken
            }
        }

        var authToken: String? = null
        var authUserId: UserId? = null

        var authorizationHeader: String? = null
            private set // only allow generating from Companion.authToken
            get() = createAuthorizationHeader(Companion.authToken)
    }
}