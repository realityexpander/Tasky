package com.realityexpander.tasky.agenda_feature.presentation.event_screen

import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.agenda_feature.domain.Photo
import com.realityexpander.tasky.core.presentation.common.util.UiText
import java.time.ZonedDateTime

sealed interface EventScreenEvent {
    data class SetIsLoaded(val isLoaded: Boolean) : EventScreenEvent
    data class ShowProgressIndicator(val isShowing: Boolean) : EventScreenEvent

    // • Is Event Editable?
    data class SetIsEditable(val isEditable: Boolean) : EventScreenEvent

    // • The Current EditMode of Event (Title, Description, FromDateTime, ToDateTime, RemindAt, Photos)
    data class SetEditMode(val editMode: EditMode) : EventScreenEvent
    object CancelEditMode : EventScreenEvent

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
        data class TitleText(
            override val text: String = "",
            override val dialogTitle: String = "EDIT TITLE",
        ) : EditMode, EditTextPayload

        data class DescriptionText(
            override val text: String = "",
            override val dialogTitle: String = "EDIT DESCRIPTION",
        ) : EditMode, EditTextPayload

        data class FromDate(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: String = "SET FROM DATE",
        ) : EditMode, EditDateTimePayload
        data class FromTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: String = "SET FROM TIME",
        ) : EditMode, EditDateTimePayload

        data class ToDate(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: String = "SET TO DATE",
        ) : EditMode, EditDateTimePayload
        data class ToTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: String = "SET TO TIME",
        ) : EditMode, EditDateTimePayload

        data class RemindAtDateTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val dialogTitle: String = "SET REMIND AT TIME",
        ) : EditMode, EditDateTimePayload

        data class AddPhoto(
            override val dialogTitle: String = "ADD PHOTO",
        ) : EditMode
        data class ConfirmDeletePhoto(
            override val dialogTitle: String = "CONFIRM DELETE PHOTO",
        ) : EditMode

        data class AddAttendee(
            override val dialogTitle: String = "ADD ATTENDEE",
        ) : EditMode
        data class ConfirmDeleteAttendee(
            val attendee: Attendee,
            override val dialogTitle: String = "CONFIRM DELETE ATTENDEE",
        ) : EditMode


        // • (2) WHAT is being edited? (Text, DateTime, Photo, Attendee)
        sealed interface EditTextPayload {
            val text: String
        }
        sealed interface EditDateTimePayload {
            val dateTime: ZonedDateTime
        }


        // • (3) FINALLY "Save Data" Events - Delivers the edited data payload to the ViewModel
        data class SaveText(val text: String) : EventScreenEvent
        data class SaveDateTime(val dateTime: ZonedDateTime) : EventScreenEvent
        data class SavePhoto(val photo: Photo) : EventScreenEvent
    }
}