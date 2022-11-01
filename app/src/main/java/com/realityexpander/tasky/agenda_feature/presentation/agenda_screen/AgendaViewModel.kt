package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_authInfo
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_isLoaded
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_username
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Get params from savedStateHandle (from another screen or after process death)
    private val username: String =
        Uri.decode(savedStateHandle[SAVED_STATE_username]) ?: ""
    private val isLoaded: Boolean =
        savedStateHandle[SAVED_STATE_isLoaded] ?: false
//    private val errorMessage: UiText =
//        savedStateHandle[SAVED_STATE_errorMessage] ?: UiText.None
    private val authInfo: AuthInfo? =
        savedStateHandle[SAVED_STATE_authInfo]

    private val _agendaState = MutableStateFlow(AgendaState())
    val agendaState = _agendaState.onEach { state ->
        // save state for process death
//        savedStateHandle[SAVED_STATE_username] = state.username
        savedStateHandle[SAVED_STATE_isLoaded] = state.isLoaded
//        savedStateHandle[SAVED_STATE_errorMessage] = state.errorMessage
        savedStateHandle[SAVED_STATE_authInfo] = state.authInfo

        // Validate as the user types
        if(state.username.isNotBlank()) sendEvent(AgendaEvent.ValidateUsername)
        if(state.email.isNotBlank()) sendEvent(AgendaEvent.ValidateEmail)
        sendEvent(AgendaEvent.ValidatePasswordsMatch)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AgendaState())

    init {
        viewModelScope.launch {
            yield() // allow the agendaState to be initialized

            // restore state after process death
            _agendaState.value = AgendaState(
//                username = username,
                isLoaded = true,
//                errorMessage = errorMessage,
                authInfo = authRepository.getAuthInfo(),
            )
            yield() // allow the agendaState to be updated

            // Validate email & password when restored from process death or coming from another screen
            if (agendaState.value.username.isNotBlank()) sendEvent(AgendaEvent.ValidateUsername)
            if (agendaState.value.email.isNotBlank()) sendEvent(AgendaEvent.ValidateEmail)
            sendEvent(AgendaEvent.ValidatePasswordsMatch)

            yield() // allow the agendaState to be updated
            // Show status validation messages when restored from process death or coming from another screen
//            if(agendaState.value.isInvalidConfirmPassword) sendEvent(AgendaEvent.ShowInvalidConfirmPasswordMessage)
        }
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
