package com.realityexpander.tasky.presentation.register_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.R
import com.realityexpander.tasky.common.Exceptions
import com.realityexpander.tasky.common.UiText
import com.realityexpander.tasky.domain.IAuthRepository
import com.realityexpander.tasky.domain.validation.IValidateEmail
import com.realityexpander.tasky.domain.validation.ValidatePassword
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_confirmPassword
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_email
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_errorMessage
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_isError
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_isInvalidConfirmPassword
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_isInvalidEmail
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_isInvalidPassword
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_isLoggedIn
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_isPasswordsMatch
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_password
import com.realityexpander.tasky.presentation.common.UIConstants.SAVED_STATE_statusMessage
import com.realityexpander.tasky.presentation.login_screen.LoginEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val validateEmail: IValidateEmail,
    private val validatePassword: ValidatePassword,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState())
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    private val email: String = savedStateHandle[SAVED_STATE_email] ?: ""
    private val password: String = savedStateHandle[SAVED_STATE_password] ?: ""
    private val confirmPassword: String = savedStateHandle[SAVED_STATE_confirmPassword] ?: ""
    private val isInvalidEmail: Boolean = savedStateHandle[SAVED_STATE_isInvalidEmail] ?: false
    private val isInvalidPassword: Boolean = savedStateHandle[SAVED_STATE_isInvalidPassword] ?: false
    private val isInvalidConfirmPassword: Boolean = savedStateHandle[SAVED_STATE_isInvalidConfirmPassword] ?: false
    private val isPasswordsMatch: Boolean = savedStateHandle[SAVED_STATE_isPasswordsMatch] ?: true
    private val isLoggedIn: Boolean = savedStateHandle[SAVED_STATE_isLoggedIn] ?: false
//    private val isError: Boolean = savedStateHandle[SAVED_STATE_isError] ?: false
    private val statusMessage: UiText = savedStateHandle[SAVED_STATE_statusMessage] ?: UiText.None
    private val errorMessage: UiText = savedStateHandle[SAVED_STATE_errorMessage] ?: UiText.None

    init {
//        sendEvent(RegisterEvent.UpdateEmail(email))
//        sendEvent(RegisterEvent.UpdatePassword(password))
//        sendEvent(RegisterEvent.UpdateConfirmPassword(confirmPassword))

        // restore state after process death
        _registerState.value = RegisterState(
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            isInvalidEmail = isInvalidEmail,
            isInvalidPassword = isInvalidPassword,
            isInvalidConfirmPassword = isInvalidConfirmPassword,
            isPasswordsMatch = isPasswordsMatch,
            isLoggedIn = isLoggedIn,
//            isError = isError,
            statusMessage = statusMessage,
            errorMessage = errorMessage,
        )

        // Save state for process death
        viewModelScope.launch {
            yield()
            registerState.collect {
                savedStateHandle[SAVED_STATE_email] = it.email
                savedStateHandle[SAVED_STATE_password] = it.password
                savedStateHandle[SAVED_STATE_confirmPassword] = it.confirmPassword
                savedStateHandle[SAVED_STATE_isInvalidEmail] = it.isInvalidEmail
                savedStateHandle[SAVED_STATE_isInvalidPassword] = it.isInvalidPassword
                savedStateHandle[SAVED_STATE_isInvalidConfirmPassword] = it.isInvalidConfirmPassword
                savedStateHandle[SAVED_STATE_isPasswordsMatch] = it.isPasswordsMatch
                savedStateHandle[SAVED_STATE_isLoggedIn] = it.isLoggedIn
//                savedStateHandle[SAVED_STATE_isError] = it.isError
                savedStateHandle[SAVED_STATE_statusMessage] = it.statusMessage
                savedStateHandle[SAVED_STATE_errorMessage] = it.errorMessage

                if(email.isNotBlank()) sendEvent(RegisterEvent.ValidateEmail(email))
                if(password.isNotBlank()) sendEvent(RegisterEvent.ValidatePassword(password))
                if(confirmPassword.isNotBlank()) sendEvent(RegisterEvent.ValidateConfirmPassword(confirmPassword))
                sendEvent(RegisterEvent.ValidatePasswordsMatch)
            }
        }

//        if(email.isNotBlank()) sendEvent(RegisterEvent.ValidateEmail(email))
//        if(password.isNotBlank()) sendEvent(RegisterEvent.ValidatePassword(password))
//        if(confirmPassword.isNotBlank()) sendEvent(RegisterEvent.ValidateConfirmPassword(confirmPassword))
//        sendEvent(RegisterEvent.ValidatePasswordsMatch)
    }

    private suspend fun register(email: String, password: String) {
        try {
            val authToken = authRepository.register(email, password)
            sendEvent(RegisterEvent.RegisterSuccess(authToken))
        } catch(e: Exceptions.EmailAlreadyExistsException) {
            sendEvent(RegisterEvent.EmailAlreadyExists)
        } catch(e: Exceptions.LoginException) {
            sendEvent(RegisterEvent.RegisterError(UiText.Res(R.string.register_register_error, e.message ?: "")))
        } catch(e: Exceptions.InvalidEmailException) {
            sendEvent(RegisterEvent.IsValidEmail(false))
        } catch(e: Exceptions.InvalidPasswordException) {
            sendEvent(RegisterEvent.IsValidPassword(false))
        } catch (e: Exception) {
            // handle general error
            sendEvent(RegisterEvent.UnknownError(UiText.Res( R.string.error_unknown, e.message ?: "")))
            e.printStackTrace()
        }
    }

    private fun validateEmail(email: String) {
        val isValid = validateEmail.validate(email)
        sendEvent(RegisterEvent.IsValidEmail(isValid))
    }

    private fun validatePassword(password: String) {
        val isValid = validatePassword.validate(password)
        sendEvent(RegisterEvent.IsValidPassword(isValid))
    }

    private fun validateConfirmPassword(confirmPassword: String) {
        val isValid = validatePassword.validate(confirmPassword)
        sendEvent(RegisterEvent.IsValidConfirmPassword(isValid))
    }

    private fun validatePasswordsMatch(password: String, confirmPassword: String) {
        // Both passwords must have at least 1 character to validate match
        if(password.isBlank() || confirmPassword.isBlank()) {
            sendEvent(RegisterEvent.IsPasswordsMatch(true))

            return
        }

        val isMatch = (password == confirmPassword)
        sendEvent(RegisterEvent.IsPasswordsMatch(isMatch))
    }

    fun sendEvent(event: RegisterEvent) {
        viewModelScope.launch {
            onEvent(event)
            yield()
        }
    }

    private suspend fun onEvent(event: RegisterEvent) {
        when(event) {
            is RegisterEvent.Loading -> {
                _registerState.value = _registerState.value.copy(
                    isLoading = event.isLoading
                )
            }
            is RegisterEvent.UpdateEmail -> {
                _registerState.value = _registerState.value.copy(
                    email = event.email,
                    isInvalidEmail = false
                )
//                savedStateHandle[SAVED_STATE_email] = event.email
            }
            is RegisterEvent.UpdatePassword -> {
                _registerState.value = _registerState.value.copy(
                    password = event.password,
                    isInvalidPassword = false,
                    isPasswordsMatch = false
                )
//                savedStateHandle[SAVED_STATE_password] = event.password
//                sendEvent(RegisterEvent.ValidatePasswordsMatch)
            }
            is RegisterEvent.UpdateConfirmPassword -> {
                _registerState.value = _registerState.value.copy(
                    confirmPassword = event.confirmPassword,
                    isInvalidConfirmPassword = false,
                    isPasswordsMatch = false
                )
//                savedStateHandle[SAVED_STATE_confirmPassword] = event.confirmPassword
//                sendEvent(RegisterEvent.ValidatePasswordsMatch)
            }
            is RegisterEvent.TogglePasswordVisibility -> {
                _registerState.value = _registerState.value
                    .copy(isPasswordVisible = !event.isVisible)
            }
            is RegisterEvent.ValidateEmail -> {
                validateEmail(event.email)
            }
            is RegisterEvent.ValidatePassword -> {
                validatePassword(event.password)
            }
            is RegisterEvent.ValidateConfirmPassword -> {
                validateConfirmPassword(event.confirmPassword)
            }
            is RegisterEvent.ValidatePasswordsMatch -> {
                validatePasswordsMatch(
                    registerState.value.password,
                    registerState.value.confirmPassword
                )
            }
            is RegisterEvent.IsValidEmail -> {
                _registerState.value = _registerState.value
                    .copy(isInvalidEmail = !event.isValid)
            }
            is RegisterEvent.IsValidPassword -> {
                _registerState.value = _registerState.value
                    .copy(isInvalidPassword = !event.isValid)
            }
            is RegisterEvent.IsValidConfirmPassword -> {
                _registerState.value = _registerState.value
                    .copy(isInvalidConfirmPassword = !event.isValid)
            }
            is RegisterEvent.IsPasswordsMatch -> {
                _registerState.value = _registerState.value.copy(
                    isPasswordsMatch = event.isMatch
                )
            }
//            is RegisterEvent.Register -> {
//                if(registerState.value.isInvalidEmail
//                    || registerState.value.isInvalidPassword
//                    || registerState.value.isInvalidConfirmPassword
//                    || !registerState.value.isPasswordsMatch
//                ) {
//                    return
//                }
//
//                sendEvent(RegisterEvent.Loading(true))
//                register(event.email, event.password)
//            }
            is RegisterEvent.Register -> {
                sendEvent(RegisterEvent.ValidateEmail(event.email))
                sendEvent(RegisterEvent.ValidatePassword(event.password))
                sendEvent(RegisterEvent.ValidateConfirmPassword(event.confirmPassword))
                sendEvent(RegisterEvent.ValidatePasswordsMatch)
                yield()

                if(_registerState.value.isInvalidEmail
                    || _registerState.value.isInvalidPassword
                    || _registerState.value.isInvalidConfirmPassword
                    || !registerState.value.isPasswordsMatch
                ) return

                sendEvent(RegisterEvent.Loading(true))
                register(event.email, event.password)
            }
            is RegisterEvent.EmailAlreadyExists -> {
                _registerState.value = _registerState.value.copy(
                    isLoggedIn = false,
//                    isError = true,
                    errorMessage = UiText.Res(R.string.register_error_email_exists),
                    statusMessage = UiText.None,
                    isLoading = false,
                )
            }
            is RegisterEvent.RegisterSuccess -> {
                _registerState.value = _registerState.value.copy(
                    isLoggedIn = true,
//                    isError = false,
                    errorMessage = UiText.None,
                    statusMessage = UiText.Res(R.string.register_success, event.authToken),
                    isPasswordVisible = false,
                    isLoading = false,
                )
            }
            is RegisterEvent.RegisterError -> {
                _registerState.value = _registerState.value.copy(
                    isLoggedIn = false,
//                    isError = true,
                    errorMessage = event.message,
                    statusMessage = UiText.None,
                    isLoading = false,
                )
            }
            is RegisterEvent.UnknownError -> {
                _registerState.value = _registerState.value.copy(
//                    isError = true,
                    errorMessage = event.message
                )
            }

        }
    }
}
