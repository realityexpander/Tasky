package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.IAgendaRepository
import com.realityexpander.tasky.agenda_feature.presentation.common.enums.AgendaItemType
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_errorMessage
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_selectedDayIndex
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.uuidStr
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
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

    private val _agendaState = MutableStateFlow(AgendaState())
    val agendaState = _agendaState.onEach { state ->
        // save state for process death
        savedStateHandle[SAVED_STATE_errorMessage] = state.errorMessage
        savedStateHandle[SAVED_STATE_selectedDayIndex] = state.selectedDayIndex
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AgendaState())

    init {
        viewModelScope.launch {
            // restore state after process death
            _agendaState.update {
                it.copy(
                    username = authRepository.getAuthInfo()?.username ?: "",
                    isLoaded = true, // only after init occurs
                    errorMessage = errorMessage,
                    authInfo = authRepository.getAuthInfo(),
                    agendaItems = agendaRepository.getAgendaForDayFlow(
                        getDateForSelectedDayIndex(_agendaState.value.selectedDayIndex)
                    ),
                    selectedDayIndex = selectedDayIndex
                )
            }

            yield() // wait for database to load
            if(_agendaState.value.agendaItems.first().isEmpty()) {
                createFakeAgendaItems(agendaRepository)
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    private fun createAgendaItem(agendaItemType: AgendaItemType) {
        viewModelScope.launch {
            val today = LocalDate.now()
            val todayDayOfWeek = today.dayOfWeek.name
            val todayDayOfMonth = today.dayOfMonth
            val todayMonth = today.month
            val todayYear = today.year
            val todayDate = LocalDate.of(todayYear, todayMonth, todayDayOfMonth)

            // todo create Dummy data for now - replace with actual data soon
            val id = when(agendaItemType) {
                AgendaItemType.Event -> {
                    val uuid = UUID.randomUUID().toString()
                    agendaRepository.createEvent(
                       AgendaItem.Event(
                           id = uuid,
                           title = "New Event for ** $todayDate",
                           description = "New Event Description - $uuid",
                           from = ZonedDateTime.of(
                               todayDate.plusDays(1),
                               LocalTime.now(),
                               ZoneId.systemDefault()
                           ),
                           to = ZonedDateTime.of(
                               todayDate.plusDays(1),
                               LocalTime.now().plusHours(1),
                               ZoneId.systemDefault()
                           ),
                           remindAt = ZonedDateTime.of(
                               todayDate.plusDays(1),
                               LocalTime.now().minusMinutes(30),
                               ZoneId.systemDefault()
                           ),
                           attendees =  emptyList(), //listOf(attendeeId("634e5c33628cc62b5ec50b37")),
                           photos = emptyList()
                       ))
                    uuidStr(uuid)
                    // todo add error checking
               }
                AgendaItemType.Task -> { null } // todo replace with actual type
//                     AgendaItem.Task(
//                          id = UUID.randomUUID().toString(),
//                          title = "New Task for $todayDate",
//                          time = ZonedDateTime.of(
//                              todayDate,
//                              LocalTime.now().plusHours(1),
//                              ZoneId.systemDefault()
//                          ),
//                          remindAt = ZonedDateTime.of(
//                              todayDate,
//                              LocalTime.now().plusMinutes(30),
//                              ZoneId.systemDefault()
//                          ),
//                          description = "New Task Description - $todayDayOfWeek - $todayDayOfMonth - $todayMonth - $todayYear"
//                     )
//                }
                AgendaItemType.Reminder -> { null } // todo replace with actual type
//                     AgendaItem.Reminder(
//                          id = UUID.randomUUID().toString(),
//                          title = "New Reminder for $todayDate",
//                          time = ZonedDateTime.of(
//                              todayDate,
//                              LocalTime.now().plusHours(2),
//                              ZoneId.systemDefault()
//                          ),
//                          remindAt = ZonedDateTime.of(
//                              todayDate,
//                              LocalTime.now().plusMinutes(60),
//                              ZoneId.systemDefault()
//                          ),
//                          description = "New Reminder Description - $todayDayOfWeek - $todayDayOfMonth - $todayMonth - $todayYear"
//                     )
//
//                }
            }

            id?.let { sendEvent(AgendaEvent.StatefulOneTimeEvent.ScrollToItemId(id)) }
        }
    }

    fun sendEvent(event: AgendaEvent) {
        viewModelScope.launch {
            onEvent(event)
            yield() // allow events to percolate
        }
    }

    private fun getDateForSelectedDayIndex(selectedDayIndex: Int?): ZonedDateTime {
        return ZonedDateTime.of(
            LocalDate.now(ZoneId.systemDefault())
                .plusDays(selectedDayIndex?.toLong() ?: 0),
            LocalTime.of(0,0),
            ZoneId.systemDefault()
        )
    }

    private suspend fun onEvent(event: AgendaEvent) {

        when(event) {
            is AgendaEvent.ShowProgressIndicator -> {
                _agendaState.update {
                    it.copy(isLoading = event.isShowing)
                }
            }
            is AgendaEvent.SetIsLoaded -> {
                _agendaState.update {
                    it.copy(isLoading = event.isLoaded)
                }
            }
            is AgendaEvent.SetSelectedDayIndex -> {
                _agendaState.update {
                    it.copy(
                        selectedDayIndex = event.dayIndex,
                        agendaItems = agendaRepository.getAgendaForDayFlow(
                            getDateForSelectedDayIndex(event.dayIndex)
                        )
                    )
                }
            }
            is AgendaEvent.CreateAgendaItem -> {
                createAgendaItem(event.agendaItemType)
            }
            is AgendaEvent.TaskToggleCompleted -> {
//                _agendaState.update {  // todo implement update task completed state
//                    it.copy(
//                        agendaItems = it.agendaItems.map { agendaItem ->
//                            (agendaItem as? AgendaItem.Task)?.let { task ->
//                                if (task.id == event.agendaItemId) {
//                                    task.copy(isDone = !task.isDone)
//                                } else {
//                                    task
//                                }
//                            } ?: agendaItem
//                        }
//                    )
//                }
            }
            is AgendaEvent.Logout -> {
                _agendaState.update {
                    it.copy(authInfo = null)
                }
                logout()
            }
            is AgendaEvent.StatefulOneTimeEvent -> {
                when (event) {

                    // Because you can only scroll to one location at a time, we only
                    // need one reset event to reset all the scrolling events.
                    is AgendaEvent.StatefulOneTimeEvent.ResetScrollTo -> {
                        _agendaState.update {
                            it.copy(
                                scrollToItemId = null,
                                scrollToTop = false,
                                scrollToBottom = false
                            )
                        }
                    }
                    is AgendaEvent.StatefulOneTimeEvent.ScrollToTop -> {
                        // • Send the one time event
                        _agendaState.update {
                            it.copy(scrollToTop = true)
                        }
                    }
                    is AgendaEvent.StatefulOneTimeEvent.ScrollToBottom -> {
                        // • Send the one time event
                        _agendaState.update {
                            it.copy(scrollToBottom = true)
                        }
                    }
                    is AgendaEvent.StatefulOneTimeEvent.ScrollToItemId -> {
                        // • Send the one time event
                        _agendaState.update {
                            it.copy(scrollToItemId = event.agendaItemId)
                        }
                    }
                }
            }
            is AgendaEvent.Error -> {
                _agendaState.update {
                    it.copy(
                        errorMessage = if(event.message.isRes)
                            event.message
                        else
                            UiText.Res(R.string.error_unknown, "")
                    )
                }
                sendEvent(AgendaEvent.ShowProgressIndicator(false))
            }
            is AgendaEvent.CreateAgendaItemError -> TODO()
            is AgendaEvent.CreateAgendaItemSuccess -> TODO()
        }
    }

    // Create Dummy data to pre-populate the Agenda // todo remove later
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
                    from = ZonedDateTime.of(todayDate, today.toLocalTime().minusHours(1), zoneId),
                    to = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(1), zoneId),
                    remindAt = ZonedDateTime.of(
                        todayDate,
                        today.toLocalTime().minusMinutes(30),
                        zoneId
                    ),
                    description = "Discuss the new project",
                    host = "John",
                    isUserEventCreator = false,
                    isGoing = true,
                    attendees = emptyList(),
                    photos = emptyList(),
                )
            )
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
            agendaRepository.createEvent(
                AgendaItem.Event(
                    id = UUID.randomUUID().toString(),
                    title = "Meeting with Jack",
                    from = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(5), zoneId),
                    to = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(6), zoneId),
                    remindAt = ZonedDateTime.of(
                        todayDate,
                        today.toLocalTime().plusHours(4),
                        zoneId
                    ),
                    description = "Discuss the yet another project",
                    host = "Jack",
                    isUserEventCreator = true,
                    isGoing = false,
                    attendees = emptyList(),
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
            agendaRepository.createEvent(
                AgendaItem.Event(
                    id = UUID.randomUUID().toString(),
                    title = "Meeting with Jeremy",
                    from = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(8), zoneId),
                    to = ZonedDateTime.of(todayDate, today.toLocalTime().plusHours(9), zoneId),
                    remindAt = ZonedDateTime.of(
                        todayDate,
                        today.toLocalTime().plusHours(7),
                        zoneId
                    ),
                    description = "Discuss the worse project",
                    host = "Jeremy",
                    isUserEventCreator = true,
                    isGoing = true,
                    attendees = emptyList(),
                    photos = emptyList(),
                )
            )
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
