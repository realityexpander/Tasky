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
            authRepository.login(email, password)
        } catch(e: Exceptions.LoginException) {
            sendEvent(LoginEvent.LoginError)
        } catch(e: Exceptions.InvalidEmailException) {
            sendEvent(LoginEvent.InvalidEmail)
        } catch (e: Exception) {
            // handle general error
            sendEvent(LoginEvent.UnknownError(e.message ?: "Unknown error"))
            e.printStackTrace()
        }
    }

    private fun validateEmail(email: String) {
        try {
            validateEmail.validateEmail(email)
        } catch(e: Exceptions.InvalidEmailException) {
            sendEvent(LoginEvent.InvalidEmail)
        } catch (e: Exception) {
            // handle general error
            sendEvent(LoginEvent.UnknownError(e.message ?: "Unknown Error"))
            e.printStackTrace()
        }
    }

    private fun validatePassword(password: String) {
        try {
            //validatePassword.validatePassword(password) // todo
        } catch(e: Exceptions.InvalidPasswordException) {
            sendEvent(LoginEvent.InvalidPassword)
        } catch (e: Exception) {
            sendEvent(LoginEvent.UnknownError(e.message ?: "Unknown Error"))
            e.printStackTrace()
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
                _loginStateFlow.value = _loginStateFlow.value.copy(email = event.email)
            }
            is LoginEvent.UpdatePassword -> {
                _loginStateFlow.value = _loginStateFlow.value.copy(password = event.password)
            }
            is LoginEvent.Login -> {
                login(event.email, event.password)
            }
            is LoginEvent.ValidateEmail -> {
                validateEmail(event.email)
            }
            is LoginEvent.ValidatePassword -> {
                validatePassword(event.password)
            }
            is LoginEvent.LoginSuccess -> {
                _loginStateFlow.value = _loginStateFlow.value.copy(isLoggedIn = true)
            }
            is LoginEvent.LoginError -> {
                _loginStateFlow.value = _loginStateFlow.value.copy(isError = true)
            }
            is LoginEvent.InvalidEmail -> {
                _loginStateFlow.value = _loginStateFlow.value.copy(isInvalidEmail = true)
            }
            is LoginEvent.InvalidPassword -> {
                _loginStateFlow.value = _loginStateFlow.value.copy(isInvalidPassword = true)
            }
            is LoginEvent.UnknownError -> {
                _loginStateFlow.value = _loginStateFlow.value.copy(isError = true, errorMessage = event.message)
            }
        }
    }

}
