package com.realityexpander.tasky.presentation.login_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.R
import com.realityexpander.tasky.common.Exceptions
import com.realityexpander.tasky.common.UiText
import com.realityexpander.tasky.domain.IAuthRepository
import com.realityexpander.tasky.domain.validation.IValidateEmail
import com.realityexpander.tasky.domain.validation.ValidatePassword
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_email
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_errorMessage
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_isError
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_isInvalidEmail
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_isInvalidPassword
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_isLoggedIn
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_password
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_statusMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val validateEmail: IValidateEmail,
    private val validatePassword: ValidatePassword,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val email: String = savedStateHandle[SAVED_STATE_email] ?: ""
    private val password: String = savedStateHandle[SAVED_STATE_password] ?: ""
    private val isInvalidEmail: Boolean = savedStateHandle[SAVED_STATE_isInvalidEmail] ?: false
    private val isInvalidPassword: Boolean = savedStateHandle[SAVED_STATE_isInvalidPassword] ?: false
    private val isLoggedIn: Boolean = savedStateHandle[SAVED_STATE_isLoggedIn] ?: false
    private val isError: Boolean = savedStateHandle[SAVED_STATE_isError] ?: false
    private val statusMessage: UiText = savedStateHandle[SAVED_STATE_statusMessage] ?: UiText.None
    private val errorMessage: UiText = savedStateHandle[SAVED_STATE_errorMessage] ?: UiText.None

    init {
        // restore state after process death
        _loginState.value = LoginState(
                email = email,
                password = password,
                isInvalidEmail = isInvalidEmail,
                isInvalidPassword = isInvalidPassword,
                isLoggedIn = isLoggedIn,
                isError = isError,
                statusMessage = statusMessage,
                errorMessage = errorMessage
        )

//        sendEvent(LoginEvent.UpdateEmail(email))
//        sendEvent(LoginEvent.UpdatePassword(password))

        // Save state for process death
        viewModelScope.launch {
            yield()
            loginState.collect {
                savedStateHandle[SAVED_STATE_email] = it.email
                savedStateHandle[SAVED_STATE_password] = it.password
                savedStateHandle[SAVED_STATE_isInvalidEmail] = it.isInvalidEmail
                savedStateHandle[SAVED_STATE_isInvalidPassword] = it.isInvalidPassword
                savedStateHandle[SAVED_STATE_isLoggedIn] = it.isLoggedIn
                savedStateHandle[SAVED_STATE_isError] = it.isError
                savedStateHandle[SAVED_STATE_statusMessage] = it.statusMessage
                savedStateHandle[SAVED_STATE_errorMessage] = it.errorMessage

                if(email.isNotBlank()) sendEvent(LoginEvent.ValidateEmail(email))
                if(password.isNotBlank()) sendEvent(LoginEvent.ValidatePassword(password))
            }
        }

//        if(email.isNotBlank()) sendEvent(LoginEvent.ValidateEmail(email))
//        if(password.isNotBlank()) sendEvent(LoginEvent.ValidatePassword(password))
    }

    private suspend fun login(email: String, password: String) {
        try {
            val authToken = authRepository.login(email, password)
            sendEvent(LoginEvent.LoginSuccess(authToken))
        } catch(e: Exceptions.LoginException) {
            sendEvent(LoginEvent.LoginError(UiText.Res(R.string.error_login_error, e.message ?: "")))
        } catch(e: Exceptions.InvalidEmailException) {
            sendEvent(LoginEvent.IsValidEmail(false))
        } catch(e: Exceptions.InvalidPasswordException) {
            sendEvent(LoginEvent.IsValidPassword(false))
        } catch (e: Exception) {
            // handle general error
            sendEvent(LoginEvent.UnknownError(UiText.Res( R.string.error_unknown, e.message ?: "")))
            e.printStackTrace()
        }
    }

    private fun validateEmail(email: String) {
        val isValid = validateEmail.validate(email)
        sendEvent(LoginEvent.IsValidEmail(isValid))
    }

    private fun validatePassword(password: String) {
        val isValid = validatePassword.validate(password)
        sendEvent(LoginEvent.IsValidPassword(isValid))
    }

    fun sendEvent(event: LoginEvent) {
        viewModelScope.launch {
            onEvent(event)
            yield()
        }
    }

    private suspend fun onEvent(event: LoginEvent) {
        when(event) {
            is LoginEvent.Loading -> {
                _loginState.value = _loginState.value.copy(isLoading = event.isLoading)
            }
            is LoginEvent.UpdateEmail -> {
                _loginState.value = _loginState.value.copy(
                    email = event.email,
                    isInvalidEmail = false,
//                    isError = false,
                    errorMessage = UiText.None,
                )
                //savedStateHandle[SAVED_STATE_email] = event.email
            }
            is LoginEvent.UpdatePassword -> {
                _loginState.value = _loginState.value.copy(
                    password = event.password,
                    isInvalidPassword = false,
//                    isError = false
                    errorMessage = UiText.None,
                )
                //savedStateHandle[SAVED_STATE_password] = event.password
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
                sendEvent(LoginEvent.ValidateEmail(loginState.value.email))
                sendEvent(LoginEvent.ValidatePassword(loginState.value.password))
                yield()

                if(_loginState.value.isInvalidEmail || _loginState.value.isInvalidPassword) return

                sendEvent(LoginEvent.Loading(true))
                login(event.email, event.password)
            }
            is LoginEvent.LoginSuccess -> {
                _loginState.value = _loginState.value.copy(
                    isLoggedIn = true,
//                    isError = false,
                    errorMessage = UiText.None,
                    statusMessage = UiText.Res(R.string.login_success, event.authToken),
                    isPasswordVisible = false,
                )
                sendEvent(LoginEvent.Loading(false))
            }
            is LoginEvent.LoginError -> {
                _loginState.value = _loginState.value.copy(
                    isLoggedIn = false,
//                    isError = true,
                    errorMessage = event.message,
                    statusMessage = UiText.None
                )

                sendEvent(LoginEvent.Loading(false))
            }
            is LoginEvent.UnknownError -> {
                _loginState.value = _loginState.value.copy(
                    isLoggedIn = false,
//                    isError = true,
                    errorMessage = if(event.message.asResOrNull() == null)
                            UiText.Res(R.string.error_unknown, "")
                        else
                            event.message,
                )
            }
        }
    }
}
