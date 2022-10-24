package com.realityexpander.tasky.presentation.login_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.common.Exceptions
import com.realityexpander.tasky.domain.IAuthRepository
import com.realityexpander.tasky.domain.validation.IValidateEmail
import com.realityexpander.tasky.domain.validation.IValidatePassword
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_EMAIL
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_PASSWORD
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val validateEmail: IValidateEmail,
    private val validatePassword: IValidatePassword,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val email: String = savedStateHandle[SAVED_STATE_EMAIL] ?: ""
    private val password: String = savedStateHandle[SAVED_STATE_PASSWORD] ?: ""

    init {
        sendEvent(LoginEvent.UpdateEmail(email))
        sendEvent(LoginEvent.UpdatePassword(password))
    }

    private suspend fun login(email: String, password: String) {
        try {
            val authToken = authRepository.login(email, password)
            sendEvent(LoginEvent.LoginSuccess(authToken))
        } catch(e: Exceptions.LoginException) {
            sendEvent(LoginEvent.LoginError(e.message ?: "Unknown Login Error"))
        } catch(e: Exceptions.InvalidEmailException) {
            sendEvent(LoginEvent.IsValidEmail(false))
        } catch(e: Exceptions.InvalidPasswordException) {
            sendEvent(LoginEvent.IsValidPassword(false))
        } catch (e: Exception) {
            // handle general error
            sendEvent(LoginEvent.UnknownError(e.message ?: "Unknown error"))
            e.printStackTrace()
        }
    }

    private fun validateEmail(email: String) {
        val isValid = validateEmail.validateEmail(email)
        sendEvent(LoginEvent.IsValidEmail(isValid))
    }

    private fun validatePassword(password: String) {
        val isValid = validatePassword.validatePassword(password)
        sendEvent(LoginEvent.IsValidPassword(isValid))
    }

    fun sendEvent(event: LoginEvent) {
        viewModelScope.launch {
            onEvent(event)
        }
    }

    suspend fun onEvent(event: LoginEvent) {
        when(event) {
            is LoginEvent.Loading -> {
                _loginState.value = _loginState.value.copy(isLoading = event.isLoading)
            }
            is LoginEvent.UpdateEmail -> {
                _loginState.value = _loginState.value.copy(
                    email = event.email,
                    isInvalidEmail = false,
                    isError = false,
                )
                savedStateHandle["email"] = event.email
            }
            is LoginEvent.UpdatePassword -> {
                _loginState.value = _loginState.value.copy(
                    password = event.password,
                    isInvalidPassword = false,
                    isError = false
                )
                savedStateHandle["password"] = event.password
            }
            is LoginEvent.TogglePasswordVisibility -> {
                _loginState.value = _loginState.value.copy(
                    isPasswordVisible = !event.isPasswordVisible
                )
            }
            is LoginEvent.ValidateEmail -> {
                validateEmail(event.email)
            }
            is LoginEvent.ValidatePassword -> {
                validatePassword(event.password)
            }
            is LoginEvent.IsValidEmail -> {
                _loginState.value = _loginState.value.copy(isInvalidEmail = !event.isValid)
            }
            is LoginEvent.IsValidPassword -> {
                _loginState.value = _loginState.value.copy(isInvalidPassword = !event.isValid)
            }
            is LoginEvent.Login -> {
                if(_loginState.value.isInvalidEmail || _loginState.value.isInvalidPassword) return

                sendEvent(LoginEvent.Loading(true))
                login(event.email, event.password)
            }
            is LoginEvent.LoginSuccess -> {
                _loginState.value = _loginState.value.copy(
                    isLoggedIn = true,
                    isError = false,
                    errorMessage = "",
                    statusMessage = "Login Success: authToken = ${event.authToken}",
                    isPasswordVisible = false,
                )
                sendEvent(LoginEvent.Loading(false))
            }
            is LoginEvent.LoginError -> {
                _loginState.value = _loginState.value.copy(
                    isLoggedIn = false,
                    isError = true,
                    errorMessage = event.message,
                    statusMessage = ""
                )

                sendEvent(LoginEvent.Loading(false))
            }
            is LoginEvent.UnknownError -> {
                _loginState.value = _loginState.value.copy(
                    isLoggedIn = false,
                    isError = true,
                    errorMessage = event.message,
                )
            }
        }
    }

}
