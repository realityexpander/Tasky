package com.realityexpander.tasky.agenda_feature.presentation.event_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.agenda_feature.domain.IAgendaRepository
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText
import com.realityexpander.tasky.agenda_feature.presentation.common.util.max
import com.realityexpander.tasky.agenda_feature.presentation.common.util.min
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent.*
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.auth_feature.domain.validation.ValidateEmail
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants
import com.realityexpander.tasky.core.presentation.common.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.time.Duration
import java.time.ZonedDateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val agendaRepository: IAgendaRepository,
    private val savedStateHandle: SavedStateHandle,
    private val validateEmail: ValidateEmail
) : ViewModel() {

    // Get params from savedStateHandle (from another screen or after process death)
    private val errorMessage: UiText? =
        savedStateHandle[SavedStateConstants.SAVED_STATE_errorMessage]
    private val isEditable: Boolean =
        savedStateHandle[SavedStateConstants.SAVED_STATE_isEditable] ?: false
    private val editMode: EventScreenEvent.EditMode? =
        savedStateHandle[SavedStateConstants.SAVED_STATE_editMode]
    private val addAttendeeDialogErrorMessage: UiText? =
        savedStateHandle[SavedStateConstants.SAVED_STATE_addAttendeeDialogErrorMessage]
    private val isAttendeeEmailValid: Boolean? =
        savedStateHandle[SavedStateConstants.SAVED_STATE_isAttendeeEmailValid]

    private val _state = MutableStateFlow(
        EventScreenState(
            errorMessage = errorMessage,
            isProgressVisible = true,
            isEditable = isEditable,
            editMode = editMode,
            addAttendeeDialogErrorMessage = addAttendeeDialogErrorMessage,
            isAttendeeEmailValid = isAttendeeEmailValid,
        )
    )
    val state = _state.onEach { state ->
        // save state for process death
        savedStateHandle[SavedStateConstants.SAVED_STATE_errorMessage] =
            state.errorMessage
        savedStateHandle[SavedStateConstants.SAVED_STATE_isEditable] =
            state.isEditable
        savedStateHandle[SavedStateConstants.SAVED_STATE_addAttendeeDialogErrorMessage] =
            state.addAttendeeDialogErrorMessage
        savedStateHandle[SavedStateConstants.SAVED_STATE_isAttendeeEmailValid] =
            state.isAttendeeEmailValid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EventScreenState())

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoaded = true, // only after state is initialized
                isProgressVisible = false,
                username = authRepository.getAuthInfo()?.username ?: "",
                authInfo = authRepository.getAuthInfo(),

                // Dummy event details for UI work // todo remove soon
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

    fun sendEvent(event: EventScreenEvent) {
        viewModelScope.launch {
            onEvent(event)
            yield() // allow events to percolate
        }
    }

    private suspend fun onEvent(uiEvent: EventScreenEvent) {

        when (uiEvent) {
            is ShowProgressIndicator -> {
                _state.update { _state ->
                    _state.copy(isProgressVisible = uiEvent.isShowing)
                }
            }
            is SetIsLoaded -> {
                _state.update { _state ->
                    _state.copy(isProgressVisible = uiEvent.isLoaded)
                }
            }
            is SetIsEditable -> {
                _state.update { _state ->
                    _state.copy(isEditable = uiEvent.isEditable)
                }
            }
            is SetEditMode -> {
                _state.update { _state ->
                    _state.copy(editMode = uiEvent.editMode)
                }
            }
            is CancelEditMode -> {
                _state.update { _state ->
                    _state.copy(
                        editMode = null,
                    )
                }
                sendEvent(ClearErrorsForAddAttendeeDialog)
            }
            is ValidateAttendeeEmail -> {
                _state.update { _state ->
                    _state.copy(
                        isAttendeeEmailValid =
                            if (uiEvent.email.isBlank())
                                    null
                                else
                                    validateEmail.validate(uiEvent.email)
                    )
                }
            }
            is ValidateAttendeeEmailExistsThenAddAttendee -> {
                sendEvent(ShowProgressIndicator(true))

                // call API to check if attendee email exists
                when (val result =
                    agendaRepository.validateAttendeeExists(uiEvent.email.trim().lowercase())
                ) {
                    is ResultUiText.Success -> {
                        val attendeeInfo = result.data
                        sendEvent(ShowProgressIndicator(false))

                        // Attempt Add Attendee to Event
                        attendeeInfo?.let { attendee ->

                            // Check if attendee is already in the list
                            val attendeeAlreadyInList =
                                _state.value.event?.attendees?.any { attendee.id == it.id }
                            if (attendeeAlreadyInList == true) {
                                sendEvent(SetErrorMessageForAddAttendeeDialog(UiText.Res(R.string.add_attendee_dialog_error_email_already_added)))
                                return
                            }

                            // Add attendee to event
                            sendEvent(EditMode.AddAttendee(attendee))
                            sendEvent(CancelEditMode)
                        } ?: run {
                            sendEvent(SetErrorMessageForAddAttendeeDialog(UiText.Res(R.string.add_attendee_dialog_error_email_not_found)))
                        }
                    }
                    is ResultUiText.Error -> {
                        sendEvent(ShowProgressIndicator(false))
                        sendEvent(SetErrorMessageForAddAttendeeDialog(result.message))
                    }
                }
            }
            is SetErrorMessageForAddAttendeeDialog -> {
                _state.update { _state ->
                    _state.copy(addAttendeeDialogErrorMessage = uiEvent.message)
                }
            }
            is ClearErrorsForAddAttendeeDialog -> {
                _state.update { _state ->
                    _state.copy(
                        addAttendeeDialogErrorMessage = null,
                        isAttendeeEmailValid = null
                    )
                }
            }
            is EditMode.UpdateText -> {
                when (_state.value.editMode) {

                    is EditMode.ChooseTitleText -> {
                        _state.update { _state ->
                            _state.copy(
                                event = _state.event?.copy(title = uiEvent.text),
                                editMode = null
                            )
                        }
                    }
                    is EditMode.ChooseDescriptionText -> {
                        _state.update { _state ->
                            _state.copy(
                                event = _state.event?.copy(description = uiEvent.text),
                                editMode = null
                            )
                        }
                    }
                    else -> throw java.lang.IllegalStateException("Invalid type for SaveText: ${_state.value.editMode}")
                }
            }
            is EditMode.UpdateDateTime -> {
                when (_state.value.editMode) {

                    is EditMode.ChooseFromTime,
                    is EditMode.ChooseFromDate -> {
                        _state.update { _state ->
                            val remindAtDuration =
                                Duration.between(_state.event?.remindAt, _state.event?.from)

                            _state.copy(
                                event = _state.event?.copy(
                                    from = uiEvent.dateTime,

                                    // Ensure that `to > from`
                                    to = max(_state.event.to, uiEvent.dateTime),

                                    // Update the `RemindAt dateTime` to keep same offset from the `From` date
                                    remindAt = uiEvent.dateTime.minus(remindAtDuration)
                                ),
                                editMode = null
                            )
                        }
                    }
                    is EditMode.ChooseToTime,
                    is EditMode.ChooseToDate -> {
                        _state.update { _state ->

                            // Ensure that `from < to`
                            val minFrom =
                                min(_state.event?.from ?: ZonedDateTime.now(), uiEvent.dateTime)

                            val remindAtDuration =
                                Duration.between(_state.event?.remindAt, _state.event?.from)

                            _state.copy(
                                event = _state.event?.copy(
                                    to = uiEvent.dateTime,
                                    from = minFrom,

                                    // Update the `RemindAt dateTime` to keep same offset from the `From` date
                                    remindAt = minFrom.minus(remindAtDuration)
                                ),
                                editMode = null
                            )
                        }
                    }
                    is EditMode.ChooseRemindAtDateTime -> {
                        _state.update { _state ->

                            // Ensure that `remindAt <= from`
                            if (uiEvent.dateTime.isAfter(_state.event?.from)) {
                                // make 'from' and 'remindAt' the same
                                return@update _state.copy(
                                    event = _state.event?.copy(
                                        remindAt = _state.event.from
                                    ),
                                    editMode = null
                                )
                            }

                            _state.copy(
                                event = _state.event?.copy(
                                    remindAt = uiEvent.dateTime
                                ),
                                editMode = null
                            )
                        }
                    }
                    else -> throw java.lang.IllegalStateException("Invalid type for SaveDateTime: ${_state.value.editMode}")
                }
            }
            is EditMode.AddPhoto -> {
                _state.update { _state ->
                    _state.copy(
                        event = _state.event?.copy(
                            photosToUpload = _state.event.photosToUpload + uiEvent.photoLocal
                        ),
                        editMode = null
                    )
                }
            }
            is EditMode.AddAttendee -> {
                _state.update { _state ->
                    _state.copy(
                        event = _state.event?.copy(
                            attendees = _state.event.attendees +
                                    uiEvent.attendee.copy(isGoing = true)
                        )
                    )
                }
            }
            is EditMode.RemoveAttendee -> {
                _state.update { _state ->
                    _state.copy(
                        event = _state.event?.copy(
                            attendees = _state.event.attendees.filter { it.id != uiEvent.attendeeId }
                        ),
                    )
                }
                sendEvent(CancelEditMode)
            }

            is Error -> {
                _state.update { _state ->
                    _state.copy(
                        errorMessage = if (uiEvent.message.isRes)
                            uiEvent.message
                        else
                            UiText.Res(R.string.error_unknown, "")
                    )
                }
                sendEvent(ShowProgressIndicator(false))
            }
            else -> {}
        }
    }
}