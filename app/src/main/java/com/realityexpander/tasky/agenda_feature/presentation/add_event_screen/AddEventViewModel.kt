package com.realityexpander.tasky.agenda_feature.presentation.add_event_screen

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.domain.IAgendaRepository
import com.realityexpander.tasky.agenda_feature.presentation.agenda_screen.AgendaEvent
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants
import com.realityexpander.tasky.core.presentation.common.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class AddEventViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val agendaRepository: IAgendaRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Get params from savedStateHandle (from another screen or after process death)
    private val errorMessage: UiText? =
        savedStateHandle[SavedStateConstants.SAVED_STATE_errorMessage]

    private val _addEventState = MutableStateFlow(AddEventState(
//        username = authRepository.getAuthInfo()?.username ?: "", // todo get this from the previous screen? // put back in
        isLoaded = true, // only after default state is initialized
        username = "Chris Athanas", // todo get this from the previous screen?
        errorMessage = errorMessage,
//        authInfo = authRepository.getAuthInfo(), // todo put this back in
        authInfo = AuthInfo("0001", "0001", "Chris Athanas"),
        isProgressVisible = false,

        // Dummy event details for UI work // todo use the AgendaItem.Event DS?
        title = "Test Event Description This is a long description of the event",
        description = LoremIpsum(20).values.joinToString(),
        fromDateTime = ZonedDateTime.now(),
        toDateTime = ZonedDateTime.now().plus(1, ChronoUnit.HOURS),
        remindAt = ZonedDateTime.now().minus(30, ChronoUnit.MINUTES),
        isEventCreator = true,
        isGoing = true,
    ))
    val addEventState = _addEventState.onEach { state ->
        // save state for process death
        savedStateHandle[SavedStateConstants.SAVED_STATE_errorMessage] = state.errorMessage
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddEventState())


    fun sendEvent(event: AgendaEvent) {
        viewModelScope.launch {
            onEvent(event)
            yield() // allow events to percolate
        }
    }

    private suspend fun onEvent(event: AgendaEvent) {

        when(event) {
            is AgendaEvent.ShowProgressIndicator -> {
                _addEventState.update {
                    it.copy(isProgressVisible = event.isShowing)
                }
            }
            is AgendaEvent.SetIsLoaded -> {
                _addEventState.update {
                    it.copy(isProgressVisible = event.isLoaded)
                }
            }
            is AgendaEvent.Error -> {
                _addEventState.update {
                    it.copy(
                        errorMessage = if (event.message.isRes)
                            event.message
                        else
                            UiText.Res(R.string.error_unknown, "")
                    )
                }
                sendEvent(AgendaEvent.ShowProgressIndicator(false))
            }
            else -> {}
        }
    }
}