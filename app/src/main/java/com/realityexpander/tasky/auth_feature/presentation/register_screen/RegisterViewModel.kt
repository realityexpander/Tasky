package com.realityexpander.tasky.auth_feature.presentation.register_screen

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.R
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.auth_feature.domain.validation.ValidateEmail
import com.realityexpander.tasky.auth_feature.domain.validation.ValidatePassword
import com.realityexpander.tasky.auth_feature.domain.validation.ValidateUsername
import com.realityexpander.tasky.core.common.Exceptions
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_authInfo
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_confirmPassword
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_email
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_errorMessage
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_isInvalidConfirmPassword
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_isInvalidConfirmPasswordMessageVisible
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_isInvalidEmail
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_isInvalidEmailMessageVisible
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_isInvalidPassword
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_isInvalidPasswordMessageVisible
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_isPasswordsMatch
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_password
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_statusMessage
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_username
import com.realityexpander.tasky.core.presentation.common.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    val validateEmail: ValidateEmail,
    val validatePassword: ValidatePassword,
    val validateUsername: ValidateUsername,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState = _registerState.onEach { state ->
        // save state for process death
        savedStateHandle[SAVED_STATE_username] =
            state.username
        savedStateHandle[SAVED_STATE_email] =
            state.email
        savedStateHandle[SAVED_STATE_password] =
            state.password
        savedStateHandle[SAVED_STATE_confirmPassword] =
            state.confirmPassword
        savedStateHandle[SAVED_STATE_isInvalidEmail] =
            state.isInvalidEmail
        savedStateHandle[SAVED_STATE_isInvalidEmailMessageVisible] =
            state.isInvalidEmailMessageVisible
        savedStateHandle[SAVED_STATE_isInvalidPassword] =
            state.isInvalidPassword
        savedStateHandle[SAVED_STATE_isInvalidPasswordMessageVisible] =
            state.isInvalidPasswordMessageVisible
        savedStateHandle[SAVED_STATE_isInvalidConfirmPassword] =
            state.isInvalidConfirmPassword
        savedStateHandle[SAVED_STATE_isInvalidConfirmPasswordMessageVisible] =
            state.isInvalidConfirmPasswordMessageVisible
        savedStateHandle[SAVED_STATE_isPasswordsMatch] =
            state.isPasswordsMatch
        savedStateHandle[SAVED_STATE_authInfo] =
            state.authInfo
        savedStateHandle[SAVED_STATE_statusMessage] =
            state.statusMessage
        savedStateHandle[SAVED_STATE_errorMessage] =
            state.errorMessage

        // Validate as the user types
        if(state.username.isNotBlank()) sendEvent(RegisterEvent.ValidateUsername)
        if(state.email.isNotBlank()) sendEvent(RegisterEvent.ValidateEmail)
        if(state.password.isNotBlank()) sendEvent(RegisterEvent.ValidatePassword)
        if(state.confirmPassword.isNotBlank()) sendEvent(RegisterEvent.ValidateConfirmPassword)
        sendEvent(RegisterEvent.ValidatePasswordsMatch)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RegisterState())

    init {
        viewModelScope.launch {
            yield() // allow the registerState to be initialized

            // restore state after process death or from another screen (e.g. login)
            _registerState.value = RegisterState(
                username =
                    Uri.decode(savedStateHandle[SAVED_STATE_username]) ?: "",
                email =
                    Uri.decode(savedStateHandle[SAVED_STATE_email]) ?: "",
                password =
                    Uri.decode(savedStateHandle[SAVED_STATE_password]) ?: "",
                confirmPassword =
                    Uri.decode(savedStateHandle[SAVED_STATE_confirmPassword]) ?: "",
                isInvalidEmail =
                    savedStateHandle[SAVED_STATE_isInvalidEmail] ?: false,
                isInvalidEmailMessageVisible =
                    savedStateHandle[SAVED_STATE_isInvalidEmailMessageVisible] ?: false,
                isInvalidPassword =
                    savedStateHandle[SAVED_STATE_isInvalidPassword] ?: false,
                isInvalidPasswordMessageVisible =
                    savedStateHandle[SAVED_STATE_isInvalidPasswordMessageVisible] ?: false,
                isInvalidConfirmPassword =
                    savedStateHandle[SAVED_STATE_isInvalidConfirmPassword] ?: false,
                isInvalidConfirmPasswordMessageVisible =
                    savedStateHandle[SAVED_STATE_isInvalidConfirmPasswordMessageVisible] ?: false,
                isPasswordsMatch =
                    savedStateHandle[SAVED_STATE_isPasswordsMatch] ?: true,
                authInfo =
                    savedStateHandle[SAVED_STATE_authInfo],
                statusMessage =
                    savedStateHandle[SAVED_STATE_statusMessage],
                errorMessage =
                    savedStateHandle[SAVED_STATE_errorMessage]
            )
            yield() // allow the registerState to be updated

            // Validate email & password when restored from process death or coming from another screen
            if (registerState.value.username.isNotBlank()) sendEvent(RegisterEvent.ValidateUsername)
            if (registerState.value.email.isNotBlank()) sendEvent(RegisterEvent.ValidateEmail)
            if (registerState.value.password.isNotBlank()) sendEvent(RegisterEvent.ValidatePassword)
            if (registerState.value.confirmPassword.isNotBlank()) sendEvent(RegisterEvent.ValidateConfirmPassword)
            sendEvent(RegisterEvent.ValidatePasswordsMatch)

            yield() // allow the registerState to be updated
            // Show status validation messages when restored from process death or coming from another screen
            if(registerState.value.isInvalidUsername) sendEvent(RegisterEvent.ShowInvalidUsernameMessage)
            if(registerState.value.isInvalidEmail) sendEvent(RegisterEvent.ShowInvalidEmailMessage)
            if(registerState.value.isInvalidPassword) sendEvent(RegisterEvent.ShowInvalidPasswordMessage)
            if(registerState.value.isInvalidConfirmPassword) sendEvent(RegisterEvent.ShowInvalidConfirmPasswordMessage)
        }
    }

    private suspend fun register(
        username: String,
        email: String,
        password: String
    ) {
        try {
            authRepository.register(username, email, password)

            sendEvent(RegisterEvent.RegisterSuccess(UiText.Res(R.string.register_success)))
        } catch(e: Exceptions.EmailAlreadyExistsException) {
            sendEvent(RegisterEvent.EmailAlreadyExists)
        } catch(e: Exceptions.RegisterException) {
            sendEvent(RegisterEvent.RegisterError(UiText.Res(R.string.register_register_error, e.message ?: "")))
        } catch(e: Exceptions.NetworkException) {
            sendEvent(RegisterEvent.RegisterError(UiText.Res(R.string.register_network_error, e.message ?: "")))
        } catch (e: Exception) {
            sendEvent(RegisterEvent.UnknownError(UiText.Res( R.string.error_unknown, e.message ?: "")))
            e.printStackTrace()
        }
    }

    private fun validatePasswordsMatch(): Boolean {
        // Both passwords must have at least 1 character to validate match
        if (registerState.value.password.isBlank()
            || registerState.value.confirmPassword.isBlank()
        ) {
            return true
        }

        return (registerState.value.password == registerState.value.confirmPassword)
    }

    fun sendEvent(event: RegisterEvent) {
        viewModelScope.launch {
            onEvent(event)
            yield() // allow events to percolate
        }
    }

    private suspend fun onEvent(event: RegisterEvent) {
        when(event) {
            is RegisterEvent.SetIsLoading -> {
                _registerState.update {
                    it.copy(isLoading = event.isLoading)
                }
            }
            is RegisterEvent.UpdateUsername -> {
                _registerState.update {
                    it.copy(
                        username = event.username,
                        isInvalidUsername = false,
                        isInvalidUsernameMessageVisible = false
                    )
                }
            }
            is RegisterEvent.UpdateEmail -> {
                _registerState.update {
                    it.copy(
                        email = event.email,
                        isInvalidEmail = false,
                        isInvalidEmailMessageVisible = false
                    )
                }
            }
            is RegisterEvent.UpdatePassword -> {
                _registerState.update {
                    it.copy(
                        password = event.password,
                        isInvalidPassword = false,
                        isInvalidPasswordMessageVisible = false,
                        isPasswordsMatch = false
                    )
                }
            }
            is RegisterEvent.UpdateConfirmPassword -> {
                _registerState.update {
                    it.copy(
                        confirmPassword = event.confirmPassword,
                        isInvalidConfirmPassword = false,
                        isInvalidConfirmPasswordMessageVisible = false,
                        isPasswordsMatch = false
                    )
                }
            }
            is RegisterEvent.SetIsPasswordVisible -> {
                _registerState.update {
                    it.copy(isPasswordVisible = event.isVisible)
                }
            }
            is RegisterEvent.ValidateUsername -> {
                val isValid = validateUsername.validate(registerState.value.username)
                _registerState.update {
                    it.copy(isInvalidUsername = !isValid)
                }
                yield()
            }
            is RegisterEvent.ValidateEmail -> {
                val isValid = validateEmail.validate(registerState.value.email)
                _registerState.update {
                    it.copy(
                        isInvalidEmail = !isValid
                    )
                }
                yield()
            }
            is RegisterEvent.ValidatePassword -> {
                val isValid = validatePassword.validate(registerState.value.password)
                _registerState.update {
                    it.copy(
                        isInvalidPassword = !isValid
                    )
                }
                yield()
            }
            is RegisterEvent.ValidateConfirmPassword -> {
                val isValid = validatePassword.validate(registerState.value.confirmPassword)
                _registerState.update {
                    it.copy(
                        isInvalidConfirmPassword = !isValid
                    )
                }
                yield()
            }
            is RegisterEvent.ValidatePasswordsMatch -> {
                _registerState.update {
                    it.copy(
                        isPasswordsMatch = validatePasswordsMatch()
                    )
                }
                yield()
            }
            is RegisterEvent.ShowInvalidUsernameMessage -> {
                _registerState.update {
                    it.copy(
                        isInvalidUsernameMessageVisible = true
                    )
                }
            }
            is RegisterEvent.ShowInvalidEmailMessage -> {
                _registerState.update {
                    it.copy(
                        isInvalidEmailMessageVisible = true
                    )
                }
            }
            is RegisterEvent.ShowInvalidPasswordMessage -> {
                _registerState.update {
                    it.copy(
                        isInvalidPasswordMessageVisible = true
                    )
                }
            }
            is RegisterEvent.ShowInvalidConfirmPasswordMessage -> {
                _registerState.update {
                    it.copy(
                        isInvalidConfirmPasswordMessageVisible = true
                    )
                }
            }
            is RegisterEvent.Register -> {
                sendEvent(RegisterEvent.ValidateUsername)
                sendEvent(RegisterEvent.ValidateEmail)
                sendEvent(RegisterEvent.ValidatePassword)
                sendEvent(RegisterEvent.ValidateConfirmPassword)
                sendEvent(RegisterEvent.ValidatePasswordsMatch)
                yield()

                // Only show `Invalid Username` message only when "login" is clicked and the username is invalid.
                if(_registerState.value.isInvalidUsername)
                    sendEvent(RegisterEvent.ShowInvalidUsernameMessage)

                // Only show `Invalid Email` message only when "login" is clicked and the email is invalid.
                if(_registerState.value.isInvalidEmail)
                    sendEvent(RegisterEvent.ShowInvalidEmailMessage)

                // Only show `Invalid Password` message only when "login" is clicked and the password is invalid.
                if(_registerState.value.isInvalidPassword)
                    sendEvent(RegisterEvent.ShowInvalidPasswordMessage)

                // Only show `Invalid Confirm Password` message only when "login" is clicked and the confirm password is invalid.
                if(_registerState.value.isInvalidConfirmPassword)
                    sendEvent(RegisterEvent.ShowInvalidConfirmPasswordMessage)

                if( _registerState.value.isInvalidUsername
                    || _registerState.value.isInvalidEmail
                    || _registerState.value.isInvalidPassword
                    || _registerState.value.isInvalidConfirmPassword
                    || !registerState.value.isPasswordsMatch
                ) return

                sendEvent(RegisterEvent.SetIsLoading(true))
                register(event.username, event.email, event.password)
            }
            is RegisterEvent.EmailAlreadyExists -> {
                _registerState.update {
                    it.copy(
                        errorMessage = UiText.Res(R.string.register_error_email_exists),
                        statusMessage = null
                    )
                }
                sendEvent(RegisterEvent.SetIsLoading(false))
            }
            is RegisterEvent.RegisterSuccess -> {
                _registerState.update {
                    it.copy(
                        errorMessage = null,
                        statusMessage = UiText.Res(R.string.register_success),
                        isPasswordVisible = false
                    )
                }
                sendEvent(RegisterEvent.SetIsLoading(false))
            }
            is RegisterEvent.RegisterError -> {
                _registerState.update {
                    it.copy(
                        errorMessage = event.message,
                        statusMessage = null
                    )
                }
                sendEvent(RegisterEvent.SetIsLoading(false))
            }
            is RegisterEvent.UnknownError -> {
                _registerState.update {
                    it.copy(
                        errorMessage = if(event.message.isRes)
                            event.message
                        else
                            UiText.Res(R.string.error_unknown, "")
                    )
                }
                sendEvent(RegisterEvent.SetIsLoading(false))
            }

        }
    }
}