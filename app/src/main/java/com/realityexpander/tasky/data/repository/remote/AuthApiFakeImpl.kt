package com.realityexpander.tasky.data.repository.remote

import com.realityexpander.tasky.common.*
import com.realityexpander.tasky.data.repository.AuthInfo
import kotlinx.coroutines.delay

// Fake implementation of the API for testing purposes
// Has a delay of 1 second to simulate network latency.
// Uses fake tokens.

class AuthApiFakeImpl: IAuthApi {
    private var authToken: AuthInfo? = null
    private val users =
        mutableMapOf<Email, Pair<Username,Password>>()

    init {
        users["chris@demo.com"] = "Chris Athanas" to "Password11"
        users["a@a.c"] = "Bilbo Baggins" to "1Zzzzzzzz"
    }

    override suspend fun login(email: Email, password: Password): AuthInfo {
        if (email.isBlank() || password.isBlank()) {
            throw Exceptions.LoginException("Invalid email or password")
        }

        delay(1000) // simulate network call

        if (users[email] == null) {
            throw Exceptions.LoginException("Unknown email")
        }

        if(users[email]?.second != password) {
            throw Exceptions.WrongPasswordException()
        }

        return AuthInfo(
            AuthToken("token for $email"),
            UserId("id for $email"),
            Username(users[email]?.first ?: "Unknown")
        )
    }

    override suspend fun register(
        username: Username,
        email: Email,
        password: Password
    ) {
        if (email.isBlank() || password.isBlank() || username.isBlank()) {
            throw Exceptions.RegisterException("Invalid username, email or password")
        }

        delay(1000) // simulate network call

        if (users[email] != null) {
            throw Exceptions.EmailAlreadyExistsException()
        }

        users[email] = username to password
        AuthInfo(
            AuthToken("token for $email"),
            UserId("id for $email"),
            Username(username)
        )
    }
}