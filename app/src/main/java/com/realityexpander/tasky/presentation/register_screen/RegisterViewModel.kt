package com.realityexpander.tasky.presentation.register_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.common.Exceptions
import com.realityexpander.tasky.domain.IAuthRepository
import com.realityexpander.tasky.domain.validation.IValidateEmail
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
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var _registerStateFlow = MutableStateFlow<State>(State())
    var registerStateFlow: StateFlow<State> = _registerStateFlow.asStateFlow()

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
            sendEvent(RegisterEvent.IsInvalidEmail)
        } catch(e: Exceptions.InvalidPasswordException) {
            sendEvent(RegisterEvent.IsInvalidPassword)
        } catch (e: Exception) {
            // handle general error
            sendEvent(RegisterEvent.UnknownError(e.message ?: "Unknown error"))
            e.printStackTrace()
        }
    }


    private fun validateEmail(email: String) {
        if (validateEmail.validateEmail(email)) {
            sendEvent(RegisterEvent.IsValidEmail)
        } else {
            sendEvent(RegisterEvent.IsInvalidEmail)
        }
    }

    private fun validatePassword(password: String) {
        if (password.length >= 6 ) { // && validatePassword.validatePassword(password)) { //todo
            sendEvent(RegisterEvent.IsValidPassword)
        } else {
            sendEvent(RegisterEvent.IsInvalidPassword)
        }
    }

    private fun validateConfirmPassword(confirmPassword: String) {
        if (confirmPassword.length >= 6 ) { // && validatePassword.validatePassword(password)) { //todo
            sendEvent(RegisterEvent.IsValidConfirmPassword)
        } else {
            sendEvent(RegisterEvent.IsInvalidConfirmPassword)
        }
    }

    fun sendEvent(event: RegisterEvent) {
        viewModelScope.launch {
            onEvent(event)
        }
    }

    suspend fun onEvent(event: RegisterEvent) {
        when(event) {
            is RegisterEvent.Loading -> {
                if(event.isLoading) {
                    _registerStateFlow.value = _registerStateFlow.value
                        .copy(isLoading = true)
                } else {
                    _registerStateFlow.value = _registerStateFlow.value
                        .copy(isLoading = false)
                }
            }
            is RegisterEvent.UpdateEmail -> {
                _registerStateFlow.value = _registerStateFlow.value
                    .copy(
                        email = event.email,
                        isInvalidEmail = false
                    )
                savedStateHandle["email"] = event.email
            }
            is RegisterEvent.UpdatePassword -> {
                _registerStateFlow.value = _registerStateFlow.value
                    .copy(
                        password = event.password,
                        isInvalidPassword = false
                    )
                savedStateHandle["password"] = event.password
            }
            is RegisterEvent.UpdateConfirmPassword -> {
                _registerStateFlow.value = _registerStateFlow.value
                    .copy(
                        confirmPassword = event.confirmPassword,
                        isInvalidConfirmPassword = false
                    )
                savedStateHandle["confirmPassword"] = event.confirmPassword
            }
            is RegisterEvent.Register -> {
                if(_registerStateFlow.value.isInvalidEmail
                    || _registerStateFlow.value.isInvalidPassword
                    || _registerStateFlow.value.isInvalidConfirmPassword
                    || _registerStateFlow.value.email.isBlank()
                    || _registerStateFlow.value.password.isBlank()
                    || _registerStateFlow.value.confirmPassword.isBlank()
                    || (_registerStateFlow.value.password != _registerStateFlow.value.confirmPassword)
                ) {
                    return
                }

                sendEvent(RegisterEvent.Loading(true))
                register(event.email, event.password)
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
            is RegisterEvent.EmailAlreadyExists -> {
                _registerStateFlow.value = _registerStateFlow.value
                    .copy(
                        isLoggedIn = false,
                        isError = true,
                        errorMessage = "Email already exists - try logging in!",
                        statusMessage = "",
                        isLoading = false,
                    )
            }
            is RegisterEvent.RegisterSuccess -> {
                _registerStateFlow.value = _registerStateFlow.value
                    .copy(
                        isLoggedIn = true,
                        isError = false,
                        errorMessage = "",
                        statusMessage = "Login Success: authToken = ${event.authToken}",
                        isPasswordVisible = false,
                    )
                sendEvent(RegisterEvent.Loading(false))
            }
            is RegisterEvent.RegisterError -> {
                _registerStateFlow.value = _registerStateFlow.value
                    .copy(
                        isLoggedIn = false,
                        isError = true,
                        errorMessage = event.message,
                        statusMessage = ""
                    )

                sendEvent(RegisterEvent.Loading(false))
            }
            is RegisterEvent.IsInvalidEmail -> {
                _registerStateFlow.value = _registerStateFlow.value
                    .copy(isInvalidEmail = true)
            }
            is RegisterEvent.IsInvalidPassword -> {
                _registerStateFlow.value = _registerStateFlow.value
                    .copy(isInvalidPassword = true)
            }
            is RegisterEvent.IsInvalidConfirmPassword -> {
                _registerStateFlow.value = _registerStateFlow.value
                    .copy(isInvalidConfirmPassword = true)
            }
            is RegisterEvent.IsValidEmail -> {
                _registerStateFlow.value = _registerStateFlow.value
                    .copy(isInvalidEmail = false)
            }
            is RegisterEvent.IsValidPassword -> {
                _registerStateFlow.value = _registerStateFlow.value
                    .copy(isInvalidPassword = false)
            }
            is RegisterEvent.IsValidConfirmPassword -> {
                _registerStateFlow.value = _registerStateFlow.value
                    .copy(isInvalidConfirmPassword = false)
            }
            is RegisterEvent.UnknownError -> {
                _registerStateFlow.value = _registerStateFlow.value
                    .copy(isError = true, errorMessage = event.message)
            }
            is RegisterEvent.TogglePasswordVisibility -> {
                _registerStateFlow.value = _registerStateFlow.value
                    .copy(isPasswordVisible = !event.isVisible)
            }
        }
    }
}
