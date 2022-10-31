package com.realityexpander.tasky.auth_feature.data.repository.remote

import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
import com.realityexpander.tasky.core.common.AuthToken
import com.realityexpander.tasky.core.common.Email
import com.realityexpander.tasky.core.common.Password
import com.realityexpander.tasky.core.common.Username


interface IAuthApi {
    suspend fun login(
        email: Email,
        password: Password
    ): AuthInfoDTO

    suspend fun register(
        username: Username,
        email: Email,
        password: Password
    )

    suspend fun authenticate(authToken: AuthToken? = null): Boolean

    companion object {
        var authToken: String? = null
            private set

        var authorizationHeader: String? = null
            private set
            get() = createAuthorizationHeader(Companion.authToken)

        @JvmName("setAuthToken1")
        @JvmStatic
        fun setAuthToken(authToken: AuthToken?) {
            Companion.authToken = authToken
        }

        fun createAuthorizationHeader(authToken: String?): String {
            return "Bearer $authToken"
        }
    }
}