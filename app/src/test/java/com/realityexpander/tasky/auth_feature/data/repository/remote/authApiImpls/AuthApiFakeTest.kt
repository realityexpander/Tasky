package com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls

import com.auth0.jwt.JWT
import com.realityexpander.tasky.auth_feature.data.repository.remote.IAuthApi
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.util.Exceptions.EMAIL_ALREADY_EXISTS
import junit.framework.TestCase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

// Uses fake network API, Authentication & Authorization using JWT tokens
// Exercises the IAuthApi interface
// Simulates a Tasky server with an in RAM database of users
@OptIn(ExperimentalCoroutinesApi::class)
class AuthApiFakeTest {

    private lateinit var authApiFakeImpl: IAuthApi

    @Before
    fun setUp() {
        authApiFakeImpl = AuthApiFake()
    }

    @Test
    fun `login() is successful for a registered user`() {
        // ARRANGE
        val email = "chris@demo.com"
        val password = "Password1"
        val expectedUsername = "Chris Athanas"

        // ACT
        runTest {
            val authInfoDTO = authApiFakeImpl.login(email, password)

            // ASSERT
            assertEquals(expectedUsername, authInfoDTO?.username)
            // email matches the email in the JWT token
            assertEquals(true,
                JWT.decode(authInfoDTO?.accessToken!!)
                    .claims["userId"]
                    .toString()
                    .contains("chris@demo.com")
            )

        }
    }

    @Test
    fun `login() is unsuccessful for an unknown user`() {
        // ARRANGE
        val email = "UNKNOWN_EMAIL@demo.com"
        val password = "Password1"
        var expectedExceptionOccurred = false

        // ACT
        runTest {
            try {
                val authInfoDTO = authApiFakeImpl.login(email, password)
            } catch (e: Exception) {
                // ASSERT
                expectedExceptionOccurred = true
            }

            // ASSERT
            assertTrue(expectedExceptionOccurred)
        }
    }

    @Test
    fun `register() is successful for a new user`() {
        // ARRANGE
        val expectedEmail = "adam@demo.com"
        val password = "Password1"
        val expectedUsername = "Adam Athanas"

        // ACT
        runTest {
            authApiFakeImpl.register(expectedUsername, expectedEmail, password)
            val authInfoDTO = authApiFakeImpl.login(expectedEmail, password)

            // ASSERT
            assertEquals(expectedUsername, authInfoDTO?.username)
            // email matches the email in the JWT token
            assertEquals(true,
                JWT.decode(authInfoDTO?.accessToken!!)
                    .claims["userId"]
                    .toString()
                    .contains(expectedEmail)
            )
        }
    }

    @Test
    fun `register() is unsuccessful for a user that already exists`() {
        // ARRANGE
        val email = "chris@demo.com"
        val password = "Password1"
        val expectedUsername = "Chris Athanas"

        // ACT
        runTest {
            val authInfoDTO = try {
                authApiFakeImpl.register(expectedUsername, email, password)
                authApiFakeImpl.login(email, password)
            } catch (e: Exception) {
                // ASSERT
                assertTrue(e.message?.contains(EMAIL_ALREADY_EXISTS) ?: false)
                null
            }

            // ASSERT
            assertEquals(authInfoDTO, null)
        }
    }

    @Test
    fun `refreshToken() is successful for a logged-in user`() {
        // ARRANGE
        val email = "chris@demo.com"
        val password = "Password1"
        val expectedUsername = "Chris Athanas"

        runTest {
            val authInfoDTO = authApiFakeImpl.login(email, password)!!
            val originalAccessToken = authInfoDTO.accessToken

            assertTrue(IAuthApi.accessToken == originalAccessToken)

            // ACT
            authApiFakeImpl.refreshAccessToken(
                    authInfoDTO.userId!!,
                    authInfoDTO.refreshToken!!
                )


            // ASSERT - Refreshed token is different from the original token
            assertTrue(IAuthApi.refreshToken != originalAccessToken)
        }
    }

    @Test
    fun `refreshToken() is unsuccessful for an invalid Refresh Token`() {
        // ARRANGE
        val email = "chris@demo.com"
        val password = "Password1"
        val expectedUsername = "Chris Athanas"

        runTest {
            val authInfoDTO = authApiFakeImpl.login(email, password)!!
            val originalAccessToken = authInfoDTO.accessToken

            // Simulate an invalid refresh token
            IAuthApi.refreshToken = "invalid_refresh_token"

            // ACT
            try {
                authApiFakeImpl.refreshAccessToken(
                    authInfoDTO.userId!!,
                    IAuthApi.refreshToken!!
                )
            } catch (e: Exception) {
                // ASSERT
                assertTrue(e.message?.contains("401 Unauthorized") ?: false)
            }

            // ASSERT - Refreshed token is different from the original token
            assertTrue(IAuthApi.refreshToken != originalAccessToken)
        }
    }

    @Test
    fun `authenticate() is successful for a logged-in user`() {
        // ARRANGE
        val email = "chris@demo.com"
        val password = "Password1"
        val expectedUsername = "Chris Athanas"

        // ACT
        runTest {
            val authInfoDTO = authApiFakeImpl.login(email, password)
            val authenticated = authApiFakeImpl.authenticate()

            // ASSERT
            assertTrue(authenticated)
            assertEquals(expectedUsername, authInfoDTO?.username)
        }
    }

    @Test
    fun `authenticate() is unsuccessful for a logged-out user`() {
        // ARRANGE
        val email = "chris@demo.com"
        val password = "Password1"
        val expectedUsername = "Chris Athanas"

        // ACT
        runTest {
            val authInfoDTO: AuthInfo? = null
            val authenticated = authApiFakeImpl.authenticate()

            // ASSERT
            assertFalse(authenticated)
            assertEquals(null, authInfoDTO)
        }
    }

}
