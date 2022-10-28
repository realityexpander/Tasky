package com.realityexpander.tasky.presentation.login_screen

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.R
import com.realityexpander.tasky.common.Exceptions
import com.realityexpander.tasky.ui.util.UiText
import com.realityexpander.tasky.domain.IAuthRepository
import com.realityexpander.tasky.domain.validation.validateEmail.IValidateEmail
import com.realityexpander.tasky.domain.validation.ValidatePassword
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_email
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_errorMessage
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_isInvalidEmail
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_isInvalidPassword
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_isLoggedIn
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_isShowInvalidEmailMessage
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_isShowInvalidPasswordMessage
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_password
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_statusMessage
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_username
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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

    private val username: String =
        Uri.decode(savedStateHandle[SAVED_STATE_username]) ?: ""
    private val email: String =
        Uri.decode(savedStateHandle[SAVED_STATE_email]) ?: ""
    private val password: String =
        Uri.decode(savedStateHandle[SAVED_STATE_password]) ?: ""
    private val isInvalidEmail: Boolean =
        savedStateHandle[SAVED_STATE_isInvalidEmail] ?: false
    private val isShowInvalidEmailMessage: Boolean =
        savedStateHandle[SAVED_STATE_isShowInvalidEmailMessage] ?: false
    private val isInvalidPassword: Boolean =
        savedStateHandle[SAVED_STATE_isInvalidPassword] ?: false
    private val isShowInvalidPasswordMessage: Boolean =
        savedStateHandle[SAVED_STATE_isShowInvalidPasswordMessage] ?: false
    private val isLoggedIn: Boolean =
        savedStateHandle[SAVED_STATE_isLoggedIn] ?: false
    private val statusMessage: UiText =
        savedStateHandle[SAVED_STATE_statusMessage] ?: UiText.None
    private val errorMessage: UiText =
        savedStateHandle[SAVED_STATE_errorMessage] ?: UiText.None

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.onEach { state ->
        // save state for process death
        savedStateHandle[SAVED_STATE_username] = state.username
        savedStateHandle[SAVED_STATE_email] = state.email
        savedStateHandle[SAVED_STATE_password] = state.password
        savedStateHandle[SAVED_STATE_isInvalidEmail] = state.isInvalidEmail
        savedStateHandle[SAVED_STATE_isShowInvalidEmailMessage] = state.isShowInvalidEmailMessage
        savedStateHandle[SAVED_STATE_isInvalidPassword] = state.isInvalidPassword
        savedStateHandle[SAVED_STATE_isShowInvalidPasswordMessage] = state.isShowInvalidPasswordMessage
        savedStateHandle[SAVED_STATE_isLoggedIn] = state.isLoggedIn
        savedStateHandle[SAVED_STATE_statusMessage] = state.statusMessage
        savedStateHandle[SAVED_STATE_errorMessage] = state.errorMessage

        // Validate email as the user types
        if(state.email.isNotBlank()) sendEvent(LoginEvent.ValidateEmail)
        if(state.password.isNotBlank()) sendEvent(LoginEvent.ValidatePassword)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LoginState())

    init {
        viewModelScope.launch {
            yield() // allow the loginState to be initialized

            // restore state after process death
            _loginState.value = LoginState(
                username = username,
                email = email,
                password = password,
                isInvalidEmail = isInvalidEmail,
                isShowInvalidEmailMessage = isShowInvalidEmailMessage,
                isInvalidPassword = isInvalidPassword,
                isShowInvalidPasswordMessage = isShowInvalidPasswordMessage,
                isLoggedIn = isLoggedIn,
                statusMessage = statusMessage,
                errorMessage = errorMessage
            )
            yield() // allow loginState to be updated

            // Validate email & password when restored from process death or coming from another screen
            if (loginState.value.email.isNotBlank()) sendEvent(LoginEvent.ValidateEmail)
            if (loginState.value.password.isNotBlank()) sendEvent(LoginEvent.ValidatePassword)

            yield() // allow the loginState to be updated
            // Show status validation messages when restored from process death or coming from another screen
            if(loginState.value.isInvalidEmail) sendEvent(LoginEvent.ShowInvalidEmailMessage)
            if(loginState.value.isInvalidPassword) sendEvent(LoginEvent.ShowInvalidPasswordMessage)
        }
    }

    private suspend fun login(email: String, password: String) {
        try {
            val authToken = authRepository.login(email, password)
            sendEvent(LoginEvent.LoginSuccess(authToken))
        } catch(e: Exceptions.WrongPasswordException) {
            sendEvent(LoginEvent.LoginError(UiText.Res(R.string.error_login_error, e.message ?: "")))
        } catch(e: Exceptions.LoginException) {
            sendEvent(LoginEvent.LoginError(UiText.Res(R.string.error_login_error, e.message ?: "")))
        } catch(e: Exceptions.InvalidEmailException) {
            sendEvent(LoginEvent.SetIsValidEmail(false))
        } catch(e: Exceptions.InvalidPasswordException) {
            sendEvent(LoginEvent.SetIsValidPassword(false))
        } catch (e: Exception) {
            sendEvent(LoginEvent.UnknownError(UiText.Res(R.string.error_unknown, e.message ?: "")))
            e.printStackTrace()
        }
    }

    private fun validateEmail() {
        val isValid = validateEmail.validate(loginState.value.email)
        sendEvent(LoginEvent.SetIsValidEmail(isValid))
    }

    private fun validatePassword() {
        val isValid = validatePassword.validate(loginState.value.password)
        sendEvent(LoginEvent.SetIsValidPassword(isValid))
    }

    fun sendEvent(event: LoginEvent) {
        viewModelScope.launch {
            onEvent(event)
            yield() // allow events to percolate
        }
    }

    private suspend fun onEvent(event: LoginEvent) {
        when(event) {
            is LoginEvent.Loading -> {
//                _loginState.value = _loginState.value.copy(isLoading = event.isLoading)
                _loginState.update {
                    it.copy(isLoading = event.isLoading)
                }
            }
            is LoginEvent.UpdateEmail -> {
//                _loginState.value = _loginState.value.copy(
//                    email = event.email,
//                    isInvalidEmail = false,
//                    isShowInvalidEmailMessage = false,
//                    errorMessage = UiText.None,
//                )
                _loginState.update {
                    it.copy(
                        email = event.email,
                        isInvalidEmail = false,
                        isShowInvalidEmailMessage = false,
                        errorMessage = UiText.None,
                    )
                }
            }
            is LoginEvent.UpdatePassword -> {
//                _loginState.value = _loginState.value.copy(
//                    password = event.password,
//                    isInvalidPassword = false,
//                    isShowInvalidPasswordMessage = false,
//                    errorMessage = UiText.None,
//                )
                _loginState.update {
                    it.copy(
                        password = event.password,
                        isInvalidPassword = false,
                        isShowInvalidPasswordMessage = false,
                        errorMessage = UiText.None,
                    )
                }
            }
            is LoginEvent.SetIsPasswordVisibile -> {
//                _loginState.value = _loginState.value.copy(
//                    isPasswordVisible = event.isPasswordVisible
//                )
                _loginState.update {
                    it.copy(
                        isPasswordVisible = event.isPasswordVisible
                    )
                }
            }
            is LoginEvent.ValidateEmail -> {
                validateEmail()
                yield()
            }
            is LoginEvent.ValidatePassword -> {
                validatePassword()
                yield()
            }
            is LoginEvent.SetIsValidEmail -> {
//                _loginState.value = _loginState.value.copy(
//                    isInvalidEmail = !event.isValid,
//                )
                _loginState.update {
                    it.copy(
                        isInvalidEmail = !event.isValid,
                    )
                }
            }
            is LoginEvent.ShowInvalidEmailMessage -> {
                _loginState.value = _loginState.value.copy(
                    isShowInvalidEmailMessage = true
                )
            }
            is LoginEvent.SetIsValidPassword -> {
//                _loginState.value = _loginState.value.copy(
//                    isInvalidPassword = !event.isValid
//                )
                _loginState.update {
                    it.copy(
                        isInvalidPassword = !event.isValid,
                    )
                }
            }
            is LoginEvent.ShowInvalidPasswordMessage -> {
//                _loginState.value = _loginState.value.copy(
//                    isShowInvalidPasswordMessage = true
//                )
                _loginState.update {
                    it.copy(
                        isShowInvalidPasswordMessage = true
                    )
                }
            }
            is LoginEvent.Login -> {
                sendEvent(LoginEvent.ValidateEmail)
                sendEvent(LoginEvent.ValidatePassword)
                yield()

                // Only show `Invalid Email` message only when "login" is clicked and the email is invalid.
                if(_loginState.value.isInvalidEmail)
                    sendEvent(LoginEvent.ShowInvalidEmailMessage)

                // Only show `Invalid Password` message only when "login" is clicked and the password is invalid.
                if(_loginState.value.isInvalidPassword)
                    sendEvent(LoginEvent.ShowInvalidPasswordMessage)

                if(_loginState.value.isInvalidEmail || _loginState.value.isInvalidPassword)
                    return

                sendEvent(LoginEvent.Loading(true))
                login(event.email, event.password)
            }
            is LoginEvent.LoginSuccess -> {
//                _loginState.value = _loginState.value.copy(
//                    isLoggedIn = true,
//                    errorMessage = UiText.None,
//                    statusMessage = UiText.Res(R.string.login_success, event.authToken),
//                    isPasswordVisible = false,
//                )
                _loginState.update {
                    it.copy(
                        isLoggedIn = true,
                        errorMessage = UiText.None,
                        statusMessage = UiText.Res(R.string.login_success, event.authToken),
                        isPasswordVisible = false,
                    )
                }
                sendEvent(LoginEvent.Loading(false))
            }
            is LoginEvent.LoginError -> {
//                _loginState.value = _loginState.value.copy(
//                    isLoggedIn = false,
//                    errorMessage = event.message,
//                    statusMessage = UiText.None,
//                    isLoading = false
//                )
                _loginState.update {
                    it.copy(
                        isLoggedIn = false,
                        errorMessage = event.message,
                        statusMessage = UiText.None,
                        isLoading = false
                    )
                }
                sendEvent(LoginEvent.Loading(false))
            }
            is LoginEvent.UnknownError -> {
//                _loginState.value = _loginState.value.copy(
//                    isLoggedIn = false,
//                    errorMessage = if(event.message.isRes)
//                            event.message
//                        else
//                            UiText.Res(R.string.error_unknown, ""),
//                )
                _loginState.update {
                    it.copy(
                        isLoggedIn = false,
                        errorMessage = if(event.message.isRes)
                            event.message
                        else
                            UiText.Res(R.string.error_unknown, ""),
                    )
                }
            }
        }
    }
}
