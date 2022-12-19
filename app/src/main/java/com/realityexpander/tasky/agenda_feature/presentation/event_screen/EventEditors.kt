package com.realityexpander.tasky.agenda_feature.presentation.event_screen

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.presentation.common.components.EditTextModal
import com.realityexpander.tasky.agenda_feature.presentation.common.util.toZonedDateTime
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent.*
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.components.PhotoModal
import com.realityexpander.tasky.core.presentation.common.modifiers.DP
import com.realityexpander.tasky.core.presentation.common.modifiers.mediumHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.smallHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.tinyHeight
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDateTime

@Composable
fun EventPropertyEditors(
    editMode: EditMode,
    state: EventScreenState,
    onAction: (EventScreenEvent) -> Unit,
    singlePhotoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
) {
    onAction(ClearErrorMessage)

    when (editMode) {
        is EditMode.ChooseTitleText,
        is EditMode.ChooseDescriptionText -> {
            editMode as EditMode.TextPayload

            EditTextModal(
                title = editMode.dialogTitle.get,
                text = editMode.text,
                editTextStyle = (editMode as EditMode.EditTextStyle).editTextStyle,
                onSave = {
                    onAction(EditMode.UpdateText(it))
                }
            ) {
                onAction(CancelEditMode)
            }
        }
        is EditMode.ChooseFromDate,
        is EditMode.ChooseToDate -> {
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
        is EditMode.ChooseFromTime,
        is EditMode.ChooseToTime -> {
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
        is EditMode.ChooseAddPhoto -> {
            // Reference: https://github.com/philipplackner/NewPhotoPickerAndroid13/blob/master/app/src/main/java/com/plcoding/newphotopickerandroid13/MainActivity.kt
            singlePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
            onAction(CancelEditMode)
        }
        is EditMode.ViewOrRemovePhoto -> {
            PhotoModal(
                photo = editMode.photo,
                title = editMode.dialogTitle.get,
                isRemoveEnabled = state.isEditable,
                onRemove = {
                    onAction(EditMode.RemovePhoto(editMode.photo))
                },
                onCancel = {
                    onAction(CancelEditMode)
                }
            )
        }
        is EditMode.ChooseAddAttendee -> {
            val addAttendeeDialogState = rememberMaterialDialogState()
            var attendeeEmail by remember { mutableStateOf("") }

            addAttendeeDialogState.show()
            MaterialDialog(
                dialogState = addAttendeeDialogState,
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                ),
                buttons = {
                    positiveButton(
                        text = stringResource(android.R.string.ok),
                        textStyle = MaterialTheme.typography.button.copy(
                            if (state.isAttendeeEmailValid == true && !state.isProgressVisible)
                                MaterialTheme.colors.onSurface
                            else
                                MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                        )
                    ) {
                        if (state.isAttendeeEmailValid == true
                            && !state.isProgressVisible
                        ) {
                            onAction(ValidateAttendeeEmailExistsThenAddAttendee(attendeeEmail))
                        }
                    }
                    negativeButton(
                        text = stringResource(android.R.string.cancel),
                        textStyle = MaterialTheme.typography.button.copy(
                            if (!state.isProgressVisible)
                                MaterialTheme.colors.onSurface
                            else
                                MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                        )
                    ) {
                        if (!state.isProgressVisible) {
                            addAttendeeDialogState.hide()
                            onAction(CancelEditMode)
                        }
                    }
                },
                onCloseRequest = {
                    if (!state.isProgressVisible) {
                        addAttendeeDialogState.hide()
                        onAction(CancelEditMode)
                    }
                }
            ) {
                Spacer(modifier = Modifier.tinyHeight())

                Text(
                    text = stringResource(R.string.attendee_add_attendee_dialog_title),
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.mediumHeight())

                OutlinedTextField(
                    value = attendeeEmail,
                    onValueChange = { email ->
                        attendeeEmail = email
                        onAction(ClearErrorsForAddAttendeeDialog)
                        onAction(ValidateAttendeeEmail(email))
                    },
                    label = { Text(stringResource(R.string.attendee_add_attendee_dialog_email_title)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    trailingIcon = {
                        if (state.isProgressVisible) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colors.primary, //.copy(alpha = if(state.isProgressVisible) 1f else 0f),
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            state.isAttendeeEmailValid?.let { emailValid ->
                                if (emailValid) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = stringResource(R.string.emailField_description_isValid),
                                        tint = MaterialTheme.colors.primary
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = stringResource(R.string.error_invalid_email),
                                        tint = MaterialTheme.colors.error
                                    )
                                }
                            }
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = DP.small, end = DP.small)
                )
                Spacer(modifier = Modifier.smallHeight())

                // • ERROR MESSAGE
                Text(
                    text = state.addAttendeeDialogErrorMessage?.get ?: "",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.smallHeight())
            }
        }
        is EditMode.ConfirmRemoveAttendee -> {
            val attendee = editMode.attendee
            val removeAttendeeDialogState = rememberMaterialDialogState()
            onAction(SetIsEditable(true)) // turn on edit mode when removing attendee

            removeAttendeeDialogState.show()
            MaterialDialog(
                dialogState = removeAttendeeDialogState,
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                ),
                buttons = {
                    positiveButton(text = stringResource(R.string.attendee_remove_attendee_dialog_remove_button)) {
                        onAction(EditMode.RemoveAttendee(attendee.id))
                    }
                    negativeButton(text = stringResource(android.R.string.cancel)) {
                        removeAttendeeDialogState.hide()
                        onAction(CancelEditMode)
                    }
                },
                onCloseRequest = {
                    removeAttendeeDialogState.hide()
                    onAction(CancelEditMode)
                }
            ) {
                Spacer(modifier = Modifier.tinyHeight())

                Text(
                    text = stringResource(R.string.event_dialog_title_remove_attendee),
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.mediumHeight())

                Text(
                    text = attendee.fullName,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = DP.small, end = DP.small)
                )
                Text(
                    text = attendee.email,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = DP.small, end = DP.small)
                )
                Spacer(modifier = Modifier.smallHeight())
            }
        }
    }
}