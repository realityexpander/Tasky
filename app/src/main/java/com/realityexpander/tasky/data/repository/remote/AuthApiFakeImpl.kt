package com.realityexpander.tasky.data.repository.remote

import com.realityexpander.tasky.common.*
import kotlinx.coroutines.delay

class AuthApiFakeImpl: IAuthApi {
    private var authToken: AuthToken? = null
    private val users = mutableMapOf<Email, Pair<Username,Password>>()

    init {
        users["chris@demo.com"] = "Chris Athanas" to "Password11"
        users["a@a.c"] = "Bilbo Baggins" to "1Zzzzzzzz"
    }

    override suspend fun login(email: String, password: String): AuthToken {
        delay(1000)

        if (users[email] == null) {
            throw Exceptions.LoginException("Unknown email")
        }

        return if(users[email]?.second == password) {
            AuthToken("token for $email")
        } else {
            throw Exceptions.WrongPasswordException()
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): AuthToken {
        delay(1000)

        return if(users[email] == null) {
            users[email] = username to password
            AuthToken("token for $email")
        } else {
            throw Exceptions.EmailAlreadyExistsException()
        }
    }
}