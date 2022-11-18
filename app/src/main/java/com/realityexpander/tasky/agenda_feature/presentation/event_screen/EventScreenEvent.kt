package com.realityexpander.tasky.agenda_feature.presentation.event_screen

import androidx.compose.ui.text.TextStyle
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.common.util.AttendeeId
import com.realityexpander.tasky.agenda_feature.common.util.PhotoId
import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.agenda_feature.domain.Photo
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.Email
import java.time.ZonedDateTime

sealed interface EventScreenEvent {
    data class SetIsLoaded(val isLoaded: Boolean) : EventScreenEvent
    data class ShowProgressIndicator(val isShowing: Boolean) : EventScreenEvent

    // • Is Event Editable?
    data class SetIsEditable(val isEditable: Boolean) : EventScreenEvent

    // • The Current EditMode of Event (Title, Description, FromDateTime, ToDateTime, RemindAt, Photos)
    data class SetEditMode(val editMode: EditMode) : EventScreenEvent
    object CancelEditMode : EventScreenEvent

    // • Add Attendee Dialog
    data class ValidateAttendeeEmailExistsThenAddAttendee(val email: Email) : EventScreenEvent
    object ClearErrorsForAddAttendeeDialog : EventScreenEvent
    data class SetErrorMessageForAddAttendeeDialog(val message: UiText) : EventScreenEvent
    data class ValidateAttendeeEmail(val email: Email) : EventScreenEvent

    // • Errors
    data class Error(val message: UiText) : EventScreenEvent

    // • One-time events  // todo setup one-time events
    sealed interface StatefulOneTimeEvent {
//        object ResetScrollTo                                        : StatefulOneTimeEvent, AddEventEvent
    }

    sealed interface EditMode {

        // Dialog Display values
        abstract val dialogTitle: UiText
        sealed interface EditTextStyle { // dialog uses a specific text style
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

        data class ChooseFromDate(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_from_date)
        ) : EditMode, DateTimePayload
        data class ChooseFromTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_from_time),
        ) : EditMode, DateTimePayload

        data class ChooseToDate(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_to_date)
        ) : EditMode, DateTimePayload
        data class ChooseToTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_to_time)
        ) : EditMode, DateTimePayload

        data class ChooseRemindAtDateTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_remind_at_date_time)
        ) : EditMode, DateTimePayload

        data class ChooseAddPhoto(
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_add_photo)
        ) : EditMode
        data class ConfirmRemovePhoto(
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_confirm_remove_photo)
        ) : EditMode

        data class ChooseAddAttendee(
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_choose_add_attendee)
        ) : EditMode
        data class ConfirmRemoveAttendee(
            val attendee: Attendee,
            override val dialogTitle: UiText = UiText.Res(R.string.event_dialog_title_confirm_remove_attendee)
        ) : EditMode


        // • (2) WHAT is being edited? (Text, DateTime, Photo, Attendee)
        sealed interface TextPayload {
            val text: String
        }
        sealed interface DateTimePayload {
            val dateTime: ZonedDateTime
        }
        sealed interface PhotoPayload {
            val photoLocal: Photo.Local
        }
        sealed interface PhotoIdPayload {
            val photoId: PhotoId
        }
        sealed interface AttendeePayload {
            val attendee: Attendee
        }
        sealed interface AttendeeIdPayload {
            val attendeeId: AttendeeId
        }


        // • (3) FINALLY "Update Data" Events - Delivers the edited/added/deleted data payload to the ViewModel
        data class UpdateText(override val text: String) : EventScreenEvent, TextPayload
        data class UpdateDateTime(override val dateTime: ZonedDateTime) : EventScreenEvent, DateTimePayload
        data class AddPhoto(override val photoLocal: Photo.Local) : EventScreenEvent, PhotoPayload
        data class RemovePhoto(override val photoId: PhotoId) : EventScreenEvent, PhotoIdPayload
        data class AddAttendee(override val attendee: Attendee) : EventScreenEvent, AttendeePayload
        data class RemoveAttendee(override val attendeeId: AttendeeId) : EventScreenEvent, AttendeeIdPayload
    }
}