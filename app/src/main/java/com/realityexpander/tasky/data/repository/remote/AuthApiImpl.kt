package com.realityexpander.tasky.data.repository.remote

import com.realityexpander.tasky.common.AuthToken
import com.realityexpander.tasky.common.Email
import com.realityexpander.tasky.common.Exceptions
import com.realityexpander.tasky.common.Password
import kotlinx.coroutines.delay

class AuthApiImpl: IAuthApi {
    private var authToken: AuthToken? = null
    private val users = mutableMapOf<Email, Password>()

    init {
        users["chris@demo.com"] = "Password1"
        users["a@a.c"] = "1Zzzzz"
    }

    override suspend fun login(email: String, password: String): AuthToken {
        delay(1000)

        if (users[email] == null) {
            throw Exceptions.LoginException("Unknown email")
        }

        return if(users[email] == password) {
            AuthToken("token for $email")
        } else {
            throw Exceptions.LoginException("Invalid password")
        }
    }

    override suspend fun register(email: String, password: String): AuthToken {
        delay(1000)

        return if(users[email] == null) {
            users[email] = password
            AuthToken("token for $email")
        } else {
            throw Exceptions.EmailAlreadyExistsException()
        }
    }
}