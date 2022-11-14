package com.realityexpander.tasky.agenda_feature.presentation.add_event_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.agenda_feature.domain.IAgendaRepository
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants
import com.realityexpander.tasky.core.presentation.common.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.time.ZonedDateTime
import java.util.*
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
    private val isEditMode: Boolean =
        savedStateHandle[SavedStateConstants.SAVED_STATE_isEditMode] ?: false

    private val _addEventState = MutableStateFlow(AddEventState(
        errorMessage = errorMessage,
        isProgressVisible = true,
        isEditMode = isEditMode
    ))
    val addEventState = _addEventState.onEach { state ->
        // save state for process death
        savedStateHandle[SavedStateConstants.SAVED_STATE_errorMessage] = state.errorMessage
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddEventState())

    init {
        viewModelScope.launch {
            _addEventState.value = _addEventState.value.copy(
                isLoaded = true, // only after state is initialized
                isProgressVisible = false,
                username = authRepository.getAuthInfo()?.username ?: "", // todo get this from the previous screen? // put back in
                authInfo = authRepository.getAuthInfo(), // todo put this back in
                // Dummy event details for UI work // todo use the AgendaItem.Event DS?
                event = AgendaItem.Event(
                    id = "0001",
                    title = "Title of Event",
                    description = "Description of Event",
                    isUserEventCreator = false,
                    from = ZonedDateTime.now().plusHours(1),
                    to = ZonedDateTime.now().plusHours(2),
                    remindAt = ZonedDateTime.now().plusMinutes(30),
                    isGoing = true,
                    attendees = listOf(
                        Attendee(
                            eventId = "0001",
                            isGoing = true,
                            fullName = authRepository.getAuthInfo()?.username!!,
                            email = "cameron@demo.com",
                            remindAt = ZonedDateTime.now(),
                            id = authRepository.getAuthInfo()?.userId!!,
                            photo = "https://randomuser.me/api/portraits/men/75.jpg"
                        ),
                        Attendee(
                            eventId = "0001",
                            isGoing = true,
                            fullName = "Jeremy Johnson",
                            remindAt = ZonedDateTime.now(),
                            email = "jj@demo.com",
                            id = UUID.randomUUID().toString(),
                            photo = "https://randomuser.me/api/portraits/men/75.jpg"
                        ),
                        Attendee(
                            eventId = "0001",
                            isGoing = true,
                            fullName = "Fred Flintstone",
                            remindAt = ZonedDateTime.now(),
                            email = "ff@demo.com",
                            id = UUID.randomUUID().toString(),
                            photo = "https://randomuser.me/api/portraits/men/71.jpg"
                        ),
                        Attendee(
                            eventId = "0001",
                            isGoing = true,
                            fullName = "Sam Bankman",
                            remindAt = ZonedDateTime.now(),
                            email = "sb@demo.com",
                            id = UUID.randomUUID().toString(),
                            photo = "https://randomuser.me/api/portraits/men/70.jpg"
                        ),
                        Attendee(
                            eventId = "0001",
                            isGoing = false,
                            fullName = "Billy Johnson",
                            remindAt = ZonedDateTime.now(),
                            email = "bj@demo.com",
                            id = UUID.randomUUID().toString(),
                            photo = "https://randomuser.me/api/portraits/men/73.jpg"
                        ),
                        Attendee(
                            eventId = "0001",
                            isGoing = false,
                            fullName = "Edward Flintstone",
                            remindAt = ZonedDateTime.now(),
                            email = "FE@demo.com",
                            id = UUID.randomUUID().toString(),
                            photo = "https://randomuser.me/api/portraits/men/21.jpg"
                        ),
                        Attendee(
                            eventId = "0001",
                            isGoing = false,
                            fullName = "Jill Bankman",
                            remindAt = ZonedDateTime.now(),
                            email = "jb@demo.com",
                            id = UUID.randomUUID().toString(),
                            photo = "https://randomuser.me/api/portraits/men/30.jpg"
                        ),
                    ),
                ),
            )
        }
    }

    fun sendEvent(event: AddEventEvent) {
        viewModelScope.launch {
            onEvent(event)
            yield() // allow events to percolate
        }
    }

    private suspend fun onEvent(event: AddEventEvent) {

        when(event) {
            is AddEventEvent.ShowProgressIndicator -> {
                _addEventState.update {
                    it.copy(isProgressVisible = event.isShowing)
                }
            }
            is AddEventEvent.SetIsLoaded -> {
                _addEventState.update {
                    it.copy(isProgressVisible = event.isLoaded)
                }
            }
            is AddEventEvent.SetIsEditMode -> {
                _addEventState.update {
                    it.copy(isEditMode = event.isEditMode)
                }
            }
            is AddEventEvent.Error -> {
                _addEventState.update {
                    it.copy(
                        errorMessage = if (event.message.isRes)
                            event.message
                        else
                            UiText.Res(R.string.error_unknown, "")
                    )
                }
                sendEvent(AddEventEvent.ShowProgressIndicator(false))
            }
            else -> {}
        }
    }
}