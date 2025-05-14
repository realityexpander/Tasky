@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls

import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.IAuthApi
import com.realityexpander.tasky.core.util.*
import kotlinx.coroutines.delay
import javax.inject.Inject

// Fake implementation of the API for testing purposes
// Has a delay of .1-.5 second to simulate network latency.
// Uses fake tokens.
// Simulates server responses.

data class AuthInfoFakeEntity(
    var accessToken: AccessToken? = null,
    val userId: UserId? = null,
    val username: Username? = null,
    val password: Password? = null,
    val email: Email? = null,
    val refreshToken: RefreshToken? = null,
    val accessTokenExpirationTimestampEpochMilli: Long? = null,
    val authenticatedUserId: UserId? = null,
)


class AuthApiFake @Inject constructor(): IAuthApi {

    /////////////////// FAKE API IMPLEMENTATION ///////////////////////

    override suspend fun login(
        email: Email,
        password: Password
    ): @Suppress("RedundantNullableReturnType")
       AuthInfoDTO? {  // always returns non-null to maintain consistency with real API

        // simulate network call
        delay(500)

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
            accessToken(generateAuthToken_onServer(email)),
            userId = userId(generateUserId_onServer(email)),
            username = username(users_onServer[email]?.username ?: "Username Missing")
        )
    }

    override suspend fun register(
        username: Username,
        email: Email,
        password: Password
    ) {
        // Simulate network call
        delay(500)

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
            accessToken = generateAuthToken_onServer(email),
            userId = generateUserId_onServer(email),
            password = password,
        )
        AuthInfoDTO(
            accessToken(generateAuthToken_onServer(email)),
            userId = userId(generateUserId_onServer(email)),
            username = username(users_onServer[email]?.username ?: "Username Missing")
        )
    }

    // Authenticate the logged-in user
    override suspend fun authenticate(): Boolean {
        // Simulate network call
        delay(500)

        return authenticateAccessToken(IAuthApi.accessToken)
    }

    // Authenticate any token
    override suspend fun authenticateAccessToken(
        accessToken: AccessToken?
    ): Boolean {
        // simulate network call
        delay(500)

        // Simulate server-side check
        accessToken?.let {
            // Check the passed-in authToken against the fake user database
            if (users_onServer.map { it.value.accessToken }.contains(accessToken)) {
                return true
            }
        }

        return false
    }

    suspend fun refreshAccessToken() {
        // Simulate network call
        delay(100)

        // Simulate server-side check
        IAuthApi.refreshToken?.let { token ->
            if (users_onServer.map { user ->
                user.value.accessToken
            }.contains(token)) {
                // Simulate a new token
                val newToken = generateAuthToken_onServer("Refresh Token " + System.currentTimeMillis().toString())
                users_onServer.forEach { entry ->
                    if (entry.value.accessToken == token) {
                        entry.value.accessToken = newToken
                    }
                }
                return
            }
        }

        throw Exceptions.NetworkException("401 Unauthorized")
    }

    fun setAccessToken(accessToken: AccessToken?) {
        IAuthApi.accessToken = accessToken
    }

    override suspend fun logout() {
        // simulate network call
        delay(100)

        expireToken_onServer(IAuthApi.accessToken)
    }

    /////////////// Server simulation functions //////////////////////

    private val users_onServer =
        mutableMapOf<Email, AuthInfoFakeEntity>()

    init {
        // Setup fake users
        users_onServer["chris@demo.com"] =
            AuthInfoFakeEntity(
                username = "Chris Athanas",
                accessToken = generateAuthToken_onServer("chris@demo.com"),
                userId = generateUserId_onServer("chris@demo.com") ,
                password = "Password1",
                refreshToken = "refresh token 1234",
                accessTokenExpirationTimestampEpochMilli = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7,
                email = "chris@demo.com",
            )
        users_onServer["a@aa.com"] =
            AuthInfoFakeEntity(
                username = "Bilbo Baggins",
                accessToken = generateAuthToken_onServer("a@aa.com"),
                userId = generateUserId_onServer("a@aa.com"),
                password = "Password1",
                refreshToken = "refresh token 5678",
                accessTokenExpirationTimestampEpochMilli = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7,
                email = "a@aa.com",
            )
    }

    private fun generateAuthToken_onServer(email: Email): AccessToken {
        return accessToken("token for $email") as AccessToken
    }

    private fun generateUserId_onServer(email: Email): UserId {
        return userId("id for $email") as UserId
    }

    fun expireTokenByEmail_onServer(email: Email) {
        users_onServer[email]?.accessToken = null
    }

    private fun expireToken_onServer(accessToken: AccessToken?) {
        users_onServer.filter { entry ->
            entry.value.accessToken == accessToken
        }.forEach { entry ->
            entry.value.accessToken = null
        }

        // todo should throw error if token not found?
    }
}

// Test the AuthApiFake implementation
suspend fun main() {

    fun assert(condition: Boolean): String {
        return if (condition) {
            "True"
        } else {
            "False"
        }
    }

    val authApiFake = AuthApiFake()
    var user = authApiFake.login("chris@demo.com", "Password1")
    authApiFake.setAccessToken(user?.accessToken) // set the AuthToken for the logged-in user

    println("user=$user")

    println("AuthApiFakeImpl.authenticate() Logged in user")
    println("  user.authToken is valid=" +
                assert(authApiFake.authenticate()))
    println("  logged-in user authToken is valid=" +
                assert(authApiFake.authenticate()))

    println()

    val authTokenOnServer = user?.accessToken
    authApiFake.clearAuthInfo() // clear the AuthToken for the logged-in user
    println("AuthApiFakeImpl.authenticate() Logged out user has no authToken")
    println("   IAuthApi.authToken=null, token is NOT valid locally=" +
                assert(!authApiFake.authenticate()))
    println("   user.authToken still valid on server=" +
                assert(authApiFake.authenticateAccessToken(authTokenOnServer)))

    println()

    user = authApiFake.login("chris@demo.com", "Password1")
    authApiFake.expireTokenByEmail_onServer("chris@demo.com") // server expires the token
    println("AuthApiFakeImpl.authenticate() Expired token (server side) when user is still logged in")
    println("   logged-in user authToken is valid & not null=" +
                assert(user?.accessToken != null))
    println("   user.authToken is INVALID on server=" +
                assert(!authApiFake.authenticate()))
    println("   logged-in user is NOT able to authenticate their token=" +
                assert(!authApiFake.authenticate()))

    println()

    println("AuthApiFakeImpl.authenticateAuthToken() Invalid token")
    println("   invalid authToken is INVALID=" +
                assert(!authApiFake.authenticateAccessToken("invalid token")))

    println()

    println("AuthApiFakeImpl.authenticateAuthToken() Null token")
    println("   null authToken is INVALID=" +
                assert(!authApiFake.authenticateAccessToken(null)))

    println()

    println("AuthApiFakeImpl.authenticateAuthToken() Blank token")
    println("   blank authToken is INVALID=" +
                assert(!authApiFake.authenticateAccessToken(accessToken(""))))

    println()
}
