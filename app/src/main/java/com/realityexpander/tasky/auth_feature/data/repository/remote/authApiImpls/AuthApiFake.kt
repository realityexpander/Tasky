@file:Suppress("FunctionName")

package com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.IAuthApi
import com.realityexpander.tasky.core.util.AccessToken
import com.realityexpander.tasky.core.util.Email
import com.realityexpander.tasky.core.util.Exceptions
import com.realityexpander.tasky.core.util.Password
import com.realityexpander.tasky.core.util.RefreshToken
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.Username
import com.realityexpander.tasky.core.util.accessToken
import com.realityexpander.tasky.core.util.refreshToken
import com.realityexpander.tasky.core.util.userId
import com.realityexpander.tasky.core.util.username
import kotlinx.coroutines.delay
import java.util.Date
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
    var refreshToken: RefreshToken? = null,
    var accessTokenExpirationTimestampEpochMilli: Long? = null,
    var authenticatedUserId: UserId? = null,
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

        // Simulates a server-side check/error for invalid password
        if(users_onServer[email]?.password != password) {
            throw Exceptions.WrongPasswordException()
        }

        // Simulate setting the refresh token & access token expiration date
        users_onServer[email]?.accessToken = accessToken(generateAccessToken_onServer(email))
        users_onServer[email]?.refreshToken = refreshToken(generateRefreshToken_onServer(email))
        users_onServer[email]?.accessTokenExpirationTimestampEpochMilli =
            System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 7L // 7 days

        // Simulate setting the authenticated userId on device
        IAuthApi.accessToken = users_onServer[email]?.accessToken
        IAuthApi.refreshToken = users_onServer[email]?.refreshToken
        IAuthApi.accessTokenExpirationTimestampEpochMilli =
            users_onServer[email]?.accessTokenExpirationTimestampEpochMilli

        return AuthInfoDTO(
            accessToken = users_onServer[email]?.accessToken,
            userId = userId(generateUserId_onServer(email)),
            username = username(users_onServer[email]?.username ?: "Username Missing"),
            refreshToken = users_onServer[email]?.refreshToken,
            accessTokenExpirationTimestampEpochMilli = users_onServer[email]?.accessTokenExpirationTimestampEpochMilli,
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
            accessToken = generateAccessToken_onServer(email),
            refreshToken = generateRefreshToken_onServer(email),
            accessTokenExpirationTimestampEpochMilli = System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 7L, // 7 days
            userId = generateUserId_onServer(email),
            password = password,
        )
        AuthInfoDTO(
            userId = userId(generateUserId_onServer(email)),
            username = username(users_onServer[email]?.username ?: "Username Missing"),
            accessToken = accessToken(generateAccessToken_onServer(email)),
            refreshToken = refreshToken(generateRefreshToken_onServer(email)),
            accessTokenExpirationTimestampEpochMilli = users_onServer[email]?.accessTokenExpirationTimestampEpochMilli,
        )
    }

    // Authenticate the logged-in user
    override suspend fun authenticate(): Boolean {
        return authenticateAccessToken(IAuthApi.accessToken)
    }

    // Authenticate passed-in token
    override suspend fun authenticateAccessToken(
        accessToken: AccessToken?
    ): Boolean {
        // simulate network call
        delay(100)

        // Simulate server-side check
        accessToken?.let {
            // Check the passed-in authToken against the fake user database
            if (users_onServer.map { user ->
                    user.value.accessToken
            }.contains(accessToken)) {
                return true
            }
        }

        return false
    }

    override suspend fun refreshAccessToken(
        userId: UserId,
        refreshToken: RefreshToken
    ) {
        // Simulate network call
        delay(100)

        // Simulate server-side check
        IAuthApi.refreshToken?.let { loggedInRefreshToken ->
            if (users_onServer.map { user ->
                user.value.refreshToken
            }.contains(loggedInRefreshToken)) {
                // Simulate updating the user with a new RefreshToken
                users_onServer.forEach { user ->
                    if (user.value.refreshToken == loggedInRefreshToken) {
                        val newAccessToken = generateAccessToken_onServer(user.value.email!!)

                        user.value.accessToken = newAccessToken
                        IAuthApi.accessToken = newAccessToken
                        return
                    }
                }
            }
        }

        throw Exceptions.NetworkException("401 Unauthorized")
    }

    override suspend fun logout() {
        // simulate network call
        delay(100)

        expireAccessToken_onServer(IAuthApi.accessToken)
    }

    fun setAccessToken(accessToken: AccessToken?) {
        IAuthApi.accessToken = accessToken
    }

    /////////////// Server simulation functions //////////////////////

    private val users_onServer =
        mutableMapOf<Email, AuthInfoFakeEntity>()

    data class TokenConfig(
        val issuer: String,
        val audience: String,
        val expiresIn: Long,
        val secret: String
    )

    val tokenConfig = TokenConfig(
        issuer = "http://localhost:8080", // environmentVariables.jwtIssuer
        audience = "users", // environmentVariables.jwtAudience
//        expiresIn = 60L * 60L * 1000L, // 1 hour
        expiresIn = 10L * 1000L, // 10 seconds
        secret = "sa89HBNasdf(/32b" // environmentVariables.jwtSecret
    )

    init {
        // Setup fake users
        val usersEmail = listOf(
            "chris@demo.com",
            "a@aa.com"
        )
        users_onServer["chris@demo.com"] =
            AuthInfoFakeEntity(
                username = "Chris Athanas",
                accessToken = generateAccessToken_onServer(usersEmail[0]),
                accessTokenExpirationTimestampEpochMilli = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7,
                refreshToken = generateRefreshToken_onServer(usersEmail[0]),
                userId = generateUserId_onServer(usersEmail[0]) ,
                password = "Password1",
                email = usersEmail[0],
            )
        users_onServer["a@aa.com"] =
            AuthInfoFakeEntity(
                username = "Bilbo Baggins",
                accessToken = generateAccessToken_onServer(usersEmail[1]),
                accessTokenExpirationTimestampEpochMilli = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7,
                refreshToken = generateRefreshToken_onServer(usersEmail[1]),
                userId = generateUserId_onServer(usersEmail[1]),
                password = "Password1",
                email = usersEmail[1],
            )
    }

    private fun generateAccessToken_onServer(email: Email): AccessToken {
        // create JWT token
        var token = JWT.create()
            .withAudience(tokenConfig.audience)
            .withIssuer(tokenConfig.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + tokenConfig.expiresIn))
        val claims = listOf(
            Pair("userId", email),
        )
        claims.forEach { claim ->
            token = token.withClaim(claim.first, claim.second)
        }

        return token.sign(Algorithm.HMAC256(tokenConfig.secret))

//        return accessToken(token) as AccessToken
//        return accessToken("token for $email, expires:${System.currentTimeMillis() + 60L * 10L}") as AccessToken
    }

    private fun generateRefreshToken_onServer(email: Email): RefreshToken {
        return refreshToken("refresh token for $email, expires:${System.currentTimeMillis() + 60L * 10L}") as RefreshToken
    }

    private fun generateUserId_onServer(email: Email): UserId {
        return userId("id for $email") as UserId
    }

    fun expireAccessTokenByEmail_onServer(email: Email) {
        users_onServer[email]?.accessToken = null
    }

    private fun expireAccessToken_onServer(accessToken: AccessToken?) {
        users_onServer.filter { entry ->
            entry.value.accessToken == accessToken
        }.forEach { entry ->
            entry.value.accessToken = null
        }

        // todo should throw error if token not found?
    }

    ///// Fake API implementation ends here //////////
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
    authApiFake.setAccessToken(user?.accessToken) // set the AccessToken for the logged-in user

    println("user=$user")

    println("AuthApiFakeImpl.authenticate() Logged in user")
    println("  user.accessToken is valid=" +
                assert(authApiFake.authenticate()))
    println("  logged-in user accessToken is valid=" +
                assert(authApiFake.authenticate()))

    println()

    val accessTokenOnServer = user?.accessToken
    authApiFake.clearAuthInfo() // clear the AuthToken for the logged-in user
    println("AuthApiFakeImpl.authenticate() Logged out user has no accessToken")
    println("   IAuthApi.accessToken=null, token is NOT valid locally=" +
                assert(!authApiFake.authenticate()))
    println("   user.accessToken still valid on server=" +
                assert(authApiFake.authenticateAccessToken(accessTokenOnServer)))

    println()

    user = authApiFake.login("chris@demo.com", "Password1")
    authApiFake.expireAccessTokenByEmail_onServer("chris@demo.com") // server expires the token
    println("AuthApiFakeImpl.authenticate() Expired token (server side) when user is still logged in")
    println("   logged-in user accessToken is valid & not null=" +
                assert(user?.accessToken != null))
    println("   user.accessToken is INVALID on server=" +
                assert(!authApiFake.authenticate()))
    println("   logged-in user is NOT able to authenticate their token=" +
                assert(!authApiFake.authenticate()))

    println()

    println("AuthApiFakeImpl.authenticateAuthToken() Invalid token")
    println("   invalid accessToken is INVALID=" +
                assert(!authApiFake.authenticateAccessToken("invalid token")))

    println()

    println("AuthApiFakeImpl.authenticateAuthToken() Null token")
    println("   null accessToken is INVALID=" +
                assert(!authApiFake.authenticateAccessToken(null)))

    println()

    println("AuthApiFakeImpl.authenticateAuthToken() Blank token")
    println("   blank accessToken is INVALID=" +
                assert(!authApiFake.authenticateAccessToken(accessToken(""))))

    println()
}
