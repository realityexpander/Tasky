package com.realityexpander.tasky.presentation.login_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.realityexpander.tasky.common.Exceptions
import com.realityexpander.tasky.data.repository.AuthRepositoryImpl
import com.realityexpander.tasky.data.repository.local.AuthApiImpl
import com.realityexpander.tasky.data.repository.remote.AuthDaoImpl
import com.realityexpander.tasky.data.validation.ValidateEmailImpl
import com.realityexpander.tasky.domain.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: IAuthRepository = AuthRepositoryImpl(
        authApi = AuthApiImpl(),
        authDao = AuthDaoImpl(),
        validateEmail = ValidateEmailImpl()
    ),
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Kotlin Coroutines StateFlow - updates NOT sent when app is in background (HOT)
    private var _loginStateFlow = MutableStateFlow<State>(State())
    var loginStateFlow: StateFlow<State> = _loginStateFlow.asStateFlow()

    // Channel - updates ARE sent when app is in background
    val loginChannel = Channel<State>()


    suspend fun login(email: String, password: String) {
        try {
            authRepository.login(email, password)
        } catch(e: Exceptions.LoginException) {
            // _event.value = Event.LoginError
        } catch(e: Exceptions.InvalidEmailException) {
            // _event.value = Event.InvalidEmail
        } catch (e: Exception) {
            // handle general error
            //_event.value = Event.UnknownError(e.message)
            e.printStackTrace()
        }
    }

}
