package com.realityexpander.tasky.auth_feature.data.repository.remote

import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.util.createAuthorizationHeader
import com.realityexpander.tasky.core.util.AuthToken
import com.realityexpander.tasky.core.util.Email
import com.realityexpander.tasky.core.util.Password
import com.realityexpander.tasky.core.util.Username

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

    fun setAuthToken(authToken: AuthToken?) {
        IAuthApi.Companion.authToken = authToken
    }

    fun clearAuthToken() {
        IAuthApi.Companion.authToken = null
    }

    companion object {
        var authToken: String? = null
//            private set

        var authorizationHeader: String? = null
            private set // only allow generating from Companion.authToken
            get() = createAuthorizationHeader(Companion.authToken)
    }
}