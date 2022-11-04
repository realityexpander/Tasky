package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.R
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_authInfo
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_errorMessage
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_isLoaded
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_username
import com.realityexpander.tasky.core.presentation.common.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
    private val errorMessage: UiText =
        savedStateHandle[SAVED_STATE_errorMessage] ?: UiText.None
    private val authInfo: AuthInfo? =
        savedStateHandle[SAVED_STATE_authInfo]

    private val _agendaState = MutableStateFlow(AgendaState())
    val agendaState = _agendaState.onEach { state ->
        // save state for process death
        savedStateHandle[SAVED_STATE_username] = state.username
        savedStateHandle[SAVED_STATE_isLoaded] = state.isLoaded
        savedStateHandle[SAVED_STATE_errorMessage] = state.errorMessage
        savedStateHandle[SAVED_STATE_authInfo] = state.authInfo

        // Validate as the user types
        if(state.username.isNotBlank()) sendEvent(AgendaEvent.ValidateUsername)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AgendaState())

    init {
        viewModelScope.launch {
            yield() // allow the agendaState to be initialized

            // restore state after process death
            _agendaState.value = AgendaState(
                username = username,
                isLoaded = true,
                errorMessage = errorMessage,
                authInfo = authRepository.getAuthInfo(),
            )
            yield() // allow the agendaState to be updated

            // Validate email & password when restored from process death or coming from another screen
            if (agendaState.value.username.isNotBlank()) sendEvent(AgendaEvent.ValidateUsername)

            yield() // allow the agendaState to be updated
            // Show status validation messages when restored from process death or coming from another screen
//            if(agendaState.value.isInvalidConfirmPassword) sendEvent(AgendaEvent.ShowInvalidConfirmPasswordMessage)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.clearAuthInfo()
            yield()
        }
    }

    fun sendEvent(event: AgendaEvent) {
        viewModelScope.launch {
            onEvent(event)
            yield() // allow events to percolate
        }
    }

    private suspend fun onEvent(event: AgendaEvent) {
        when(event) {
            is AgendaEvent.ShowProgressIndicator -> {
                _agendaState.update {
                    it.copy(isLoading = event.isShowing)
                }
            }
            is AgendaEvent.SetIsLoaded -> {
                _agendaState.update {
                    it.copy(isLoading = event.isLoaded)
                }
            }
//            is AgendaEvent.UpdateUsername -> {
//                _agendaState.update {
//                    it.copy(
//                        username = event.username,
//                        isInvalidUsername = false,
//                        isShowInvalidUsernameMessage = false
//                    )
//                }
//            }
//            is AgendaEvent.SetIsPasswordVisible -> {
//                _agendaState.update {
//                    it.copy(isPasswordVisible = event.isVisible)
//                }
//            }
//            is AgendaEvent.ValidateUsername -> {
//                val isValid = validateUsername()
//                _agendaState.update {
//                    it.copy(isInvalidUsername = !isValid)
//                }
//                yield()
//            }
//            is AgendaEvent.ShowInvalidUsernameMessage -> {
//                _agendaState.update {
//                    it.copy(
//                        isShowInvalidUsernameMessage = true
//                    )
//                }
//            }
            is AgendaEvent.ShowAgendaItemDropdown -> {
                _agendaState.update {
                    it.copy(
//                        isAgendaItemMenuDropdownShowing = event.agendaItemId != null,
                        agendaItemIdForMenu = event.agendaItemId,
                    )
                }
                yield()
            }

            is AgendaEvent.ToggleLogoutDropdown -> {
                _agendaState.update {
                    it.copy(
                        isLogoutDropdownShowing = !agendaState.value.isLogoutDropdownShowing
                    )
                }
            }
            is AgendaEvent.Logout -> {
                _agendaState.update {
                    it.copy(authInfo = null)
                }
                logout()
            }
//            is AgendaEvent.LogoutSuccess -> {
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
            is AgendaEvent.UnknownError -> {
                _agendaState.update {
                    it.copy(
                        errorMessage = if(event.message.isRes)
                            event.message
                        else
                            UiText.Res(R.string.error_unknown, "")
                    )
                }
                sendEvent(AgendaEvent.ShowProgressIndicator(false))
            }

        }
    }
}
