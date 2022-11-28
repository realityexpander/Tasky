package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.agenda_feature.domain.IAgendaRepository
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText
import com.realityexpander.tasky.agenda_feature.presentation.agenda_screen.AgendaScreenEvent.*
import com.realityexpander.tasky.agenda_feature.presentation.common.enums.AgendaItemType
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_currentDate
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_errorMessage
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_selectedDayIndex
import com.realityexpander.tasky.core.presentation.common.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val agendaRepository: IAgendaRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Get params from savedStateHandle (from another screen or after process death)
    private val errorMessage: UiText? =
        savedStateHandle[SAVED_STATE_errorMessage]
    private val selectedDayIndex: Int? =
        savedStateHandle[SAVED_STATE_selectedDayIndex]
    private val selectedDate: ZonedDateTime? =
        savedStateHandle[SAVED_STATE_currentDate] ?: ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)

    private val _currentDate = MutableStateFlow(selectedDate)
    private val _selectedDayIndex = MutableStateFlow(selectedDayIndex)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class) // for .flatMapLatest, .flattenMerge
    private val _agendaItems =
        _selectedDayIndex.combine(_currentDate) { dayIndex, date ->
            agendaRepository.getAgendaForDayFlow(
                getDateForSelectedDayIndex(date, dayIndex)
            )
        }
//        .flattenMerge()  // was not working for some reason...
        .flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _agendaState =
        MutableStateFlow(
            AgendaState(
                // restore state from savedStateHandle after process death
                errorMessage = errorMessage,
                selectedDayIndex = selectedDayIndex,
            ))

    val agendaState = combine(
        _agendaState,
        _currentDate,
        _selectedDayIndex,
        _agendaItems,
    ) { state,
        currentDate,
        selectedDayIndex,
        items  ->

        savedStateHandle[SAVED_STATE_errorMessage] = state.errorMessage
        savedStateHandle[SAVED_STATE_selectedDayIndex] = selectedDayIndex
        savedStateHandle[SAVED_STATE_currentDate] = currentDate

        state.copy(
            agendaItems = items,
            selectedDayIndex = selectedDayIndex,
            currentDate = currentDate ?: ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AgendaState())

    private val _oneTimeEvent = MutableSharedFlow<OneTimeEvent>()
    val oneTimeEvent = _oneTimeEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            yield() // wait for other init code to run

            // restore state after process death
            _agendaState.update {
                it.copy(
                    isLoaded = true, // only after init occurs
                    username = authRepository.getAuthInfo()?.username ?: "",
                    authInfo = authRepository.getAuthInfo(),
                    agendaItems = _agendaItems.value,
                )
            }

//            yield() // wait for database to load  // leave for testing for now // todo remove
//            if(agendaState.value.agendaItems.isEmpty()) { // if no items for today, make some fake ones
//                createFakeAgendaItems(agendaRepository)
//            }
        }
    }

    fun sendEvent(event: AgendaScreenEvent) {
        viewModelScope.launch {
            onEvent(event)
            yield() // allow events to percolate
        }
    }

    private fun getDateForSelectedDayIndex(
        startDate: ZonedDateTime?,
        selectedDayIndex: Int?
    ): ZonedDateTime {
        return (startDate ?: ZonedDateTime.now(ZoneId.systemDefault()))
                .plusDays(selectedDayIndex?.toLong() ?: 0)
    }

    private suspend fun onEvent(uiEvent: AgendaScreenEvent) {

        when(uiEvent) {
            is ShowProgressIndicator -> {
                _agendaState.update {
                    it.copy(isProgressVisible = uiEvent.isVisible)
                }
            }
            is SetIsLoaded -> {
                _agendaState.update {
                    it.copy(isLoaded = uiEvent.isLoaded)
                }
            }
            is ShowChooseCurrentDateDialog -> {
                _agendaState.update {
                    it.copy(
                        chooseCurrentDateDialog = uiEvent.currentDate,
                    )
                }
            }
            is CancelChooseCurrentDateDialog -> {
                _agendaState.update {
                    it.copy(
                        chooseCurrentDateDialog = null,
                    )
                }
            }
            is SetCurrentDate -> {
                _currentDate.value = uiEvent.date
                sendEvent(CancelChooseCurrentDateDialog)
                sendEvent(SetSelectedDayIndex(0))
            }
            is SetSelectedDayIndex -> {
                _selectedDayIndex.value = uiEvent.dayIndex
            }
            is CreateAgendaItem -> {
                when(uiEvent.agendaItemType) {
                    AgendaItemType.Event -> {
                        _oneTimeEvent.emit(OneTimeEvent.NavigateToCreateEvent)
                    }
                    AgendaItemType.Task -> {
//                        _oneTimeEvent.emit(OneTimeEvent.NavigateToCreateTask)
                    }
                    AgendaItemType.Reminder -> {
//                        _oneTimeEvent.emit(OneTimeEvent.NavigateToCreateReminder)
                    }
                }
            }
            is ToggleTaskCompleted -> {
                //agendaRepository.updateTask(uiEvent.agendaItem.copy(isCompleted = !uiEvent.agendaItem.isCompleted)) // todo implement update task - completed state
            }
            is Logout -> {
                viewModelScope.launch {
                    agendaRepository.clearAllEvents()
                    authRepository.logout()

                    _agendaState.update {
                        it.copy(authInfo = null)
                    }
                }
            }
            is StatefulOneTimeEvent -> {
                when (uiEvent) {

                    // Because you can only scroll to one location at a time, we only
                    // need one reset event to reset all the scrolling events.
                    is StatefulOneTimeEvent.ResetScrollTo -> {
                        _agendaState.update {
                            it.copy(
                                scrollToItemId = null,
                                scrollToTop = false,
                                scrollToBottom = false
                            )
                        }
                    }
                    is StatefulOneTimeEvent.ScrollToTop -> {
                        // • Send the one time event
                        _agendaState.update {
                            it.copy(scrollToTop = true)
                        }
                    }
                    is StatefulOneTimeEvent.ScrollToBottom -> {
                        // • Send the one time event
                        _agendaState.update {
                            it.copy(scrollToBottom = true)
                        }
                    }
                    is StatefulOneTimeEvent.ScrollToItemId -> {
                        // • Send the one time event
                        _agendaState.update {
                            it.copy(scrollToItemId = uiEvent.agendaItemId)
                        }
                    }
                }
            }
            is SetErrorMessage -> {
                _agendaState.update {
                    it.copy(
                        errorMessage = if(uiEvent.message.isRes)
                            uiEvent.message
                        else
                            UiText.Res(R.string.error_unknown, ""),
                        isProgressVisible = false
                    )
                }
            }
            is ClearErrorMessage -> {
                _agendaState.update {
                    it.copy(
                        errorMessage = null,
                        isProgressVisible = false
                    )
                }
            }
            is OneTimeEvent.NavigateToCreateEvent -> {
                _oneTimeEvent.emit(OneTimeEvent.NavigateToCreateEvent)
            }
            is OneTimeEvent.NavigateToOpenEvent -> {
                _oneTimeEvent.emit(OneTimeEvent.NavigateToOpenEvent(uiEvent.eventId))
            }
            is OneTimeEvent.NavigateToEditEvent -> {
                _oneTimeEvent.emit(OneTimeEvent.NavigateToEditEvent(uiEvent.eventId))
            }
            is ShowConfirmDeleteAgendaItemDialog -> {
                _agendaState.update {
                    it.copy(
                        confirmDeleteAgendaItem = uiEvent.agendaItem
                    )
                }
                sendEvent(ClearErrorMessage)
            }
            is DismissConfirmDeleteAgendaItemDialog -> {
                _agendaState.update {
                    it.copy(
                        confirmDeleteAgendaItem = null
                    )
                }
            }
            is DeleteAgendaItem -> {
               val result =
                   when (uiEvent.agendaItem) {
                        is AgendaItem.Event -> {
                            // If the logged-in user owns the Event, they are allowed to delete it.
                            if (uiEvent.agendaItem.isUserEventCreator) {
                                agendaRepository.deleteEventId(uiEvent.agendaItem.id)
                            } else {
                                // Otherwise, the user is removed from the Event.
                                agendaRepository.removeLoggedInUserFromEventId(
                                    eventId = uiEvent.agendaItem.id,
                                )
                            }
                        }
                        is AgendaItem.Task -> {
//                                agendaRepository.deleteTaskId(agendaItem)  // todo implement
                            ResultUiText.Error<AgendaItem.Task>(UiText.Str("unimplemented"))
                        }
                        is AgendaItem.Reminder -> {
//                                agendaRepository.deleteReminderId(agendaItem) // todo implement
                            ResultUiText.Error<AgendaItem.Task>(UiText.Str("unimplemented"))
                        }
                        else -> {
                            ResultUiText.Error<AgendaItem>(UiText.Res(R.string.error_unknown_agenda_type))
                        }
                    }

                if(result is ResultUiText.Error) {
                    sendEvent(SetErrorMessage(result.message))
                } else {
                    _oneTimeEvent.emit(OneTimeEvent.ShowToast(UiText.Res(R.string.event_message_event_deleted_success)))
                    sendEvent(ClearErrorMessage)
                }
                sendEvent(DismissConfirmDeleteAgendaItemDialog)
            }
        }
    }

    // Create Dummy data to pre-populate the Agenda // todo remove later - left for reference
    private fun createFakeAgendaItems(agendaRepository: IAgendaRepository) {
        val today = ZonedDateTime.now()
        val zoneId = ZoneId.systemDefault()
        val todayDayOfWeek = today.dayOfWeek.value
        val todayDayOfMonth = today.dayOfMonth
        val todayMonth = today.month
        val todayYear = today.year
        val todayDate = LocalDate.of(todayYear, todayMonth, todayDayOfMonth)

        viewModelScope.launch {
            agendaRepository.createEvent(
                AgendaItem.Event(
                    id = UUID.randomUUID().toString(),
                    title = "Meeting with John",
                    description = "Discuss the new project",
                    from = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(1), zoneId),
                    to = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(2), zoneId),
                    remindAt = ZonedDateTime.of(
                        todayDate,
                        today.toLocalTime().minusMinutes(30),
                        zoneId
                    ),
                    host = "635dc7880806b27dc8ab81ae",
                    isUserEventCreator = false,
                    isGoing = true,
                    attendees = listOf(
                        Attendee(
                            id = authRepository.getAuthInfo()?.userId!!,
                            fullName = authRepository.getAuthInfo()?.username ?: "",
                            email = authRepository.getAuthInfo()?.email ?: "",
                            isGoing = true,
                        )
                    ),
                    photos = emptyList(),
                )
            )
            agendaRepository.createEvent(
                AgendaItem.Event(
                    id = UUID.randomUUID().toString(),
                    title = "Meeting with Jack",
                    from = ZonedDateTime.of(
                        todayDate,
                        today.toLocalTime().plusHours(3),
                        zoneId
                    ),
                    to = ZonedDateTime.of(
                        todayDate,
                        today.toLocalTime().plusHours(4),
                        zoneId
                    ),
                    remindAt = ZonedDateTime.of(
                        todayDate,
                        today.toLocalTime().plusHours(2),
                        zoneId
                    ),
                    description = "Discuss the yet another project",
                    host = "635dc7880806b27dc8ab81ae",
                    isUserEventCreator = true,
                    isGoing = true,
                    attendees = listOf(
                        Attendee(
                            id = authRepository.getAuthInfo()?.userId!!,
                            fullName = authRepository.getAuthInfo()?.username ?: "",
                            email = authRepository.getAuthInfo()?.email ?: "",
                            isGoing = true,
                        )
                    ),
                    photos = emptyList(),
                )
            )
            agendaRepository.createEvent(
                AgendaItem.Event(
                    id = UUID.randomUUID().toString(),
                    title = "Meeting with Jeremy",
                    from = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(4), zoneId),
                    to = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(5), zoneId),
                    remindAt = ZonedDateTime.of(
                        todayDate,
                        today.toLocalTime().plusHours(3),
                        zoneId
                    ),
                    description = "Discuss the worse project",
                    host = "635dc7880806b27dc8ab81ae",
                    isUserEventCreator = true,
                    isGoing = true,
                    attendees = listOf(
                        Attendee(
                            id = authRepository.getAuthInfo()?.userId!!,
                            fullName = authRepository.getAuthInfo()?.username ?: "",
                            email = authRepository.getAuthInfo()?.email ?: "",
                            isGoing = true,
                        )
                    ),
                    photos = emptyList(),
                )
            )
//                    AgendaItem.Reminder(  // todo add reminder
//                        id = "0006",
//                        title = "Reminder with Jill",
//                        time = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(7), zoneId),
//                        remindAt = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(6), zoneId),
//                        description = "Make the similar project"
//                    ),
//                    AgendaItem.Task(  // todo add task
//                        id = "0008",
//                        title = "Chore with Jason",
//                        time = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(10), zoneId),
//                        remindAt = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(9), zoneId),
//                        description = "Kill the better project",
//                        isDone = true
//                    ),
//                )
//                    AgendaItem.Task(  // todo add task
//                        id = "0002",
//                        title = "Task with Jim",
//                        time = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(2), zoneId),
//                        remindAt = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(1), zoneId),
//                        description = "Do the old project"
//                    ),
//                    AgendaItem.Reminder( // todo add reminder
//                        id = "0003",
//                        title = "Reminder with Jane",
//                        time = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(3), zoneId),
//                        remindAt = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(2), zoneId),
//                        description = "Reminder to move the different project"
//                    ),
//                    AgendaItem.Task(  // todo add task
//                        id = "0004",
//                        title = "Task with Joe",
//                        time = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(4), zoneId),
//                        remindAt = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(3), zoneId),
//                        description = "Do the the other project",
//                        isDone = true
//                    ),
        }
    }

}


// todo remove later - left for reference
//    private fun createAgendaItem(agendaItemType: AgendaItemType) {
//        viewModelScope.launch {
//            val today = LocalDate.now()
//            val todayDayOfWeek = today.dayOfWeek.name
//            val todayDayOfMonth = today.dayOfMonth
//            val todayMonth = today.month
//            val todayYear = today.year
//            val todayDate = LocalDate.of(todayYear, todayMonth, todayDayOfMonth)
//
//            // todo create Dummy data for now - replace with actual data soon
//            val id = when(agendaItemType) {
////                AgendaItemType.Task ->
////                     AgendaItem.Task(
////                          id = UUID.randomUUID().toString(),
////                          title = "New Task for $todayDate",
////                          time = ZonedDateTime.of(
////                              todayDate,
////                              LocalTime.now().plusHours(1),
////                              ZoneId.systemDefault()
////                          ),
////                          remindAt = ZonedDateTime.of(
////                              todayDate,
////                              LocalTime.now().plusMinutes(30),
////                              ZoneId.systemDefault()
////                          ),
////                          description = "New Task Description - $todayDayOfWeek - $todayDayOfMonth - $todayMonth - $todayYear"
////                     )
////                }
////                AgendaItemType.Reminder ->
////                     AgendaItem.Reminder(
////                          id = UUID.randomUUID().toString(),
////                          title = "New Reminder for $todayDate",
////                          time = ZonedDateTime.of(
////                              todayDate,
////                              LocalTime.now().plusHours(2),
////                              ZoneId.systemDefault()
////                          ),
////                          remindAt = ZonedDateTime.of(
////                              todayDate,
////                              LocalTime.now().plusMinutes(60),
////                              ZoneId.systemDefault()
////                          ),
////                          description = "New Reminder Description - $todayDayOfWeek - $todayDayOfMonth - $todayMonth - $todayYear"
////                     )
////                }
//            }
//
//            id?.let { sendEvent(StatefulOneTimeEvent.ScrollToItemId(id)) }
//        }
//    }
