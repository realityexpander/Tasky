package com.realityexpander.tasky.presentation.login_screen

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.realityexpander.tasky.CoroutineTestRule
import com.realityexpander.tasky.MainCoroutineRule
import com.realityexpander.tasky.auth_feature.data.repository.authRepositoryImpls.AuthRepositoryFakeImpl
import com.realityexpander.tasky.auth_feature.data.repository.local.authDaoImpls.AuthDaoFakeImpl
import com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls.AuthApiFakeImpl
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.auth_feature.domain.validation.ValidateEmail
import com.realityexpander.tasky.auth_feature.domain.validation.ValidatePassword
import com.realityexpander.tasky.auth_feature.domain.validation.ValidateUsername
import com.realityexpander.tasky.auth_feature.presentation.login_screen.LoginEvent
import com.realityexpander.tasky.auth_feature.presentation.login_screen.LoginViewModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var authRepository: IAuthRepository
    private val authApiFake = AuthApiFakeImpl()
    private val authDaoFake = AuthDaoFakeImpl()
    private val validateEmail = ValidateEmail()
    private val validatePassword = ValidatePassword()
    private val validateUsername = ValidateUsername()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(Uri::class)

        every { Uri.decode(any()) } answers { firstArg() }  // mock Uri.decode() to return the same string

        authRepository = AuthRepositoryFakeImpl(
            authApi = authApiFake,
            authDao = authDaoFake,
            validateUsername = validateUsername,
            validateEmail = validateEmail,
            validatePassword = validatePassword,
        )

        loginViewModel = LoginViewModel(
            authRepository = authRepository,
            savedStateHandle = SavedStateHandle()
        )
    }

    @Test
    fun `LoginViewModel contains email and password`() {
        // ARRANGE
        val expectedEmail = "chris@demo.com"
        val expectedPassword = "Ab3456789"
        loginViewModel = LoginViewModel(
            authRepository = authRepository,
            savedStateHandle = SavedStateHandle().apply {
                set("email", expectedEmail)
                set("password", expectedPassword)
            }
        )

        // ACT
        coroutineRule.dispatcher.scheduler.advanceUntilIdle()

        runTest {

            loginViewModel.loginState.test {
                // ASSERT
                awaitItem() // ignore initial state

                val state = awaitItem()  // first emission from init block
                assertThat(state.email).isEqualTo(expectedEmail)
                assertThat(state.password).isEqualTo(expectedPassword)

                ensureAllEventsConsumed()
                cancelAndIgnoreRemainingEvents()
                assertThat(loginViewModel.loginState.value.email).isEqualTo(expectedEmail)
                assertThat(loginViewModel.loginState.value.password).isEqualTo(expectedPassword)
            }

        }
    }

    @Test
    fun `LoginViewModel can set password visibility`()  {
        // ARRANGE
        loginViewModel = LoginViewModel(
            authRepository = authRepository,
            savedStateHandle = SavedStateHandle()
        )

        // ACT
        coroutineRule.dispatcher.scheduler.advanceUntilIdle()

        runTest {
            loginViewModel.loginState.test {

                // ASSERT
                var state = awaitItem() // initial state
                assertThat(state.isPasswordVisible).isFalse()

                // ACT
                loginViewModel.sendEvent(LoginEvent.SetIsPasswordVisible(true))
                //yield()

                // ASSERT
                state = awaitItem()
                assertThat(state.isPasswordVisible).isTrue()

                ensureAllEventsConsumed()
                cancelAndIgnoreRemainingEvents()
                assertThat(loginViewModel.loginState.value.isPasswordVisible).isEqualTo(true)
            }
        }
    }


}