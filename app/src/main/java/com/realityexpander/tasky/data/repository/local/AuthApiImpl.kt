package com.realityexpander.tasky.data.repository.local

import com.realityexpander.tasky.common.AuthToken
import com.realityexpander.tasky.common.Email
import com.realityexpander.tasky.common.Exceptions
import com.realityexpander.tasky.common.Password

class AuthApiImpl: IAuthApi {
    private var authToken: AuthToken? = null
    private val users = mutableMapOf<Email, Password>()

    init {
        users["chris@demo.com"] = "password"
        users["a@a.c"] = "zzzzzz"
    }

    override suspend fun login(email: String, password: String): AuthToken {
        return if(users[email] == password) {
            AuthToken("token for $email")
        } else {
            throw Exceptions.LoginException("Invalid email or password")
        }
    }

    override suspend fun register(email: String, password: String): AuthToken {
        return if(users[email] == null) {
            users[email] = password
            AuthToken("token for $email")
        } else {
            throw Exceptions.EmailAlreadyExistsException()
        }
    }
}