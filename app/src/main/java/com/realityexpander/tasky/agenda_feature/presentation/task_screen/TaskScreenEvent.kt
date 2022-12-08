package com.realityexpander.tasky.agenda_feature.presentation.task_screen

import androidx.compose.ui.text.TextStyle
import com.realityexpander.tasky.R
import com.realityexpander.tasky.core.presentation.util.UiText
import java.time.ZonedDateTime

enum class ShowAlertDialogActionType(val title: UiText) {
    DeleteTask(UiText.Res(R.string.event_confirm_action_dialog_delete)),
    ConfirmOK(UiText.Res(android.R.string.ok)),
}

sealed interface TaskScreenEvent {
    data class SetIsLoaded(val isLoaded: Boolean) : TaskScreenEvent
    data class ShowProgressIndicator(val isVisible: Boolean) : TaskScreenEvent

    // • Is Event Editable?
    data class SetIsEditable(val isEditable: Boolean) : TaskScreenEvent

    // • Toggle isDone
    object ToggleIsDone : TaskScreenEvent

    // • The Current EditMode of Event (Title, Description, FromDateTime, ToDateTime, RemindAt, Photos)
    data class SetEditMode(val editMode: EditMode) : TaskScreenEvent
    object CancelEditMode : TaskScreenEvent

    // • Alert Dialog - Confirm Action (Delete/Join/Leave) & General-error-alerts
    data class ShowAlertDialog(
        val title: UiText,
        val message: UiText,
        val confirmButtonLabel: UiText = UiText.Res(android.R.string.ok),
        val onConfirm: () -> Unit,
        val isDismissButtonVisible: Boolean = true,
    ) : TaskScreenEvent
    object DismissAlertDialog : TaskScreenEvent

    // • Update/Save Task
    object SaveTask : TaskScreenEvent
    object DeleteTask : TaskScreenEvent

    // • Errors
    data class ShowErrorMessage(val message: UiText) : TaskScreenEvent
    object ClearErrorMessage : TaskScreenEvent

    // • Non-state One Time Events
    sealed interface OneTimeEvent {
        // • Event - Navigate Back to Previous Screen
        object NavigateBack : TaskScreenEvent, OneTimeEvent

        data class ShowToast(val message: UiText) : TaskScreenEvent, OneTimeEvent
    }

    sealed interface EditMode {

        // Dialog Display options
        abstract val dialogTitle: UiText
        sealed interface EditTextStyle { // dialog uses a specific text style for edit text
            val editTextStyle: TextStyle
        }

        // • (1) WHICH item is being edited?
        // - sets initial/default value and the dialog display string)
        data class ChooseTitleText(
            override val text: String = "",
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_title_text),
            override val editTextStyle: TextStyle = TextStyle.Default
        ) : EditMode, EditTextStyle, TextPayload

        data class ChooseDescriptionText(
            override val text: String = "",
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_description_text),
            override val editTextStyle: TextStyle = TextStyle.Default
        ) : EditMode, EditTextStyle, TextPayload

        data class ChooseDate(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_from_date)
        ) : EditMode, DateTimePayload
        data class ChooseTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_from_time),
        ) : EditMode, DateTimePayload

        data class ChooseRemindAtDateTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_remind_at_date_time)
        ) : EditMode, DateTimePayload


        // • (2) WHAT is being edited? (Text, DateTime, Photo, Attendee)
        sealed interface TextPayload {
            val text: String
        }
        sealed interface DateTimePayload {
            val dateTime: ZonedDateTime
        }


        // • (3) FINALLY "Update/Add/Remove Data" Events - Delivers the updated/added/removed data payload to the ViewModel
        data class UpdateText(override val text: String) : TaskScreenEvent, TextPayload
        data class UpdateDateTime(override val dateTime: ZonedDateTime) : TaskScreenEvent, DateTimePayload
    }
}
