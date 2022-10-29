package com.realityexpander.tasky

import androidx.compose.material.Surface
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.SavedStateHandle
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.realityexpander.tasky.data.repository.authRepositoryImpls.AuthRepositoryFakeImpl
import com.realityexpander.tasky.data.repository.local.AuthDaoFakeImpl
import com.realityexpander.tasky.data.repository.remote.authApiImpls.AuthApiFakeImpl
import com.realityexpander.tasky.domain.IAuthRepository
import com.realityexpander.tasky.domain.validation.validateEmail.ValidateEmailImpl
import com.realityexpander.tasky.domain.validation.ValidatePassword
import com.realityexpander.tasky.domain.validation.ValidateUsername
import com.realityexpander.tasky.presentation.login_screen.LoginScreen
import com.realityexpander.tasky.presentation.login_screen.LoginViewModel
import com.realityexpander.tasky.presentation.ui.theme.TaskyTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AppTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var authRepository: IAuthRepository
    private val authApiFake = AuthApiFakeImpl()
    private val authDaoFake = AuthDaoFakeImpl()
    private val validateEmail = ValidateEmailImpl()
    private val validatePassword = ValidatePassword()
    private val validateUsername = ValidateUsername()

    @Before
    fun setUp() {
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
    fun app_launches() {
        composeTestRule.setContent {
            TaskyTheme {
                Surface {
                    LoginScreen(
                        navigator = EmptyDestinationsNavigator,
                        viewModel = loginViewModel
                    )
                }
            }
        }

        // Check app launches at the correct destination
        composeTestRule.onNodeWithText("LOG IN").assertIsDisplayed()
    }

    @Test
    fun app_login_shows_email_and_password_hidden() {
        // ARRANGE
        loginViewModel = LoginViewModel(
            authRepository = authRepository,
            validateEmail = validateEmail,
            validatePassword = validatePassword,
            savedStateHandle = SavedStateHandle().apply {
                set("email", "chris@demo.com")
                set("password", "1234567Aa")
            }
        )

        // ACT
        composeTestRule.setContent {
            TaskyTheme {
                Surface {
                    LoginScreen(
                        navigator = EmptyDestinationsNavigator,
                        viewModel = loginViewModel
                    )
                }
            }
        }

        // ASSERT
        composeTestRule.onNodeWithText("chris@demo.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("•••••••••").assertIsDisplayed()
    }

    @Test
    fun app_login_shows_email_and_password_unhidden_when_show_password_is_clicked() {

        // ARRANGE
        loginViewModel = LoginViewModel(
            authRepository = authRepository,
            validateEmail = validateEmail,
            validatePassword = validatePassword,
            savedStateHandle = SavedStateHandle().apply {
                set("email", "chris@demo.com")
                set("password", "1234567Aa")
            }
        )

        // ACT
        composeTestRule.setContent {
            TaskyTheme {
                Surface {
                    LoginScreen(
                        navigator = EmptyDestinationsNavigator,
                        viewModel = loginViewModel
                    )
                }
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Show password").assertExists()
        composeTestRule.onNodeWithContentDescription("Show password").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Show password").performClick()
        composeTestRule.waitForIdle()

        // ASSERT
        composeTestRule.onNodeWithText("chris@demo.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("1234567Aa").assertIsDisplayed()
    }

}