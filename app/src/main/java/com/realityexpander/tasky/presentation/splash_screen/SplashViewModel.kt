package com.realityexpander.tasky.presentation.splash_screen

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.TaskyApplication
import com.realityexpander.tasky.common.settings.AppSettings
import com.realityexpander.tasky.common.settings.AppSettingsSerializer
import com.realityexpander.tasky.data.repository.remote.IAuthApi
import com.realityexpander.tasky.domain.AuthInfo
import com.realityexpander.tasky.domain.IAuthRepository
import com.realityexpander.tasky.presentation.common.UIConstants
import com.realityexpander.tasky.presentation.common.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val authInfo: AuthInfo? =
        savedStateHandle[UIConstants.SAVED_STATE_authInfo]
    private val statusMessage: UiText =
        savedStateHandle[UIConstants.SAVED_STATE_statusMessage] ?: UiText.None

    private val _splashState = MutableStateFlow(SplashState())
    val splashState = _splashState.onEach { state ->
        // save state for process death // is this needed for splash screen?
        savedStateHandle[UIConstants.SAVED_STATE_authInfo] = state.authInfo
        savedStateHandle[UIConstants.SAVED_STATE_statusMessage] = state.statusMessage

        println("SplashViewModel: splashState: $state")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SplashState())

    init {
        viewModelScope.launch {
            yield() // allow the splashState to be initialized

            // restore state after process death -- // is this needed for splash screen?
            _splashState.value = SplashState(
                authInfo = authInfo,
                authInfoChecked = false,
                statusMessage = statusMessage,
            )
            yield() // allow the splashState to be restored
        }
    }

    fun onSetAuthInfo(authInfo: AuthInfo) {
        viewModelScope.launch {

            // set the AuthInfo and AuthToken for this user
            TaskyApplication.authInfoGlobal = authInfo
            IAuthApi.setAuthToken(authInfo.authToken)

            if (authInfo.authToken != AuthInfo.NOT_LOGGED_IN.authToken
                && authRepository.authenticateAuthInfo(authInfo)
            ) {
                _splashState.value = SplashState(
                    authInfo = authInfo,
                    authInfoChecked = true,
                    statusMessage = UiText.None, //UiText.Res(R.string.splash_logged_in)
                )
            } else {
                _splashState.value = SplashState(
                    authInfo = AuthInfo.NOT_LOGGED_IN,
                    authInfoChecked = true,
                    statusMessage = UiText.None, //UiText.Res(R.string.splash_not_logged_in)
                )
            }
        }
    }
}