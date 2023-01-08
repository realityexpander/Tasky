package com.realityexpander.tasky.agenda_feature.presentation.event_screen

import android.os.Parcelable
import androidx.compose.ui.text.TextStyle
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.common.util.AttendeeId
import com.realityexpander.tasky.agenda_feature.data.common.typeParceler.TextStyleParceler
import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.agenda_feature.domain.Photo
import com.realityexpander.tasky.core.presentation.util.UiText
import com.realityexpander.tasky.core.util.Email
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import java.time.ZonedDateTime

enum class ShowAlertDialogActionType(val title: UiText) {
    DeleteEvent(UiText.Res(R.string.event_confirm_action_dialog_delete)),
    LeaveEvent(UiText.Res(R.string.event_confirm_action_dialog_leave)),
    JoinEvent(UiText.Res(R.string.event_confirm_action_dialog_join)),
    ConfirmOK(UiText.Res(android.R.string.ok)),
}

sealed interface EventScreenEvent {
    data class SetIsLoaded(val isLoaded: Boolean) : EventScreenEvent
    data class ShowProgressIndicator(val isVisible: Boolean) : EventScreenEvent

    // • Is Event Editable?
    data class SetIsEditable(val isEditable: Boolean) : EventScreenEvent

    // • The Current EditMode of Event (Title, Description, FromDateTime, ToDateTime, RemindAt, Photos)
    data class SetEditMode(val editMode: EditMode) : EventScreenEvent
    object CancelEditMode : EventScreenEvent

    // • Alert Dialog - Confirm Action (Delete/Join/Leave) & General-error-alerts
    data class ShowAlertDialog(
        val title: UiText,
        val message: UiText,
        val confirmButtonLabel: UiText = UiText.Res(android.R.string.ok),
        val onConfirm: () -> Unit,
        val isDismissButtonVisible: Boolean = true,
    ) : EventScreenEvent
    object DismissAlertDialog : EventScreenEvent

    // • Update/Delete/Join/Leave Event
    object SaveEvent : EventScreenEvent
    object DeleteEvent : EventScreenEvent
    object LeaveEvent : EventScreenEvent
    object JoinEvent : EventScreenEvent

    // • Add Attendee Dialog
    data class ValidateAttendeeEmailExistsThenAddAttendee(val email: Email) : EventScreenEvent
    object ClearErrorsForAddAttendeeDialog : EventScreenEvent
    data class SetErrorMessageForAddAttendeeDialog(val message: UiText) : EventScreenEvent
    data class ValidateAttendeeEmail(val email: Email) : EventScreenEvent

    // • Errors
    data class ShowErrorMessage(val message: UiText) : EventScreenEvent
    object ClearErrorMessage : EventScreenEvent

    // • Non-state One Time Events
    sealed interface OneTimeEvent {
        // • Event - Navigate Back to Previous Screen
        object NavigateBack : EventScreenEvent, OneTimeEvent

        data class ShowToast(val message: UiText) : EventScreenEvent, OneTimeEvent
        object LaunchPhotoPicker : EventScreenEvent, OneTimeEvent
    }

    sealed interface EditMode {

        // Dialog Display options
        abstract val dialogTitle: UiText
        sealed interface EditTextStyle { // dialog uses a specific text style for edit text
            val editTextStyle: TextStyle
        }

        // • (1) WHICH item is being edited?
        // - sets initial/default value and the dialog display string & text style
        @Parcelize
        @TypeParceler<TextStyle, TextStyleParceler>()
        data class ChooseTitleText(
            override val text: String = "",
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_title_text),
            override val editTextStyle: TextStyle = TextStyle.Default
        ) : EditMode, EditTextStyle, TextPayload, Parcelable

        @Parcelize
        @TypeParceler<TextStyle, TextStyleParceler>()
        data class ChooseDescriptionText(
            override val text: String = "",
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_description_text),
            override val editTextStyle: TextStyle = TextStyle.Default
        ) : EditMode, EditTextStyle, TextPayload, Parcelable

        @Parcelize
        data class ChooseFromDate(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_from_date)
        ) : EditMode, DateTimePayload, Parcelable
        @Parcelize
        data class ChooseFromTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_from_time),
        ) : EditMode, DateTimePayload, Parcelable

        @Parcelize
        data class ChooseToDate(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_to_date)
        ) : EditMode, DateTimePayload, Parcelable
        @Parcelize
        data class ChooseToTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_to_time)
        ) : EditMode, DateTimePayload, Parcelable

        @Parcelize
        data class ChooseRemindAtDateTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_remind_at_date_time)
        ) : EditMode, DateTimePayload, Parcelable

        @Parcelize
        data class ChooseAddPhoto(
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_add_photo)
        ) : EditMode, Parcelable
        @Parcelize
        data class ViewOrRemovePhoto(
            val photo: Photo,
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_view_or_delete_photo)
        ) : EditMode, Parcelable

        @Parcelize
        data class ChooseAddAttendee(
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_add_attendee)
        ) : EditMode, Parcelable
        @Parcelize
        data class ConfirmRemoveAttendee(
            val attendee: Attendee,
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_confirm_remove_attendee)
        ) : EditMode, Parcelable


        // • (2) WHAT is being edited? (Text, DateTime, Photo, Attendee)
        sealed interface TextPayload {
            val text: String
        }
        sealed interface DateTimePayload {
            val dateTime: ZonedDateTime
        }
        sealed interface PhotoLocalPayload {
            val photoLocal: Photo.Local
        }
        sealed interface PhotoPayload {
            val photo: Photo
        }
        sealed interface AttendeePayload {
            val attendee: Attendee
        }
        sealed interface AttendeeIdPayload {
            val attendeeId: AttendeeId
        }


        // • (3) FINALLY "Update/Add/Remove Data" Events - Delivers the updated/added/removed data payload to the ViewModel
        data class UpdateText(override val text: String) : EventScreenEvent, TextPayload
        data class UpdateDateTime(override val dateTime: ZonedDateTime) : EventScreenEvent, DateTimePayload
        data class AddLocalPhoto(override val photoLocal: Photo.Local) : EventScreenEvent, PhotoLocalPayload
        data class RemovePhoto(override val photo: Photo) : EventScreenEvent, PhotoPayload
        data class AddAttendee(override val attendee: Attendee) : EventScreenEvent, AttendeePayload
        data class RemoveAttendee(override val attendeeId: AttendeeId) : EventScreenEvent, AttendeeIdPayload
    }
}
