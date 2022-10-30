package com.realityexpander.tasky.data.repository.remote

import com.realityexpander.tasky.BuildConfig
import com.realityexpander.tasky.common.AuthToken
import com.realityexpander.tasky.common.Email
import com.realityexpander.tasky.common.Password
import com.realityexpander.tasky.common.Username


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
            get() = "Bearer ${Companion.authToken}"

        @JvmName("setAuthToken1")
        fun setAuthToken(authToken: AuthToken?) {
            Companion.authToken = authToken
        }

        fun createAuthorizationHeader(authToken: String?): String {
            return "Bearer $authToken"
        }
    }
}