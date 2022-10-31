package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.R
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.core.common.Exceptions
import com.realityexpander.tasky.core.presentation.common.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(
//    @AuthRepositoryFakeUsingProvides
//    @AuthRepositoryProd_AuthApiProd_AuthDaoFake
    private val authRepository: IAuthRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Get params from savedStateHandle (from another screen or after process death)
//    private val username: String =
//        Uri.decode(savedStateHandle[SAVED_STATE_username]) ?: ""
//    private val email: String =
//        Uri.decode(savedStateHandle[SAVED_STATE_email]) ?: ""
//    private val password: String =
//        Uri.decode(savedStateHandle[SAVED_STATE_password]) ?: ""
//    private val confirmPassword: String =
//        Uri.decode(savedStateHandle[SAVED_STATE_confirmPassword]) ?: ""
//    private val isInvalidEmail: Boolean =
//        savedStateHandle[SAVED_STATE_isInvalidEmail] ?: false
//    private val isShowInvalidEmailMessage: Boolean =
//        savedStateHandle[SAVED_STATE_isShowInvalidEmailMessage] ?: false
//    private val isInvalidPassword: Boolean =
//        savedStateHandle[SAVED_STATE_isInvalidPassword] ?: false
//    private val isShowInvalidPasswordMessage: Boolean =
//        savedStateHandle[SAVED_STATE_isShowInvalidPasswordMessage] ?: false
//    private val isInvalidConfirmPassword: Boolean =
//        savedStateHandle[SAVED_STATE_isInvalidConfirmPassword] ?: false
//    private val isShowInvalidConfirmPasswordMessage: Boolean =
//        savedStateHandle[SAVED_STATE_isShowInvalidConfirmPasswordMessage] ?: false
//    private val isPasswordsMatch: Boolean =
//        savedStateHandle[SAVED_STATE_isPasswordsMatch] ?: true
//    private val isLoggedIn: Boolean =
//        savedStateHandle[SAVED_STATE_isLoggedIn] ?: false
//    private val statusMessage: UiText =
//        savedStateHandle[SAVED_STATE_statusMessage] ?: UiText.None
//    private val errorMessage: UiText =
//        savedStateHandle[SAVED_STATE_errorMessage] ?: UiText.None

    private val _agendaState = MutableStateFlow(AgendaState())
    val agendaState = _agendaState.onEach { state ->
        // save state for process death
//        savedStateHandle[SAVED_STATE_username] = state.username
//        savedStateHandle[SAVED_STATE_email] = state.email
//        savedStateHandle[SAVED_STATE_password] = state.password
//        savedStateHandle[SAVED_STATE_confirmPassword] = state.confirmPassword
//        savedStateHandle[SAVED_STATE_isInvalidEmail] = state.isInvalidEmail
//        savedStateHandle[SAVED_STATE_isShowInvalidEmailMessage] = state.isShowInvalidEmailMessage
//        savedStateHandle[SAVED_STATE_isInvalidPassword] = state.isInvalidPassword
//        savedStateHandle[SAVED_STATE_isShowInvalidPasswordMessage] = state.isShowInvalidPasswordMessage
//        savedStateHandle[SAVED_STATE_isInvalidConfirmPassword] = state.isInvalidConfirmPassword
//        savedStateHandle[SAVED_STATE_isShowInvalidConfirmPasswordMessage] = state.isShowInvalidConfirmPasswordMessage
//        savedStateHandle[SAVED_STATE_isPasswordsMatch] = state.isPasswordsMatch
//        savedStateHandle[SAVED_STATE_isLoggedIn] = state.isLoggedIn
//        savedStateHandle[SAVED_STATE_statusMessage] = state.statusMessage
//        savedStateHandle[SAVED_STATE_errorMessage] = state.errorMessage

        // Validate as the user types
        if(state.username.isNotBlank()) sendEvent(AgendaEvent.ValidateUsername)
        if(state.email.isNotBlank()) sendEvent(AgendaEvent.ValidateEmail)
        if(state.password.isNotBlank()) sendEvent(AgendaEvent.ValidatePassword)
        if(state.confirmPassword.isNotBlank()) sendEvent(AgendaEvent.ValidateConfirmPassword)
        sendEvent(AgendaEvent.ValidatePasswordsMatch)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AgendaState())

    init {
        viewModelScope.launch {
            yield() // allow the agendaState to be initialized

            // restore state after process death
            _agendaState.value = AgendaState(
//                username = username,
//                email = email,
//                password = password,
//                confirmPassword = confirmPassword,
//                isInvalidEmail = isInvalidEmail,
//                isShowInvalidEmailMessage = isShowInvalidEmailMessage,
//                isInvalidPassword = isInvalidPassword,
//                isShowInvalidPasswordMessage = isShowInvalidPasswordMessage,
//                isInvalidConfirmPassword = isInvalidConfirmPassword,
//                isShowInvalidConfirmPasswordMessage = isShowInvalidConfirmPasswordMessage,
//                isPasswordsMatch = isPasswordsMatch,
//                isLoggedIn = isLoggedIn,
//                statusMessage = statusMessage,
//                errorMessage = errorMessage,
            )
            yield() // allow the agendaState to be updated

            // Validate email & password when restored from process death or coming from another screen
            if (agendaState.value.username.isNotBlank()) sendEvent(AgendaEvent.ValidateUsername)
            if (agendaState.value.email.isNotBlank()) sendEvent(AgendaEvent.ValidateEmail)
            if (agendaState.value.password.isNotBlank()) sendEvent(AgendaEvent.ValidatePassword)
            if (agendaState.value.confirmPassword.isNotBlank()) sendEvent(AgendaEvent.ValidateConfirmPassword)
            sendEvent(AgendaEvent.ValidatePasswordsMatch)

            yield() // allow the agendaState to be updated
            // Show status validation messages when restored from process death or coming from another screen
            if(agendaState.value.isInvalidUsername) sendEvent(AgendaEvent.ShowInvalidUsernameMessage)
            if(agendaState.value.isInvalidEmail) sendEvent(AgendaEvent.ShowInvalidEmailMessage)
            if(agendaState.value.isInvalidPassword) sendEvent(AgendaEvent.ShowInvalidPasswordMessage)
            if(agendaState.value.isInvalidConfirmPassword) sendEvent(AgendaEvent.ShowInvalidConfirmPasswordMessage)
        }
    }

    private suspend fun register(
        username: String,
        email: String,
        password: String
    ) {
        try {
            authRepository.register(username, email, password)
            sendEvent(AgendaEvent.RegisterSuccess(UiText.Res(R.string.register_success)))
        } catch(e: Exceptions.EmailAlreadyExistsException) {
            sendEvent(AgendaEvent.EmailAlreadyExists)
        } catch(e: Exceptions.RegisterException) {
            sendEvent(
                AgendaEvent.RegisterError(
                    UiText.Res(
                        R.string.register_register_error,
                        e.message ?: ""
                    )
                )
            )
        } catch(e: Exceptions.InvalidUsernameException) {
            sendEvent(AgendaEvent.IsValidUsername(false))
        } catch(e: Exceptions.InvalidEmailException) {
            sendEvent(AgendaEvent.IsValidEmail(false))
        } catch(e: Exceptions.InvalidPasswordException) {
            sendEvent(AgendaEvent.IsValidPassword(false))
        } catch(e: Exceptions.RegisterNetworkException) {
            sendEvent(
                AgendaEvent.RegisterError(
                    UiText.Res(
                        R.string.register_network_error,
                        e.message ?: ""
                    )
                )
            )
        } catch (e: Exception) {
            sendEvent(AgendaEvent.UnknownError(UiText.Res(R.string.error_unknown, e.message ?: "")))
            e.printStackTrace()
        }
    }

    private fun validateUsername() {
        val isValid = authRepository.validateUsername(agendaState.value.username)
        sendEvent(AgendaEvent.IsValidUsername(isValid))
    }

    private fun validateEmail() {
        val isValid = authRepository.validateEmail(agendaState.value.email)
        sendEvent(AgendaEvent.IsValidEmail(isValid))
    }

    private fun validatePassword() {
        val isValid = authRepository.validatePassword(agendaState.value.password)
        sendEvent(AgendaEvent.IsValidPassword(isValid))
    }

    private fun validateConfirmPassword() {
        val isValid = authRepository.validatePassword(agendaState.value.confirmPassword)
        sendEvent(AgendaEvent.IsValidConfirmPassword(isValid))
    }

    private fun validatePasswordsMatch() {
        // Both passwords must have at least 1 character to validate match
        if(agendaState.value.password.isBlank()
            || agendaState.value.confirmPassword.isBlank()
        ) {
            sendEvent(AgendaEvent.IsPasswordsMatch(true))

            return
        }

        val isMatch = (agendaState.value.password == agendaState.value.confirmPassword)
        sendEvent(AgendaEvent.IsPasswordsMatch(isMatch))
    }

    fun sendEvent(event: AgendaEvent) {
        viewModelScope.launch {
            onEvent(event)
            yield() // allow events to percolate
        }
    }

    private suspend fun onEvent(event: AgendaEvent) {
//        when(event) {
//            is AgendaEvent.SetIsLoading -> {
//                _agendaState.update {
//                    it.copy(isLoading = event.isLoading)
//                }
//            }
//            is AgendaEvent.UpdateUsername -> {
//                _agendaState.update {
//                    it.copy(
//                        username = event.username,
//                        isInvalidUsername = false,
//                        isShowInvalidUsernameMessage = false
//                    )
//                }
//            }
//            is AgendaEvent.UpdateEmail -> {
//                _agendaState.update {
//                    it.copy(
//                        email = event.email,
//                        isInvalidEmail = false,
//                        isShowInvalidEmailMessage = false
//                    )
//                }
//            }
//            is AgendaEvent.UpdatePassword -> {
//                _agendaState.update {
//                    it.copy(
//                        password = event.password,
//                        isInvalidPassword = false,
//                        isShowInvalidPasswordMessage = false,
//                        isPasswordsMatch = false
//                    )
//                }
//            }
//            is AgendaEvent.UpdateConfirmPassword -> {
//                _agendaState.update {
//                    it.copy(
//                        confirmPassword = event.confirmPassword,
//                        isInvalidConfirmPassword = false,
//                        isShowInvalidConfirmPasswordMessage = false,
//                        isPasswordsMatch = false
//                    )
//                }
//            }
//            is AgendaEvent.SetIsPasswordVisible -> {
//                _agendaState.update {
//                    it.copy(isPasswordVisible = event.isVisible)
//                }
//            }
//            is AgendaEvent.ValidateUsername -> {
//                validateUsername()
//                yield()
//            }
//            is AgendaEvent.ValidateEmail -> {
//                validateEmail()
//                yield()
//            }
//            is AgendaEvent.ValidatePassword -> {
//                validatePassword()
//                yield()
//            }
//            is AgendaEvent.ValidateConfirmPassword -> {
//                validateConfirmPassword()
//                yield()
//            }
//            is AgendaEvent.ValidatePasswordsMatch -> {
//                validatePasswordsMatch()
//                yield()
//            }
//            is AgendaEvent.IsValidUsername -> {
//                _agendaState.update {
//                    it.copy(isInvalidUsername = !event.isValid)
//                }
//            }
//            is AgendaEvent.IsValidEmail -> {
//                _agendaState.update {
//                    it.copy(
//                        isInvalidEmail = !event.isValid
//                    )
//                }
//            }
//            is AgendaEvent.IsValidPassword -> {
//                _agendaState.update {
//                    it.copy(
//                        isInvalidPassword = !event.isValid
//                    )
//                }
//            }
//            is AgendaEvent.IsValidConfirmPassword -> {
//                _agendaState.update {
//                    it.copy(
//                        isInvalidConfirmPassword = !event.isValid
//                    )
//                }
//            }
//            is AgendaEvent.IsPasswordsMatch -> {
//                _agendaState.update {
//                    it.copy(
//                        isPasswordsMatch = event.isMatch
//                    )
//                }
//            }
//            is AgendaEvent.ShowInvalidUsernameMessage -> {
//                _agendaState.update {
//                    it.copy(
//                        isShowInvalidUsernameMessage = true
//                    )
//                }
//            }
//            is AgendaEvent.ShowInvalidEmailMessage -> {
//                _agendaState.update {
//                    it.copy(
//                        isShowInvalidEmailMessage = true
//                    )
//                }
//            }
//            is AgendaEvent.ShowInvalidPasswordMessage -> {
//                _agendaState.update {
//                    it.copy(
//                        isShowInvalidPasswordMessage = true
//                    )
//                }
//            }
//            is AgendaEvent.ShowInvalidConfirmPasswordMessage -> {
//                _agendaState.update {
//                    it.copy(
//                        isShowInvalidConfirmPasswordMessage = true
//                    )
//                }
//            }
//            is AgendaEvent.Register -> {
//                sendEvent(AgendaEvent.ValidateUsername)
//                sendEvent(AgendaEvent.ValidateEmail)
//                sendEvent(AgendaEvent.ValidatePassword)
//                sendEvent(AgendaEvent.ValidateConfirmPassword)
//                sendEvent(AgendaEvent.ValidatePasswordsMatch)
//                yield()
//
//                // Only show `Invalid Username` message only when "login" is clicked and the username is invalid.
//                if(_agendaState.value.isInvalidUsername)
//                    sendEvent(AgendaEvent.ShowInvalidUsernameMessage)
//
//                // Only show `Invalid Email` message only when "login" is clicked and the email is invalid.
//                if(_agendaState.value.isInvalidEmail)
//                    sendEvent(AgendaEvent.ShowInvalidEmailMessage)
//
//                // Only show `Invalid Password` message only when "login" is clicked and the password is invalid.
//                if(_agendaState.value.isInvalidPassword)
//                    sendEvent(AgendaEvent.ShowInvalidPasswordMessage)
//
//                // Only show `Invalid Confirm Password` message only when "login" is clicked and the confirm password is invalid.
//                if(_agendaState.value.isInvalidConfirmPassword)
//                    sendEvent(AgendaEvent.ShowInvalidConfirmPasswordMessage)
//
//                if( _agendaState.value.isInvalidUsername
//                    || _agendaState.value.isInvalidEmail
//                    || _agendaState.value.isInvalidPassword
//                    || _agendaState.value.isInvalidConfirmPassword
//                    || !agendaState.value.isPasswordsMatch
//                ) return
//
//                sendEvent(AgendaEvent.SetIsLoading(true))
//                register(event.username, event.email, event.password)
//            }
//            is AgendaEvent.EmailAlreadyExists -> {
//                _agendaState.update {
//                    it.copy(
//                        isLoggedIn = false,
//                        errorMessage = UiText.Res(R.string.register_error_email_exists),
//                        statusMessage = UiText.None
//                    )
//                }
//                sendEvent(AgendaEvent.SetIsLoading(false))
//            }
//            is AgendaEvent.RegisterSuccess -> {
//                _agendaState.update {
//                    it.copy(
//                        errorMessage = UiText.None,
//                        statusMessage = UiText.Res(R.string.register_success),
//                        isPasswordVisible = false
//                    )
//                }
//                sendEvent(AgendaEvent.SetIsLoading(false))
//            }
//            is AgendaEvent.RegisterError -> {
//                _agendaState.update {
//                    it.copy(
//                        isLoggedIn = false,
//                        errorMessage = event.message,
//                        statusMessage = UiText.None
//                    )
//                }
//                sendEvent(AgendaEvent.SetIsLoading(false))
//            }
//            is AgendaEvent.UnknownError -> {
//                _agendaState.update {
//                    it.copy(
//                        isLoggedIn = false,
//                        errorMessage = if(event.message.isRes)
//                            event.message
//                        else
//                            UiText.Res(R.string.error_unknown, "")
//                    )
//                }
//                sendEvent(AgendaEvent.SetIsLoading(false))
//            }
//
//        }
    }
}
