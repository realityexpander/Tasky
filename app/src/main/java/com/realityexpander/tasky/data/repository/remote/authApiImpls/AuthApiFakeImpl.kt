package com.realityexpander.tasky.data.repository.remote.authApiImpls

import com.realityexpander.tasky.common.*
import com.realityexpander.tasky.data.repository.AuthInfoDTO
import com.realityexpander.tasky.data.repository.remote.IAuthApi
import kotlinx.coroutines.delay
import javax.inject.Inject

// Fake implementation of the API for testing purposes
// Has a delay of 1 second to simulate network latency.
// Uses fake tokens.

data class AuthInfoFakeEntity(
    val authToken: AuthToken? = null,
    val userId: UserId? = null,
    val username: Username? = null,
    val password: Password? = null
)

class AuthApiFakeImpl @Inject constructor(): IAuthApi {
    private val users =
        mutableMapOf<Email, AuthInfoFakeEntity>()

    init {
        // Setup fake users
        users["chris@demo.com"] =
            AuthInfoFakeEntity(
                username = "Chris Athanas",
                authToken = getAuthToken("chris@demo.com"),
                userId = getUserId("chris@demo.com") ,
                password = "Password1",
            )
        users["a@aa.com"] =
            AuthInfoFakeEntity(
                username = "Bilbo Baggins",
                authToken = getAuthToken("a@aa.com"),
                userId = getUserId("a@aa.com"),
                password = "Password1",
            )
    }

    override suspend fun login(email: Email, password: Password): AuthInfoDTO {
        if (email.isBlank() || password.isBlank()) {
            throw Exceptions.LoginException("Invalid email or password")
        }

        // simulate network call
        delay(1000)

        if (users[email] == null) {
            throw Exceptions.LoginException("Unknown email")
        }

        if(users[email]?.password != password) {
            throw Exceptions.WrongPasswordException()
        }

        return AuthInfoDTO(
            authToken(getAuthToken(email)),
            userId(getUserId(email)),
            username(users[email]?.username ?: "Username Missing")
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

        // simulate network call
        delay(1000)

        if (users[email] != null) {
            throw Exceptions.EmailAlreadyExistsException()
        }

        users[email] = AuthInfoFakeEntity(
            username = username,
            authToken = getAuthToken(email),
            userId = getUserId(email),
            password = password,
        )
        AuthInfoDTO(
            authToken(getAuthToken(email)),
            userId(getUserId(email)),
            username(users[email]?.username ?: "Username Missing")
        )
    }

    private fun getAuthToken(email: Email): AuthToken {
        return authToken("token for $email") as AuthToken
    }

    private fun getUserId(email: Email): UserId {
        return userId("id for $email") as UserId
    }
}