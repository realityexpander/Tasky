@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.realityexpander.tasky.auth_feature.presentation.splash_screen

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.agenda_feature.domain.IRemindAtNotificationManager
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.core.domain.IAppSettingsRepository
import com.realityexpander.tasky.core.presentation.notifications.RemindAtNotificationManagerImpl.Companion.ALARM_NOTIFICATION_INTENT_ACTION_ALARM_TRIGGER
import com.realityexpander.tasky.core.util.Exceptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val remindAtNotificationManager : IRemindAtNotificationManager,
    val appSettingsRepository: IAppSettingsRepository
) : ViewModel() {

    private val _splashState = MutableStateFlow(SplashState())
    val splashState = _splashState.asStateFlow()

    fun onIntentReceived(intent: Intent?) {
        intent ?: return

        // Guard if the intent is not an "Alarm Trigger" intent
        if (intent.action != ALARM_NOTIFICATION_INTENT_ACTION_ALARM_TRIGGER) {
            return
        }

        // App only handles "Alarm Trigger" intents
        remindAtNotificationManager.showNotification(intent)
    }

    fun onSetAuthInfo(authInfo: AuthInfo?) {
        viewModelScope.launch {

            // set the AuthInfo (& AuthToken) for this user from the AuthRepository
            authRepository.setAuthInfo(authInfo)

            // Validate the AuthToken
            val authenticateSuccess = try {
                authRepository.authenticate()
                true
            } catch (e: Exceptions.NetworkException) {
                if(e.localizedMessage == "401 Unauthorized") {
                    false
                } else {
                    authInfo?.accessToken != null
                }
            } catch (e: Exceptions.UnknownErrorException) {
                authInfo?.accessToken != null
            } catch (e: Exception) {
                _splashState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                false
            }

            if(authenticateSuccess) {
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

// NOTE: LEAVE FOR REFERENCE
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
