package com.realityexpander.tasky.auth_feature.presentation.splash_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.core.presentation.common.UIConstants.SAVED_STATE_authInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val authInfo: AuthInfo? =
        savedStateHandle[SAVED_STATE_authInfo]

    private val _splashState = MutableStateFlow(SplashState())
    val splashState = _splashState.onEach { state ->
        // save state for process death
        savedStateHandle[SAVED_STATE_authInfo] = state.authInfo
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SplashState())

    init {
        viewModelScope.launch {
            yield() // allow the splashState to be initialized

            // restore state after process death  // -- is this needed for splash screen?
            _splashState.update {
                it.copy(
                    authInfo = authInfo,
                    isLoading = true,
                )}
            yield() // allow the splashState to be restored
        }
    }

    fun onSetAuthInfo(authInfo: AuthInfo?) {
        viewModelScope.launch {

            // set the AuthInfo (& AuthToken) for this user
            authRepository.setAuthInfo(authInfo)

            if (authInfo != null
                && authRepository.authenticate()
            ) {
                // User is authenticated
                _splashState.update {
                    it.copy(
                        authInfo = authInfo,
                        isLoading = false,
                )}
            } else {
                // User is not authenticated
                _splashState.update {
                    it.copy(
                        authInfo = null,
                        isLoading = false,
                )}
            }
        }
    }
}

// to scale the splash screen if XML/SVG:
// put <path> inside a group as so:
// <group
//   android:scaleX="0.5"
//   android:scaleY="0.5"
//   android:pivotX="<half viewportWidth>"   // could also use half the width of the viewportWidth
//   android:pivotY="<half viewportHeight>"   // could also use half the height of the viewportHeight
//   >
//   <path ... /> // could be multiple `paths`
// </group>