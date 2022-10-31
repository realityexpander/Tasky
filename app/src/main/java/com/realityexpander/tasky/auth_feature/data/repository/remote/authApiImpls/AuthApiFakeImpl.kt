package com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls

import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.IAuthApi
import com.realityexpander.tasky.core.common.*
import kotlinx.coroutines.delay
import javax.inject.Inject

// Fake implementation of the API for testing purposes
// Has a delay of 1 second to simulate network latency.
// Uses fake tokens.

data class AuthInfoFakeEntity(
    var authToken: AuthToken? = null,
    val userId: UserId? = null,
    val username: Username? = null,
    val password: Password? = null
)


class AuthApiFakeImpl @Inject constructor(): IAuthApi {

    /////////////////// FAKE API IMPLEMENTATION ///////////////////////

    override suspend fun login(
        email: Email,
        password: Password
    ): AuthInfoDTO {
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
            authToken(generateAuthToken(email)),
            userId(generateUserId(email)),
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
            authToken = generateAuthToken(email),
            userId = generateUserId(email),
            password = password,
        )
        AuthInfoDTO(
            authToken(generateAuthToken(email)),
            userId(generateUserId(email)),
            username(users[email]?.username ?: "Username Missing")
        )
    }

    override suspend fun authenticate(authToken: AuthToken?): Boolean {
        // simulate network call
        delay(500)

        authToken?.let {
            // Check the passed-in authToken against the fake tokens
            if (users.map { it.value.authToken }.contains(authToken)) {
                return true
            }
        } ?: run {
            // Check the logged-in user token if no token is provided
            if(IAuthApi.authToken != null
                && (IAuthApi.authToken?.startsWith("token") == true)
            ) {
                return true
            }
        }

        return false
    }

    /////////////// Server simulation functions //////////////////////

    private val users =
        mutableMapOf<Email, AuthInfoFakeEntity>()

    init {
        // Setup fake users
        users["chris@demo.com"] =
            AuthInfoFakeEntity(
                username = "Chris Athanas",
                authToken = generateAuthToken("chris@demo.com"),
                userId = generateUserId("chris@demo.com") ,
                password = "Password1",
            )
        users["a@aa.com"] =
            AuthInfoFakeEntity(
                username = "Bilbo Baggins",
                authToken = generateAuthToken("a@aa.com"),
                userId = generateUserId("a@aa.com"),
                password = "Password1",
            )
    }

    private fun generateAuthToken(email: Email): AuthToken {
        return authToken("token for $email") as AuthToken
    }

    private fun generateUserId(email: Email): UserId {
        return userId("id for $email") as UserId
    }

    fun expireToken(email: Email) {
        users[email]?.authToken = null
    }
}

suspend fun main() {

    fun assert(condition: Boolean): String {
        return if (condition) {
            "True"
        } else {
            "False"
        }
    }

    val authApiFakeImpl = AuthApiFakeImpl()
    var user = authApiFakeImpl.login("chris@demo.com", "Password1")
    IAuthApi.setAuthToken(user.authToken)

    println("user=$user")

    println("AuthApiFakeImpl.authenticate() Logged in user")
    println("  user.authToken is valid=" + assert(authApiFakeImpl.authenticate(user.authToken)))
    println("  logged-in user authToken is valid=" + assert(authApiFakeImpl.authenticate()))

    println()

    IAuthApi.setAuthToken(null)
    println("AuthApiFakeImpl.authenticate() Logged out user has no authToken")
    println("   IAuthApi.authToken=null, token is NOT valid locally=" + assert(!authApiFakeImpl.authenticate()))
    println("   user.authToken still valid on server=" + assert(authApiFakeImpl.authenticate(user.authToken)))

    println()

    user = authApiFakeImpl.login("chris@demo.com", "Password1")
    authApiFakeImpl.expireToken("chris@demo.com") // server expires the token
    println("AuthApiFakeImpl.authenticate() Expired token (server side) when user is still logged in")
    println("   logged-in user authToken is valid & not null=" + assert(user.authToken != null))
    println("   user.authToken is INVALID on server=" + assert(!authApiFakeImpl.authenticate(user.authToken)))
    println("   logged-in user is NOT able to authenticate their token=" + assert(!authApiFakeImpl.authenticate()))

    println()

    println("AuthApiFakeImpl.authenticate() Invalid token")
    println("   invalid authToken is INVALID=" + assert(!authApiFakeImpl.authenticate(authToken("invalid token"))))

    println()

    println("AuthApiFakeImpl.authenticate() Null token")
    println("   null authToken is INVALID=" + assert(!authApiFakeImpl.authenticate(null)))

    println()

    println("AuthApiFakeImpl.authenticate() Blank token")
    println("   blank authToken is INVALID=" + assert(!authApiFakeImpl.authenticate(authToken(""))))

    println()
}