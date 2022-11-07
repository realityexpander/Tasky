package com.realityexpander.tasky.auth_feature.presentation.splash_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _splashState = MutableStateFlow(SplashState())
    val splashState = _splashState.asStateFlow()

    fun onSetAuthInfo(authInfo: AuthInfo?) {
        viewModelScope.launch {

            // set the AuthInfo (& AuthToken) for this user
            authRepository.setAuthInfo(authInfo)

            // Validate the AuthToken
            val authenticateSuccess = try {
                authRepository.authenticate()
                true
            } catch (e: Exception) {
                _splashState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                false
            }

            // If the AuthToken is valid, proceed to the Agenda screen
            // Otherwise, proceed to the Login screen.
            if( authInfo != null
                && authenticateSuccess
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