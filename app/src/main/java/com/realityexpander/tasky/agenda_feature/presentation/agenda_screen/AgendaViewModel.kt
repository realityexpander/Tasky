package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.R
import com.realityexpander.tasky.TaskyApplication
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.presentation.common.enums.AgendaItemType
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_agendaItemIdForMenu
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_agendaItems
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_authInfo
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_errorMessage
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_isLoaded
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_selectedDayIndex
import com.realityexpander.tasky.core.presentation.common.SavedStateConstants.SAVED_STATE_username
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.UuidStr
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    //private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // todo fix this when figure out Compose-destinations savedStateHandle issue
    private val savedStateHandle: SavedStateHandle = TaskyApplication.savedStateHandle

    // Get params from savedStateHandle (from another screen or after process death)
    private val username: String =
        Uri.decode(savedStateHandle[SAVED_STATE_username]) ?: ""
    private val isLoaded: Boolean =
        savedStateHandle[SAVED_STATE_isLoaded] ?: false
    private val errorMessage: UiText? =
        savedStateHandle[SAVED_STATE_errorMessage]
    private val authInfo: AuthInfo? =
        savedStateHandle[SAVED_STATE_authInfo]
    private val agendaItemIdForMenu: UuidStr? =
        savedStateHandle[SAVED_STATE_agendaItemIdForMenu]
    private val agendaItems: List<AgendaItem> =
        savedStateHandle[SAVED_STATE_agendaItems] ?: emptyList<AgendaItem>()
    private val selectedDayIndex: Int =
        savedStateHandle[SAVED_STATE_selectedDayIndex] ?: 0

    private val _agendaState = MutableStateFlow(AgendaState())
    val agendaState = _agendaState.onEach { state ->
        // save state for process death
        savedStateHandle[SAVED_STATE_username] = state.username
        savedStateHandle[SAVED_STATE_isLoaded] = state.isLoaded
        savedStateHandle[SAVED_STATE_errorMessage] = state.errorMessage
        savedStateHandle[SAVED_STATE_authInfo] = state.authInfo
        savedStateHandle[SAVED_STATE_agendaItems] = state.agendaItems
        savedStateHandle[SAVED_STATE_selectedDayIndex] = state.selectedDayIndex
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AgendaState())

    init {
        viewModelScope.launch {
            // restore state after process death
            _agendaState.update {
                it.copy(
                    username = username,
                    isLoaded = true,
                    errorMessage = errorMessage,
                    authInfo = authRepository.getAuthInfo(),
                    agendaItems = agendaItems,
                    oneTimeEvent = null,
                    selectedDayIndex = selectedDayIndex
                )
            }

            // simulate load from network or database
            // Dummy data for now
            val today = LocalDate.now()
            val todayDayOfWeek = today.dayOfWeek.value
            val todayDayOfMonth = today.dayOfMonth
            val todayMonth = today.month
            val todayYear = today.year
            val todayDate = LocalDate.of(todayYear, todayMonth, todayDayOfMonth)
            val agendaItemsLoadedFromDB = mutableListOf<AgendaItem>(
                AgendaItem.Event(
                    id = "0001",
                    title = "Meeting with John",
                    from = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 10, 0),
                    to = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 11, 0),
                    remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 9, 0),
                    description = "Discuss the new project"
                ),
                AgendaItem.Task(
                    id = "0002",
                    title = "Task with Jim",
                    time = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 13, 0),
                    remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 12, 30),
                    description = "Do the old project"
                ),
                AgendaItem.Reminder(
                    id = "0003",
                    title = "Reminder with Jane",
                    time = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 16, 0),
                    remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 14, 30),
                    description = "Reminder to move the different project"
                ),
                AgendaItem.Task(
                    id = "0004",
                    title = "Task with Joe",
                    time = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 18, 0),
                    remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 16, 30),
                    description = "Do the the other project",
                    isDone = true
                ),
                AgendaItem.Event(
                    id = "0005",
                    title = "Meeting with Jack",
                    from = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 19, 0),
                    to = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 20, 0),
                    remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 18, 30),
                    description = "Discuss the yet another project"
                ),
                AgendaItem.Reminder(
                    id = "0006",
                    title = "Reminder with Jill",
                    time = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 22, 0),
                    remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 20, 30),
                    description = "Make the similar project"
                ),
                AgendaItem.Event(
                    id = "0007",
                    title = "Meeting with Jeremy",
                    from = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 10, 0),
                    to = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 11, 0),
                    remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 9, 0),
                    description = "Discuss the worse project"
                ),
                AgendaItem.Task(
                    id = "0008",
                    title = "Chore with Jason",
                    time = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 14, 0),
                    remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 12, 30),
                    description = "Kill the better project",
                    isDone = true
                ),
            )

            _agendaState.update {
                it.copy(agendaItems = agendaItemsLoadedFromDB)
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.clearAuthInfo()
        }
    }

    private fun createAgendaItem(agendaItemType: AgendaItemType) {
        viewModelScope.launch {
            val agendaItems = agendaState.value.agendaItems.toMutableList()

            var agendaItem: AgendaItem? = null
            val today = LocalDate.now()
            val todayDayOfWeek = today.dayOfWeek.value
            val todayDayOfMonth = today.dayOfMonth
            val todayMonth = today.month
            val todayYear = today.year
            val todayDate = LocalDate.of(todayYear, todayMonth, todayDayOfMonth)

            // todo create Dummy data for now - replace with actual data soon
            when(agendaItemType) {
                AgendaItemType.Event -> {
                   agendaItem = AgendaItem.Event(
                       id = UUID.randomUUID().toString(),
                       title = "New Event for $todayDate",
                       from = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 10, 0),
                       to = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 11, 0),
                       remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 9, 0),
                       description = "New Event Description - $todayDayOfWeek - $todayDayOfMonth - $todayMonth - $todayYear"
                   )
                   agendaItems.add(agendaItem)
               }
                AgendaItemType.Task -> {
                     agendaItem = AgendaItem.Task(
                          id = UUID.randomUUID().toString(),
                          title = "New Task for $todayDate",
                          time = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 13, 0),
                          remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 12, 30),
                          description = "New Task Description - $todayDayOfWeek - $todayDayOfMonth - $todayMonth - $todayYear"
                     )
                     agendaItems.add(agendaItem)
                }
                AgendaItemType.Reminder -> {
                     agendaItem = AgendaItem.Reminder(
                          id = UUID.randomUUID().toString(),
                          title = "New Reminder for $todayDate",
                          time = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 16, 0),
                          remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 14, 30),
                          description = "New Reminder Description - $todayDayOfWeek - $todayDayOfMonth - $todayMonth - $todayYear"
                     )
                     agendaItems.add(agendaItem)
                }
            }

            _agendaState.value = agendaState.value.copy(agendaItems = agendaItems)

            sendEvent(AgendaEvent.StatefulOneTimeEvent.ScrollToItem(agendaItem.id))
        }
    }

    fun sendEvent(event: AgendaEvent) {
        viewModelScope.launch {
            onEvent(event)
            yield() // allow events to percolate
        }
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
                    it.copy(selectedDayIndex = event.dayIndex)
                }
            }
            is AgendaEvent.CreateAgendaItem -> {
                createAgendaItem(event.agendaItemType)
            }
            is AgendaEvent.TaskToggleCompleted -> {
                _agendaState.update {
                    it.copy(
                        agendaItems = it.agendaItems.map { agendaItem ->
                            (agendaItem as? AgendaItem.Task)?.let { task ->
                                if (task.id == event.agendaItemId) {
                                    task.copy(isDone = !task.isDone)
                                } else {
                                    task
                                }
                            } ?: agendaItem
                        }
                    )
                }
            }
            is AgendaEvent.Logout -> {
                _agendaState.update {
                    it.copy(authInfo = null)
                }
                logout()
            }
            is AgendaEvent.StatefulOneTimeEvent -> {
                when (event) {
                    is AgendaEvent.StatefulOneTimeEvent.Reset -> {
                        // • Reset the one time event
                        _agendaState.update {
                            it.copy(oneTimeEvent = null)
                        }
                    }
                    else -> {
                        // • Send the one time event
                        _agendaState.update {
                            it.copy(oneTimeEvent = event)
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
}
