package com.realityexpander.tasky.presentation.register_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.common.Exceptions
import com.realityexpander.tasky.domain.IAuthRepository
import com.realityexpander.tasky.domain.validation.IValidateEmail
import com.realityexpander.tasky.domain.validation.IValidatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val validateEmail: IValidateEmail,
    private val validatePassword: IValidatePassword,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState())
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    private val email: String = savedStateHandle["email"] ?: ""
    private val password: String = savedStateHandle["password"] ?: ""
    private val confirmPassword: String = savedStateHandle["confirmPassword"] ?: ""

    init {
        sendEvent(RegisterEvent.UpdateEmail(email))
        sendEvent(RegisterEvent.UpdatePassword(password))
        sendEvent(RegisterEvent.UpdateConfirmPassword(confirmPassword))
    }

    private suspend fun register(email: String, password: String) {
        try {
            val authToken = authRepository.register(email, password)
            sendEvent(RegisterEvent.RegisterSuccess(authToken))
        } catch(e: Exceptions.EmailAlreadyExistsException) {
            sendEvent(RegisterEvent.EmailAlreadyExists)
        } catch(e: Exceptions.LoginException) {
            sendEvent(RegisterEvent.RegisterError(e.message ?: "Unknown Login Error"))
        } catch(e: Exceptions.InvalidEmailException) {
            sendEvent(RegisterEvent.IsValidEmail(false))
        } catch(e: Exceptions.InvalidPasswordException) {
            sendEvent(RegisterEvent.IsValidPassword(false))
        } catch (e: Exception) {
            // handle general error
            sendEvent(RegisterEvent.UnknownError(e.message ?: "Unknown error"))
            e.printStackTrace()
        }
    }

    private fun validateEmail(email: String) {
        val isValid = validateEmail.validateEmail(email)
        sendEvent(RegisterEvent.IsValidEmail(isValid))
    }

    private fun validatePassword(password: String) {
        val isValid = validatePassword.validatePassword(password)
        sendEvent(RegisterEvent.IsValidPassword(isValid))
    }

    private fun validateConfirmPassword(confirmPassword: String) {
        val isValid = validatePassword.validatePassword(confirmPassword)
        sendEvent(RegisterEvent.IsValidConfirmPassword(isValid))
    }

    private fun validatePasswordsMatch(password: String, confirmPassword: String) {
        // Passwords are too short to match
        if(password.isEmpty() || confirmPassword.isEmpty()) {
            sendEvent(RegisterEvent.IsPasswordsMatch(true))

            return
        }

        val isMatch = (
//            registerState.value.password.length >= 6
//            && registerState.value.confirmPassword.length >= 6
//            !registerState.value.isInvalidPassword
//            && !registerState.value.isInvalidConfirmPassword
//            &&
                (password == confirmPassword)
        )
        sendEvent(RegisterEvent.IsPasswordsMatch(isMatch))
    }

    fun sendEvent(event: RegisterEvent) {
        viewModelScope.launch {
            onEvent(event)
        }
    }

    suspend fun onEvent(event: RegisterEvent) {
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
                savedStateHandle["email"] = event.email
            }
            is RegisterEvent.UpdatePassword -> {
                _registerState.value = _registerState.value.copy(
                    password = event.password,
                    isInvalidPassword = false,
                    isPasswordsMatch = false
                )
                savedStateHandle["password"] = event.password
                sendEvent(RegisterEvent.ValidatePasswordsMatch)
            }
            is RegisterEvent.UpdateConfirmPassword -> {
                _registerState.value = _registerState.value.copy(
                    confirmPassword = event.confirmPassword,
                    isInvalidConfirmPassword = false,
                    isPasswordsMatch = false
                )
                savedStateHandle["confirmPassword"] = event.confirmPassword
                sendEvent(RegisterEvent.ValidatePasswordsMatch)
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
            is RegisterEvent.Register -> {
                if(registerState.value.isInvalidEmail
                    || registerState.value.isInvalidPassword
                    || registerState.value.isInvalidConfirmPassword
                    || !registerState.value.isPasswordsMatch
                ) {
                    return
                }

                sendEvent(RegisterEvent.Loading(true))
                register(event.email, event.password)
            }
            is RegisterEvent.EmailAlreadyExists -> {
                _registerState.value = _registerState.value.copy(
                    isLoggedIn = false,
                    isError = true,
                    errorMessage = "Email already exists - try logging in!",
                    statusMessage = "",
                    isLoading = false,
                )
            }
            is RegisterEvent.RegisterSuccess -> {
                _registerState.value = _registerState.value.copy(
                    isLoggedIn = true,
                    isError = false,
                    errorMessage = "",
                    statusMessage = "Login Success: authToken = ${event.authToken}",
                    isPasswordVisible = false,
                    isLoading = false,
                )
            }
            is RegisterEvent.RegisterError -> {
                _registerState.value = _registerState.value.copy(
                    isLoggedIn = false,
                    isError = true,
                    errorMessage = event.message,
                    statusMessage = "",
                    isLoading = false,
                )
            }
            is RegisterEvent.UnknownError -> {
                _registerState.value = _registerState.value
                    .copy(isError = true, errorMessage = event.message)
            }

        }
    }
}
