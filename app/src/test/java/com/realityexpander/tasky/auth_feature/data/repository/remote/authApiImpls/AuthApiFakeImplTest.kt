package com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls

import com.realityexpander.tasky.auth_feature.data.repository.remote.IAuthApi
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.util.Exceptions.EMAIL_ALREADY_EXISTS
import junit.framework.TestCase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthApiFakeImplTest {

    private lateinit var authApiFakeImpl: IAuthApi

    @Before
    fun setUp() {
        authApiFakeImpl = AuthApiFakeImpl()
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
        val email = "greg@demo.com"
        val password = "Password1"
        val expectedUsername = "Greg Athanas"

        // ACT
        runTest {
            authApiFakeImpl.register(expectedUsername, email, password)
            val authInfoDTO = authApiFakeImpl.login(email, password)

            // ASSERT
            assertEquals(expectedUsername, authInfoDTO?.username)
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
    fun `authenticate() is successful for a logged-in user`() {
        // ARRANGE
        val email = "chris@demo.com"
        val password = "Password1"
        val expectedUsername = "Chris Athanas"

        // ACT
        runTest {
            val authInfoDTO = authApiFakeImpl.login(email, password)
            val authenticated = authApiFakeImpl.authenticate(authInfoDTO?.authToken)

            // ASSERT
            assertTrue(authenticated)
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
            val authenticated = authApiFakeImpl.authenticate(authInfoDTO?.authToken)

            // ASSERT
            assertFalse(authenticated)
        }
    }
}