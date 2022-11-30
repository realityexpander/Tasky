package com.realityexpander.tasky.agenda_feature.presentation.task_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.common.util.TaskId
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.IAgendaRepository
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText
import com.realityexpander.tasky.agenda_feature.presentation.task_screen.TaskScreenEvent.*
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
class TaskViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val agendaRepository: IAgendaRepository,
    private val savedStateHandle: SavedStateHandle,
    private val validateEmail: ValidateEmail
) : ViewModel() {

    // Get savedStateHandle (after process death)
    private val errorMessage: UiText? =
        savedStateHandle[SavedStateConstants.SAVED_STATE_errorMessage]
    private val isEditable: Boolean =
        savedStateHandle[SavedStateConstants.SAVED_STATE_isEditable] ?: false
    private val editMode: EditMode? =
        savedStateHandle[SavedStateConstants.SAVED_STATE_editMode]

    // Get params from savedStateHandle (from another screen)
    private val initialTaskId: TaskId? =
        savedStateHandle[SavedStateConstants.SAVED_STATE_initialTaskId]

    private val _state = MutableStateFlow(
        TaskScreenState(
            errorMessage = errorMessage,
            isProgressVisible = true,
            isEditable = isEditable,
            editMode = editMode,
        )
    )
    val state =
        _state.onEach { state ->
            // save state for process death
            savedStateHandle[SavedStateConstants.SAVED_STATE_errorMessage] =
                state.errorMessage
            savedStateHandle[SavedStateConstants.SAVED_STATE_isEditable] =
                state.isEditable
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskScreenState())

    private val _oneTimeEvent = MutableSharedFlow<OneTimeEvent>()
    val oneTimeEvent = _oneTimeEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            _state.update { _state ->
                val authInfo = authRepository.getAuthInfo()
                _state.copy(
                    isLoaded = true, // only after state is initialized
                    isProgressVisible = false,
                    username = authInfo?.username ?: "",
                    authInfo = authInfo,

                    task = initialTaskId?.let { taskId ->
                        // Load event from repository
                        agendaRepository.getTask(taskId)
                    }
                        ?:
                        // If `initialTaskId` is null, then create a new Task.
                        // • CREATE A NEW Task
                        AgendaItem.Task(
                            id = UUID.randomUUID().toString(),
                            title = "Title of New Task",
                            description = "Description of New Task",
                            time = ZonedDateTime.now().plusHours(1),
                            remindAt = ZonedDateTime.now().plusMinutes(30),
                            isDone = false,
                        )
                )
            }
        }
    }

    fun sendEvent(event: TaskScreenEvent) {
        viewModelScope.launch {
            onEvent(event)
            yield() // allow events to percolate
        }
    }

    private suspend fun onEvent(uiEvent: TaskScreenEvent) {

        when (uiEvent) {
            is ShowProgressIndicator -> {
                _state.update { _state ->
                    _state.copy(isProgressVisible = uiEvent.isVisible)
                }
                yield()
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
            }
            is EditMode.UpdateText -> {
                when (_state.value.editMode) {

                    is EditMode.ChooseTitleText -> {
                        _state.update { _state ->
                            _state.copy(
                                task = _state.task?.copy(title = uiEvent.text),
                                editMode = null
                            )
                        }
                    }
                    is EditMode.ChooseDescriptionText -> {
                        _state.update { _state ->
                            _state.copy(
                                task = _state.task?.copy(description = uiEvent.text),
                                editMode = null
                            )
                        }
                    }
                    else -> throw java.lang.IllegalStateException("Invalid type for SaveText: ${_state.value.editMode}")
                }
            }
            is EditMode.UpdateDateTime -> {
                when (_state.value.editMode) {

                    is EditMode.ChooseTime,
                    is EditMode.ChooseDate -> {
                        _state.update { _state ->
                            val remindAtDuration =
                                Duration.between(_state.task?.remindAt, _state.task?.time)

                            _state.copy(
                                task = _state.task?.copy(
                                    time = uiEvent.dateTime,

                                    // Update the `RemindAt dateTime` to keep same offset from the `From` date
                                    remindAt = uiEvent.dateTime.minus(remindAtDuration)
                                ),
                                editMode = null
                            )
                        }
                    }
                    is EditMode.ChooseRemindAtDateTime -> {
                        _state.update { _state ->

                            // Ensure that `remindAt <= time`
                            if (uiEvent.dateTime.isAfter(_state.task?.time)) {
                                // make 'time' and 'remindAt' the same
                                return@update _state.copy(
                                    task = _state.task?.copy(
                                        remindAt = _state.task.time
                                    ),
                                    editMode = null
                                )
                            }

                            _state.copy(
                                task = _state.task?.copy(
                                    remindAt = uiEvent.dateTime
                                ),
                                editMode = null
                            )
                        }
                    }
                    else -> throw java.lang.IllegalStateException("Invalid type for SaveDateTime: ${_state.value.editMode}")
                }
            }
            is ToggleIsDone -> {
                _state.update { _state ->
                    _state.copy(
                        task = _state.task?.copy(
                            isDone = !(_state.task.isDone)
                        )
                    )
                }
            }
            OneTimeEvent.NavigateBack -> {
                _oneTimeEvent.emit(
                    OneTimeEvent.NavigateBack
                )
            }
            is ShowAlertDialog -> {
                _state.update { _state ->
                    _state.copy(
                        showAlertDialog =
                            ShowAlertDialog(
                                title = uiEvent.title,
                                message = uiEvent.message,
                                confirmButtonLabel = uiEvent.confirmButtonLabel,
                                onConfirm = uiEvent.onConfirm,
                                isDismissButtonVisible = uiEvent.isDismissButtonVisible,
                            )
                    )
                }
            }
            is DismissAlertDialog -> {
                _state.update { _state ->
                    _state.copy(
                        showAlertDialog = null
                    )
                }
            }
            is SaveTask -> {
                _state.update { _state ->
                    _state.copy(
                        isProgressVisible = true,
                        errorMessage = null
                    )
                }

                val result =
                    when (initialTaskId) {
                        null -> {
                            agendaRepository.createTask(_state.value.task ?: return)
                        }
                        else -> {
                            agendaRepository.updateTask(_state.value.task ?: return)
                        }
                    }

                when (result) {
                    is ResultUiText.Success -> {
                        _state.update { _state ->
                            _state.copy(
                                isProgressVisible = false,
                                errorMessage = null
                            )
                        }
                        _oneTimeEvent.emit(
                            OneTimeEvent.ShowToast(
                                UiText.Res(R.string.task_message_task_saved)
                            )
                        )
                        sendEvent(CancelEditMode)
                        sendEvent(OneTimeEvent.NavigateBack)
                    }
                    is ResultUiText.Error -> {
                        _state.update { _state ->
                            _state.copy(
                                isProgressVisible = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
            is DeleteTask -> {
                _state.value.task ?: return

                _state.update { _state ->
                    _state.copy(
                        isProgressVisible = true,
                        errorMessage = null
                    )
                }

                if (initialTaskId == null) {
                    // Event is not saved yet, so just navigate back
                    _state.update { _state ->
                        _state.copy(
                            isProgressVisible = false,
                            errorMessage = null
                        )
                    }
                    sendEvent(OneTimeEvent.NavigateBack)
                    return
                }

                val result =
                    agendaRepository.deleteTaskId(_state.value.task?.id ?: return)

                when (result) {
                    is ResultUiText.Success -> {
                        _state.update { _state ->
                            _state.copy(
                                isProgressVisible = false,
                                errorMessage = null
                            )
                        }
                        _oneTimeEvent.emit(
                            OneTimeEvent.ShowToast(
                                UiText.Res(R.string.task_message_task_deleted_success)
                            )
                        )
                        sendEvent(CancelEditMode)
                        sendEvent(OneTimeEvent.NavigateBack)
                    }
                    is ResultUiText.Error -> {
                        _state.update { _state ->
                            _state.copy(
                                isProgressVisible = false,
                                errorMessage = UiText.Res(R.string.task_error_delete_task)
                            )
                        }
                    }
                }
            }
            is ShowErrorMessage -> {
                _state.update { _state ->
                    _state.copy(
                        editMode = null,
                        isProgressVisible = false,
                        errorMessage = if (uiEvent.message.isRes)
                            uiEvent.message
                        else
                            UiText.Res(R.string.error_unknown, ""),
                    )
                }
            }
            is ClearErrorMessage -> {
                _state.update { _state ->
                    _state.copy(
                        errorMessage = null,
                    )
                }
            }
            else -> {}
        }
    }
}