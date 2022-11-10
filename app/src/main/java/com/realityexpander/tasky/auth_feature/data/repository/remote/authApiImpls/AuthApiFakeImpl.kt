package com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls

import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.IAuthApi
import com.realityexpander.tasky.core.util.*
import kotlinx.coroutines.delay
import javax.inject.Inject

// Fake implementation of the API for testing purposes
// Has a delay of 1 second to simulate network latency.
// Uses fake tokens.
// Simulates server responses.

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
    ): @Suppress("RedundantNullableReturnType")
       AuthInfoDTO? {  // always returns non-null to maintain consistency with real API

        // simulate network call
        delay(1000)

        // Simulates a server side check/error for a valid email or password
        if (email.isBlank() || password.isBlank()) {
            throw Exceptions.LoginException("Invalid email or password")
        }

        // Simulates a server-side check/error for non-existing email
        if (users_onServer[email] == null) {
            throw Exceptions.LoginException("Unknown email")
        }

        if(users_onServer[email]?.password != password) {
            throw Exceptions.WrongPasswordException()
        }

        return AuthInfoDTO(
            authToken(generateAuthToken_onServer(email)),
            userId(generateUserId_onServer(email)),
            username(users_onServer[email]?.username ?: "Username Missing")
        )
    }

    override suspend fun register(
        username: Username,
        email: Email,
        password: Password
    ) {
        // Simulate network call
        delay(1000)

        // Simulates a server-side check/error for blank email or password
        if (email.isBlank() || password.isBlank() || username.isBlank()) {
            throw Exceptions.RegisterException("Invalid username, email or password")
        }

        // Simulates a server-side check/error for duplicate email
        if (users_onServer[email] != null) {
            throw Exceptions.EmailAlreadyExistsException()
        }

        users_onServer[email] = AuthInfoFakeEntity(
            username = username,
            authToken = generateAuthToken_onServer(email),
            userId = generateUserId_onServer(email),
            password = password,
        )
        AuthInfoDTO(
            authToken(generateAuthToken_onServer(email)),
            userId(generateUserId_onServer(email)),
            username(users_onServer[email]?.username ?: "Username Missing")
        )
    }

    // Authenticate the logged-in user
    override suspend fun authenticate(): Boolean {
        // Simulate network call
        delay(500)

        return authenticateAuthToken(IAuthApi.authToken)
    }

    // Authenticate any token
    override suspend fun authenticateAuthToken(
        authToken: AuthToken?
    ): Boolean {
        // simulate network call
        delay(500)

        // Simulate server-side check
        authToken?.let {
            // Check the passed-in authToken against the fake user database
            if (users_onServer.map { it.value.authToken }.contains(authToken)) {
                return true
            }
        }

        return false
    }

    override suspend fun logout(): Boolean {
        return expireToken_onServer(IAuthApi.authToken)
    }

    /////////////// Server simulation functions //////////////////////

    private val users_onServer =
        mutableMapOf<Email, AuthInfoFakeEntity>()

    init {
        // Setup fake users
        users_onServer["chris@demo.com"] =
            AuthInfoFakeEntity(
                username = "Chris Athanas",
                authToken = generateAuthToken_onServer("chris@demo.com"),
                userId = generateUserId_onServer("chris@demo.com") ,
                password = "Password1",
            )
        users_onServer["a@aa.com"] =
            AuthInfoFakeEntity(
                username = "Bilbo Baggins",
                authToken = generateAuthToken_onServer("a@aa.com"),
                userId = generateUserId_onServer("a@aa.com"),
                password = "Password1",
            )
    }

    private fun generateAuthToken_onServer(email: Email): AuthToken {
        return authToken("token for $email") as AuthToken
    }

    private fun generateUserId_onServer(email: Email): UserId {
        return userId("id for $email") as UserId
    }

    fun expireTokenByEmail_onServer(email: Email) {
        users_onServer[email]?.authToken = null
    }

    private fun expireToken_onServer(authToken: AuthToken?): Boolean {
        users_onServer.filter { entry ->
            entry.value.authToken == authToken
        }.forEach { entry ->
            entry.value.authToken = null
        }

        return true
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
    authApiFakeImpl.setAuthToken(user?.authToken) // set the AuthToken for the logged-in user

    println("user=$user")

    println("AuthApiFakeImpl.authenticate() Logged in user")
    println("  user.authToken is valid=" +
                assert(authApiFakeImpl.authenticate()))
    println("  logged-in user authToken is valid=" +
                assert(authApiFakeImpl.authenticate()))

    println()

    val authTokenOnServer = user?.authToken
    authApiFakeImpl.clearAuthToken() // clear the AuthToken for the logged-in user
    println("AuthApiFakeImpl.authenticate() Logged out user has no authToken")
    println("   IAuthApi.authToken=null, token is NOT valid locally=" +
                assert(!authApiFakeImpl.authenticate()))
    println("   user.authToken still valid on server=" +
                assert(authApiFakeImpl.authenticateAuthToken(authTokenOnServer)))

    println()

    user = authApiFakeImpl.login("chris@demo.com", "Password1")
    authApiFakeImpl.expireTokenByEmail_onServer("chris@demo.com") // server expires the token
    println("AuthApiFakeImpl.authenticate() Expired token (server side) when user is still logged in")
    println("   logged-in user authToken is valid & not null=" +
                assert(user?.authToken != null))
    println("   user.authToken is INVALID on server=" +
                assert(!authApiFakeImpl.authenticate()))
    println("   logged-in user is NOT able to authenticate their token=" +
                assert(!authApiFakeImpl.authenticate()))

    println()

    println("AuthApiFakeImpl.authenticateAuthToken() Invalid token")
    println("   invalid authToken is INVALID=" +
                assert(!authApiFakeImpl.authenticateAuthToken("invalid token")))

    println()

    println("AuthApiFakeImpl.authenticateAuthToken() Null token")
    println("   null authToken is INVALID=" +
                assert(!authApiFakeImpl.authenticateAuthToken(null)))

    println()

    println("AuthApiFakeImpl.authenticateAuthToken() Blank token")
    println("   blank authToken is INVALID=" +
                assert(!authApiFakeImpl.authenticateAuthToken(authToken(""))))

    println()
}