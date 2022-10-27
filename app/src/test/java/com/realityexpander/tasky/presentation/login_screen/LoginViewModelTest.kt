package com.realityexpander.tasky.presentation.login_screen

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.realityexpander.tasky.MainCoroutineRule
import com.realityexpander.tasky.data.repository.AuthRepositoryFakeImpl
import com.realityexpander.tasky.data.repository.local.AuthDaoFakeImpl
import com.realityexpander.tasky.data.repository.remote.AuthApiFakeImpl
import com.realityexpander.tasky.domain.IAuthRepository
import com.realityexpander.tasky.domain.validation.EmailMatcherFakeImpl
import com.realityexpander.tasky.domain.validation.ValidateEmailImpl
import com.realityexpander.tasky.domain.validation.ValidatePassword
import com.realityexpander.tasky.domain.validation.ValidateUsername
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var authRepository: IAuthRepository
    private val authApiFake = AuthApiFakeImpl()
    private val authDaoFake = AuthDaoFakeImpl()
    private val validateEmail = ValidateEmailImpl(EmailMatcherFakeImpl())
    private val validatePassword = ValidatePassword()
    private val validateUsername = ValidateUsername()

    //@MockK
    //lateinit var uri: Uri

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(Uri::class)

        every { Uri.decode(any()) } answers { firstArg() }

        authRepository = AuthRepositoryFakeImpl(
            authApi = authApiFake,
            authDao = authDaoFake,
            validateUsername = validateUsername,
            validateEmail = validateEmail,
            validatePassword = validatePassword,
        )

        loginViewModel = LoginViewModel(
            authRepository = authRepository,
            validateEmail = validateEmail,
            validatePassword = validatePassword,
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
            validateEmail = validateEmail,
            validatePassword = validatePassword,
            savedStateHandle = SavedStateHandle().apply {
                set("email", expectedEmail)
                set("password", expectedPassword)
            }
        )

        // ACT
        coroutineRule.dispatcher.scheduler.advanceUntilIdle()

        runTest {

            loginViewModel.loginState.test {

                // attempt to set state for password visibility.
                loginViewModel.sendEvent(LoginEvent.SetPasswordVisibility(true))
                // Should be able to do this, but it doesn't work
                // https://developer.android.com/kotlin/flow/test
                // https://medium.com/google-developer-experts/unit-testing-kotlin-flow-76ea5f4282c5

                // ASSERT

                var state = awaitItem() // initial state
                println("state: ${state.email}")

                state = awaitItem()  // first emission from init block
                println("state: ${state.email}")
                assertThat(state.email).isEqualTo(expectedEmail)
                assertThat(state.password).isEqualTo(expectedPassword)

                // Await the "SetPasswordVisibility" event
                //assert(awaitItem().isPasswordVisible) // why no worky?


                ensureAllEventsConsumed()
                cancelAndIgnoreRemainingEvents()
                assertThat(loginViewModel.loginState.value.email).isEqualTo(expectedEmail)
                assertThat(loginViewModel.loginState.value.password).isEqualTo(expectedPassword)
            }

        }
    }




}