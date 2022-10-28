package com.realityexpander.tasky.data.repository.remote

import com.realityexpander.tasky.common.*
import com.realityexpander.tasky.data.repository.AuthInfo
import kotlinx.coroutines.delay

class AuthApiFakeImpl: IAuthApi {
    private var authToken: AuthInfo? = null
    private val users =
        mutableMapOf<Email, Pair<Username,Password>>()

    init {
        users["chris@demo.com"] = "Chris Athanas" to "Password11"
        users["a@a.c"] = "Bilbo Baggins" to "1Zzzzzzzz"
    }

    override suspend fun login(email: Email, password: Password): AuthInfo {
        delay(1000)

        if (users[email] == null) {
            throw Exceptions.LoginException("Unknown email")
        }

        return if(users[email]?.second == password) {
            AuthInfo(
                AuthToken("token for $email"),
                UserId("id for $email"),
                Username(users[email]?.first ?: "Unknown")
            )
        } else {
            throw Exceptions.WrongPasswordException()
        }
    }

    override suspend fun register(
        username: Username,
        email: Email,
        password: Password
    ): AuthInfo {
        delay(1000)

        return if(users[email] == null) {
            users[email] = username to password
            AuthInfo(
                AuthToken("token for $email"),
                UserId("id for $email"),
                Username(username)
            )
        } else {
            throw Exceptions.EmailAlreadyExistsException()
        }
    }
}