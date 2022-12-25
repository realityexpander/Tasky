package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.observeconnectivity.IInternetConnectivityObserver
import com.realityexpander.observeconnectivity.IInternetConnectivityObserver.OnlineStatus
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.data.common.utils.getDateForDayOffset
import com.realityexpander.tasky.agenda_feature.domain.*
import com.realityexpander.tasky.agenda_feature.presentation.agenda_screen.AgendaScreenEvent.*
import com.realityexpander.tasky.agenda_feature.presentation.common.enums.AgendaItemType
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_selectedDate
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_selectedDayIndex
import com.realityexpander.tasky.core.presentation.util.ResultUiText
import com.realityexpander.tasky.core.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val agendaRepository: IAgendaRepository,
    private val connectivityObserver: IInternetConnectivityObserver,
    private val remindAtAlarmManager: IRemindAtAlarmManager,
    private val agendaWorkersScheduler: IAgendaWorkersScheduler,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Get params from savedStateHandle (from another screen or after process death)
    private val selectedDayIndex: Int? =
        savedStateHandle[SAVED_STATE_selectedDayIndex]

    private val selectedDate: ZonedDateTime? =
        savedStateHandle[SAVED_STATE_selectedDate] ?: ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)

    private val _currentDate = MutableStateFlow(selectedDate)
    private val _selectedDayIndex = MutableStateFlow(selectedDayIndex)

    @OptIn(ExperimentalCoroutinesApi::class)
    val onlineState = connectivityObserver.onlineStateFlow.mapLatest { it }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class) // for .flatMapLatest, .flattenMerge
    private val _agendaItems =
        _selectedDayIndex.combine(_currentDate) { dayIndex, date ->
            agendaRepository.syncAgenda()
            agendaRepository.getAgendaForDayFlow(
                getDateForDayOffset(date, dayIndex)
            )
        }
        //.flattenMerge()  // was not working for some reason...
        .flatMapLatest { it }
        .debounce(50)
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _agendaState =
        MutableStateFlow(
            AgendaState(
                // restore state from savedStateHandle after process death
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

        savedStateHandle[SAVED_STATE_selectedDayIndex] = selectedDayIndex
        savedStateHandle[SAVED_STATE_selectedDate] = currentDate

        state.copy(
            agendaItems = items,
            selectedDayIndex = selectedDayIndex,
            weekStartDate = currentDate ?: ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AgendaState())

    private val _oneTimeEvent = MutableSharedFlow<OneTimeEvent>()
    val oneTimeEvent = _oneTimeEvent.asSharedFlow()

    // Tick timer to update the time needle
    private val _zonedDateTimeNow = MutableStateFlow<ZonedDateTime>(ZonedDateTime.now())
    val zonedDateTimeNow = _zonedDateTimeNow.asStateFlow()
    private val timer = Timer()
    private val timerTask =
        object : TimerTask() {
            override fun run() {
                _zonedDateTimeNow.value = ZonedDateTime.now()
            }
        }

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

            // • Start workers for Syncing and Week Refresh
            agendaWorkersScheduler.startAllWorkers()

//            // todo add to unit testing
//            if(agendaState.value.agendaItems.isEmpty()) { // if no items for today, make some fake ones
//                createFakeAgendaItems(agendaRepository)
//            }
        }

        // When connectivity is restored, Sync offline changes & Pull Agenda for today from remote
        viewModelScope.launch {
            onlineState.collect { status ->
                if(status == OnlineStatus.ONLINE) {
                    withContext(Dispatchers.IO) {
                        agendaRepository.syncAgenda()
                        agendaRepository.getAgendaForDayFlow(
                            ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
                        )
                    }
                }
            }
        }

        // Set Alarms for all Agenda Items in coming week
        viewModelScope.launch {
            agendaRepository.getLocalAgendaItemsWithRemindAtInDateTimeRangeFlow(
                    ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS),
                    ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plusWeeks(1)
                )
                .debounce(500)
                .collect { agendaItems ->
                    remindAtAlarmManager.cancelAllAlarms {
                        remindAtAlarmManager.setAlarmsForAgendaItems(agendaItems)
                    }
                }
        }

        // Start ZonedDateTimeNow real-time Update Tick Timer
        timer.scheduleAtFixedRate(timerTask, 0, 1000)
    }

    override fun onCleared() {
        super.onCleared()

        // Cancel ZonedDateTimeNow Timer
        timerTask.cancel()
        timer.cancel()

    }

    fun onSwipeRefresh() {
        viewModelScope.launch {
            _agendaState.update {
                it.copy(isRefreshing = true)
            }

            agendaRepository.syncAgenda()
            agendaRepository.updateLocalAgendaDayFromRemote(
                ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            )
            _agendaState.update {
                it.copy(isRefreshing = false)
            }
        }
    }

    fun sendEvent(event: AgendaScreenEvent) {
        viewModelScope.launch {
            onEvent(event)
            yield() // allow events to percolate
        }
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
                        chooseWeekStartDateDialog = uiEvent.currentDate,
                    )
                }
            }
            is CancelChooseCurrentDateDialog -> {
                _agendaState.update {
                    it.copy(
                        chooseWeekStartDateDialog = null,
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
                        _oneTimeEvent.emit(OneTimeEvent.NavigateToCreateTask)
                    }
                    AgendaItemType.Reminder -> {
                        _oneTimeEvent.emit(OneTimeEvent.NavigateToCreateReminder)
                    }
                }
            }
            is SetTaskCompleted -> {
                agendaRepository.updateTask(
                    uiEvent.agendaItem.copy(isDone = uiEvent.isDone)
                )
            }
            is Logout -> {
                viewModelScope.launch {
                    agendaWorkersScheduler.cancelAllWorkers()
                    agendaRepository.clearAllAgendaItemsLocally()
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
            is ShowConfirmDeleteAgendaItemDialog -> {
                _agendaState.update {
                    it.copy(
                        confirmDeleteAgendaItem = uiEvent.agendaItem
                    )
                }
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
                                agendaRepository.deleteEvent(uiEvent.agendaItem)
                            } else {
                                // Otherwise, the user (as Attendee) is removed from the Event.
                                agendaRepository.removeLoggedInUserFromEvent(
                                    event = uiEvent.agendaItem,
                                )
                            }
                        }
                        is AgendaItem.Task -> {
                            agendaRepository.deleteTask(uiEvent.agendaItem)
                        }
                        is AgendaItem.Reminder -> {
                            agendaRepository.deleteReminder(uiEvent.agendaItem)
                        }
                        else -> {
                            ResultUiText.Error<AgendaItem>(UiText.Res(R.string.error_unknown_agenda_type))
                        }
                    }

                if(result is ResultUiText.Error) {
                    _oneTimeEvent.emit(OneTimeEvent.ShowSnackbar(result.message))
                } else {
                    _oneTimeEvent.emit(OneTimeEvent.ShowSnackbar(
                        UiText.Res(R.string.agenda_item_deleted_success),
                        uiEvent.agendaItem
                    ))
                }
                sendEvent(DismissConfirmDeleteAgendaItemDialog)
            }
            is UndoDeleteAgendaItem -> {
                val result =
                    when (uiEvent.agendaItem) {
                        is AgendaItem.Event -> {
                            if (uiEvent.agendaItem.isUserEventCreator) {
                                agendaRepository.createEvent(uiEvent.agendaItem)
                            } else {
                                _oneTimeEvent.emit(OneTimeEvent.ShowSnackbar(
                                    UiText.Res(R.string.agenda_undo_event_restored_but_not_rejoined),
                                ))
                                // Make a new copy of the Event, but with the logged-in user as the creator.
                                agendaRepository.createEvent(
                                    uiEvent.agendaItem.copy(
                                        id=UUID.randomUUID().toString(),
                                        host = authRepository.getAuthInfo()?.userId,
                                        isUserEventCreator = true,
                                    ))
                            }
                        }
                        is AgendaItem.Task -> {
                            agendaRepository.createTask(uiEvent.agendaItem)
                        }
                        is AgendaItem.Reminder -> {
                            agendaRepository.createReminder(uiEvent.agendaItem)
                        }
                        else -> {
                            ResultUiText.Error<AgendaItem>(UiText.Res(R.string.error_unknown_agenda_type))
                        }
                    }

                if(result is ResultUiText.Error) {
                    _oneTimeEvent.emit(OneTimeEvent.ShowSnackbar(result.message))
                } else {
                    _oneTimeEvent.emit(OneTimeEvent.ShowToast(UiText.Res(R.string.agenda_message_agenda_item_added_success)))
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
            is OneTimeEvent.NavigateToCreateTask -> {
                _oneTimeEvent.emit(OneTimeEvent.NavigateToCreateTask)
            }
            is OneTimeEvent.NavigateToEditTask -> {
                _oneTimeEvent.emit(OneTimeEvent.NavigateToEditTask(uiEvent.taskId))
            }
            is OneTimeEvent.NavigateToOpenTask -> {
                _oneTimeEvent.emit(OneTimeEvent.NavigateToOpenTask(uiEvent.taskId))
            }
            is OneTimeEvent.NavigateToCreateReminder -> {
                _oneTimeEvent.emit(OneTimeEvent.NavigateToCreateReminder)
            }
            is OneTimeEvent.NavigateToEditReminder -> {
                _oneTimeEvent.emit(OneTimeEvent.NavigateToEditReminder(uiEvent.reminderId))
            }
            is OneTimeEvent.NavigateToOpenReminder -> {
                _oneTimeEvent.emit(OneTimeEvent.NavigateToOpenReminder(uiEvent.reminderId))
            }
        }
    }

    // Create Dummy data to pre-populate the Agenda // todo add to unit testing - left for reference
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
        }
    }

}
