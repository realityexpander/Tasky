package com.realityexpander.tasky.agenda_feature.presentation.task_screen

import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.realityexpander.tasky.agenda_feature.presentation.common.components.EditTextModal
import com.realityexpander.tasky.agenda_feature.presentation.common.util.toZonedDateTime
import com.realityexpander.tasky.agenda_feature.presentation.task_screen.TaskScreenEvent.*
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDateTime

@Composable
fun TaskPropertyEditors(
    editMode: EditMode,
    onAction: (TaskScreenEvent) -> Unit,
) {
    onAction(ClearErrorMessage)

    when (editMode) {
        is EditMode.ChooseTitleText,
        is EditMode.ChooseDescriptionText -> {
            editMode as EditMode.TextPayload

            EditTextModal(
                text = editMode.text,
                title = editMode.dialogTitle.get,
                editTextStyle = (editMode as EditMode.EditTextStyle).editTextStyle,
                onSave = {
                    onAction(EditMode.UpdateText(it))
                },
                onCancel = {
                    onAction(CancelEditMode)
                }
            )
        }
        is EditMode.ChooseDate -> {
            editMode as EditMode.DateTimePayload
            var pickedDate by remember { mutableStateOf(LocalDateTime.now()) }
            val dateDialogState = rememberMaterialDialogState()

            dateDialogState.show()
            MaterialDialog(
                dialogState = dateDialogState,
                buttons = {
                    positiveButton(text = stringResource(android.R.string.ok)) {
                        onAction(EditMode.UpdateDateTime(pickedDate?.toZonedDateTime()!!))
                    }
                    negativeButton(text = stringResource(android.R.string.cancel)) {
                        dateDialogState.hide()
                        onAction(CancelEditMode)
                    }
                }
            ) {
                datepicker(
                    initialDate = editMode.dateTime.toLocalDate(),
                    title = editMode.dialogTitle.get,
                ) {
                    pickedDate = it.atTime(editMode.dateTime.toLocalTime())
                }
            }
        }
        is EditMode.ChooseTime -> {
            editMode as EditMode.DateTimePayload
            var pickedTime by remember { mutableStateOf(LocalDateTime.now()) }
            val dateDialogState = rememberMaterialDialogState()

            dateDialogState.show()
            MaterialDialog(
                dialogState = dateDialogState,
                buttons = {
                    positiveButton(text = stringResource(android.R.string.ok)) {
                        onAction(EditMode.UpdateDateTime(pickedTime?.toZonedDateTime()!!))
                    }
                    negativeButton(text = stringResource(android.R.string.cancel)) {
                        dateDialogState.hide()
                        onAction(CancelEditMode)
                    }
                }
            ) {
                timepicker(
                    initialTime = editMode.dateTime.toLocalTime(),
                    title = editMode.dialogTitle.get,
                ) {
                    pickedTime = it.atDate(editMode.dateTime.toLocalDate())
                }
            }
        }
        is EditMode.ChooseRemindAtDateTime -> { // handled in the RemindAt UI element, this is here to remove compiler warning
        }
    }
}