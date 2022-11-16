package com.realityexpander.tasky.agenda_feature.presentation.event_screen

import com.realityexpander.tasky.agenda_feature.common.util.AttendeeId
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
    object ClearAddAttendeeDialogErrorMessage : EventScreenEvent
    data class SetAddAttendeeDialogErrorMessage(val message: UiText) : EventScreenEvent

    // • Errors
    data class Error(val message: UiText) : EventScreenEvent

    // • One-time events  // todo setup one-time events
    sealed interface StatefulOneTimeEvent {
//        object ResetScrollTo                                        : StatefulOneTimeEvent, AddEventEvent
    }

    sealed interface EditMode {

        abstract val dialogTitle: String

        // • (1) WHICH item is being edited?
        // - sets initial/default value and the dialog display string)
        data class ChooseTitleText(
            override val text: String = "",
            override val dialogTitle: String = "EDIT TITLE",
        ) : EditMode, TextPayload

        data class ChooseDescriptionText(
            override val text: String = "",
            override val dialogTitle: String = "EDIT DESCRIPTION",
        ) : EditMode, TextPayload

        data class ChooseFromDate(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: String = "SET FROM DATE",
        ) : EditMode, DateTimePayload
        data class ChooseFromTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: String = "SET FROM TIME",
        ) : EditMode, DateTimePayload

        data class ChooseToDate(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: String = "SET TO DATE",
        ) : EditMode, DateTimePayload
        data class ChooseToTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: String = "SET TO TIME",
        ) : EditMode, DateTimePayload

        data class ChooseRemindAtDateTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: String = "SET REMIND AT TIME",
        ) : EditMode, DateTimePayload

        data class ChooseAddPhoto(
            override val dialogTitle: String = "ADD PHOTO",
        ) : EditMode
        data class ConfirmRemovePhoto(
            override val dialogTitle: String = "CONFIRM DELETE PHOTO",
        ) : EditMode

        data class ChooseAddAttendee(
            override val dialogTitle: String = "ADD ATTENDEE",
        ) : EditMode
        data class ConfirmRemoveAttendee(
            val attendee: Attendee,
            override val dialogTitle: String = "CONFIRM REMOVE ATTENDEE",
        ) : EditMode


        // • (2) WHAT is being edited? (Text, DateTime, Photo, Attendee)
        sealed interface TextPayload {
            val text: String
        }
        sealed interface DateTimePayload {
            val dateTime: ZonedDateTime
        }
        sealed interface AttendeePayload {
            val attendee: Attendee
        }
        sealed interface AttendeeIdPayload {
            val attendeeId: AttendeeId
        }


        // • (3) FINALLY "Update Data" Events - Delivers the edited data payload to the ViewModel
        data class UpdateText(override val text: String) : EventScreenEvent, TextPayload
        data class UpdateDateTime(override val dateTime: ZonedDateTime) : EventScreenEvent, DateTimePayload
        data class AddPhoto(val photo: Photo) : EventScreenEvent
        data class RemovePhoto(val photo: Photo) : EventScreenEvent
        data class AddAttendee(override val attendee: Attendee) : EventScreenEvent, AttendeePayload
        data class RemoveAttendee(override val attendeeId: AttendeeId) : EventScreenEvent, AttendeeIdPayload
    }
}