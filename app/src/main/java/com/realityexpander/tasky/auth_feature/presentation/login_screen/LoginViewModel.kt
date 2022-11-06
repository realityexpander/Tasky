package com.realityexpander.tasky.auth_feature.presentation.login_screen

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.R
import com.realityexpander.tasky.TaskyApplication
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.auth_feature.domain.validation.ValidateEmail
import com.realityexpander.tasky.auth_feature.domain.validation.ValidatePassword
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_authInfo
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_email
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_errorMessage
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_isInvalidEmail
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_isInvalidEmailMessageVisible
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_isInvalidPassword
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_isInvalidPasswordMessageVisible
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_password
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_statusMessage
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_username
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.Exceptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    val validateEmail: ValidateEmail,
    val validatePassword: ValidatePassword,
//    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val savedStateHandle: SavedStateHandle = TaskyApplication.savedStateHandle

    private val username: String =
        Uri.decode(savedStateHandle[SAVED_STATE_username]) ?: ""
    private val email: String =
        Uri.decode(savedStateHandle[SAVED_STATE_email]) ?: ""
    private val password: String =
        Uri.decode(savedStateHandle[SAVED_STATE_password]) ?: ""
    private val isInvalidEmail: Boolean =
        savedStateHandle[SAVED_STATE_isInvalidEmail] ?: false
    private val isInvalidEmailMessageVisible: Boolean =
        savedStateHandle[SAVED_STATE_isInvalidEmailMessageVisible] ?: false
    private val isInvalidPassword: Boolean =
        savedStateHandle[SAVED_STATE_isInvalidPassword] ?: false
    private val isInvalidPasswordMessageVisible: Boolean =
        savedStateHandle[SAVED_STATE_isInvalidPasswordMessageVisible] ?: false
    private val authInfo: AuthInfo? =
        savedStateHandle[SAVED_STATE_authInfo]
    private val statusMessage: UiText? =
        savedStateHandle[SAVED_STATE_statusMessage]
    private val errorMessage: UiText? =
        savedStateHandle[SAVED_STATE_errorMessage]

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.onEach { state ->
        // save state for process death
        savedStateHandle[SAVED_STATE_username] =
            state.username
        savedStateHandle[SAVED_STATE_email] =
            state.email
        savedStateHandle[SAVED_STATE_password] =
            state.password
        savedStateHandle[SAVED_STATE_isInvalidEmail] =
            state.isInvalidEmail
        savedStateHandle[SAVED_STATE_isInvalidEmailMessageVisible] =
            state.isInvalidEmailMessageVisible
        savedStateHandle[SAVED_STATE_isInvalidPassword] =
            state.isInvalidPassword
        savedStateHandle[SAVED_STATE_isInvalidPasswordMessageVisible] =
            state.isInvalidPasswordMessageVisible
        savedStateHandle[SAVED_STATE_authInfo] =
            state.authInfo
        savedStateHandle[SAVED_STATE_statusMessage] =
            state.statusMessage
        savedStateHandle[SAVED_STATE_errorMessage] =
            state.errorMessage

        // Validate as the user types
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
                isInvalidEmailMessageVisible = isInvalidEmailMessageVisible,
                isInvalidPassword = isInvalidPassword,
                isInvalidPasswordMessageVisible = isInvalidPasswordMessageVisible,
                authInfo = authInfo,
                statusMessage = statusMessage,
                errorMessage = errorMessage
            )
            yield() // allow loginState to be updated

            // Validate email & password when restored from process death or coming from another screen
            if (loginState.value.email.isNotBlank()) sendEvent(LoginEvent.ValidateEmail)
            if (loginState.value.password.isNotBlank()) sendEvent(LoginEvent.ValidatePassword)

            yield() // allow loginState to be updated
            // Show status validation messages when restored from process death or coming from another screen
            if(loginState.value.isInvalidEmail) sendEvent(LoginEvent.ShowInvalidEmailMessage)
            if(loginState.value.isInvalidPassword) sendEvent(LoginEvent.ShowInvalidPasswordMessage)
        }
    }

    private suspend fun login(email: String, password: String) {
        try {
            val authInfo = authRepository.login(email, password)

            // will be saved in datastore in the LoginScreen composable
            authRepository.setAuthInfo(authInfo)

            sendEvent(LoginEvent.LoginSuccess(authInfo))
        } catch(e: Exceptions.WrongPasswordException) {
            sendEvent(LoginEvent.LoginError(UiText.Res(R.string.error_login_error, e.message ?: "")))
        } catch(e: Exceptions.LoginException) {
            sendEvent(LoginEvent.LoginError(UiText.Res(R.string.error_login_error, e.message ?: "")))
        } catch (e: Exception) {
            sendEvent(LoginEvent.UnknownError(UiText.Res(R.string.error_unknown, e.message ?: "")))
            e.printStackTrace()
        }
    }

    fun sendEvent(event: LoginEvent) {
        viewModelScope.launch {
            onEvent(event)
            yield() // allow events to percolate
        }
    }

    private suspend fun onEvent(event: LoginEvent) {
        when(event) {
            is LoginEvent.SetIsLoading -> {
                _loginState.update {
                    it.copy(isLoading = event.isLoading)
                }
            }
            is LoginEvent.UpdateEmail -> {
                _loginState.update {
                    it.copy(
                        email = event.email,
                        isInvalidEmail = false,
                        isInvalidEmailMessageVisible = false,
                        errorMessage = UiText.None,
                    )
                }
            }
            is LoginEvent.UpdatePassword -> {
                _loginState.update {
                    it.copy(
                        password = event.password,
                        isInvalidPassword = false,
                        isInvalidPasswordMessageVisible = false,
                        errorMessage = UiText.None,
                    )
                }
            }
            is LoginEvent.SetIsPasswordVisible -> {
                _loginState.update {
                    it.copy(
                        isPasswordVisible = event.isPasswordVisible
                    )
                }
            }
            is LoginEvent.ValidateEmail -> {
                val isValid = validateEmail.validate(loginState.value.email)
                _loginState.update {
                    it.copy(
                        isInvalidEmail = !isValid,
                    )
                }
                yield()
            }
            is LoginEvent.ValidatePassword -> {
                val isValid = validatePassword.validate(loginState.value.password)
                _loginState.update {
                    it.copy(
                        isInvalidPassword = !isValid,
                    )
                }
                yield()
            }
            is LoginEvent.ShowInvalidEmailMessage -> {
                _loginState.value = _loginState.value.copy(
                    isInvalidEmailMessageVisible = true
                )
            }
            is LoginEvent.ShowInvalidPasswordMessage -> {
                _loginState.update {
                    it.copy(
                        isInvalidPasswordMessageVisible = true
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

                sendEvent(LoginEvent.SetIsLoading(true))
                login(event.email, event.password)
            }
            is LoginEvent.LoginSuccess -> {
                _loginState.update {
                    it.copy(
                        authInfo = event.authInfo,
                        errorMessage = UiText.None,
                        statusMessage = UiText.None, //UiText.Res(R.string.login_success, event.authInfo), // keep for debugging
                        isPasswordVisible = false,
                    )
                }
                sendEvent(LoginEvent.SetIsLoading(false))
            }
            is LoginEvent.LoginError -> {
                _loginState.update {
                    it.copy(
                        errorMessage = event.message,
                        statusMessage = UiText.None,
                        isLoading = false
                    )
                }
                sendEvent(LoginEvent.SetIsLoading(false))
            }
            is LoginEvent.UnknownError -> {
                _loginState.update {
                    it.copy(
                        errorMessage = if(event.message.isRes)
                            event.message
                        else
                            UiText.Res(R.string.error_unknown, ""),
                    )
                }
                sendEvent(LoginEvent.SetIsLoading(false))
            }
        }
    }
}
