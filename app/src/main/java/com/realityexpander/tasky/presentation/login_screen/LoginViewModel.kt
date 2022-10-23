package com.realityexpander.tasky.presentation.login_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.common.Exceptions
import com.realityexpander.tasky.domain.IAuthRepository
import com.realityexpander.tasky.domain.validation.IValidateEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
//    = AuthRepositoryImpl(
//        authApi = AuthApiImpl(),
//        authDao = AuthDaoImpl(),
//        validateEmail = ValidateEmailImpl(emailMatcher = EmailMatcherImpl())
//    ),
    private val validateEmail: IValidateEmail,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Kotlin Coroutines StateFlow - updates NOT sent when app is in background (HOT)
    private var _loginStateFlow = MutableStateFlow<State>(State())
    var loginStateFlow: StateFlow<State> = _loginStateFlow.asStateFlow()

    // Channel - updates ARE sent when app is in background
    val loginChannel = Channel<State>()


    private suspend fun login(email: String, password: String) {
        try {
            val authToken = authRepository.login(email, password)
            sendEvent(LoginEvent.LoginSuccess(authToken))
        } catch(e: Exceptions.LoginException) {
            sendEvent(LoginEvent.LoginError(e.message ?: "Unknown Login Error"))
        } catch(e: Exceptions.InvalidEmailException) {
            sendEvent(LoginEvent.IsInvalidEmail)
        } catch (e: Exception) {
            // handle general error
            sendEvent(LoginEvent.UnknownError(e.message ?: "Unknown error"))
            e.printStackTrace()
        }
    }

    private fun validateEmail(email: String) {
        if (validateEmail.validateEmail(email)) {
            sendEvent(LoginEvent.IsValidEmail)
        } else {
            sendEvent(LoginEvent.IsInvalidEmail)
        }
    }

    private fun validatePassword(password: String) {
        if (password.length >= 6 ) { // && validatePassword.validatePassword(password)) { //todo
            sendEvent(LoginEvent.IsValidPassword)
        } else {
            sendEvent(LoginEvent.IsInvalidPassword)
        }
    }

    private fun sendEvent(event: LoginEvent) {
        viewModelScope.launch {
            onEvent(event)
        }
    }

    suspend fun onEvent(event: LoginEvent) {
        when(event) {
            is LoginEvent.UpdateEmail -> {
                _loginStateFlow.value = _loginStateFlow.value
                    .copy(email = event.email, isInvalidEmail = false)
            }
            is LoginEvent.UpdatePassword -> {
                _loginStateFlow.value = _loginStateFlow.value
                    .copy(password = event.password, isInvalidPassword = false)
            }
            is LoginEvent.Login -> {
                if(_loginStateFlow.value.isInvalidEmail || _loginStateFlow.value.isInvalidPassword) {
                    return
                }

                login(event.email, event.password)
            }
            is LoginEvent.ValidateEmail -> {
                validateEmail(event.email)
            }
            is LoginEvent.ValidatePassword -> {
                validatePassword(event.password)
            }
            is LoginEvent.LoginSuccess -> {
                _loginStateFlow.value = _loginStateFlow.value
                    .copy(
                        isLoggedIn = true,
                        statusMessage = "Login Success: authToken = ${event.authToken}"
                    )
            }
            is LoginEvent.LoginError -> {
                _loginStateFlow.value = _loginStateFlow.value
                    .copy(isError = true)
            }
            is LoginEvent.IsInvalidEmail -> {
                _loginStateFlow.value = _loginStateFlow.value
                    .copy(isInvalidEmail = true)
            }
            is LoginEvent.IsInvalidPassword -> {
                _loginStateFlow.value = _loginStateFlow.value
                    .copy(isInvalidPassword = true)
            }
            is LoginEvent.IsValidEmail -> {
                _loginStateFlow.value = _loginStateFlow.value
                    .copy(isInvalidEmail = false)
            }
            is LoginEvent.IsValidPassword -> {
                _loginStateFlow.value = _loginStateFlow.value
                    .copy(isInvalidPassword = false)
            }
            is LoginEvent.UnknownError -> {
                _loginStateFlow.value = _loginStateFlow.value
                    .copy(isError = true, errorMessage = event.message)
            }
        }
    }

}
