package com.realityexpander.tasky.agenda_feature.presentation.event_screen

import android.widget.EditText
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

        // • (1) WHICH item is being edited? (sets initial [or default] value, sets up the display string)
        abstract val title: String

        data class TitleText(
            override val text: String = "",
            override val title: String = "EDIT TITLE",
        ) : EditMode, EditText

        data class DescriptionText(
            override val text: String = "",
            override val title: String = "EDIT DESCRIPTION",
        ) : EditMode, EditText

        data class FromDate(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val title: String = "EDIT `FROM` DATE",
        ) : EditMode, EditDateTime
        data class FromTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val title: String = "EDIT `FROM` TIME",
        ) : EditMode, EditDateTime

        data class ToDate(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val title: String = "EDIT `TO` DATE",
        ) : EditMode, EditDateTime
        data class ToTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val title: String = "EDIT `TO` TIME",
        ) : EditMode, EditDateTime

        data class RemindAtDateTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val title: String = "SET `REMIND AT` TIME",
        ) : EditMode, EditDateTime

        data class AddPhoto(
            override val title: String = "ADD PHOTO",
        ) : EditMode
        data class ConfirmDeletePhoto(
            override val title: String = "CONFIRM DELETE PHOTO",
        ) : EditMode

        data class AddAttendee(
            override val title: String = "ADD ATTENDEE",
        ) : EditMode
        data class ConfirmDeleteAttendee(
            val attendee: Attendee,
            override val title: String = "CONFIRM DELETE ATTENDEE",
        ) : EditMode


        // • (2) WHAT is being edited? (Text, DateTime, Photo, Attendee)
        // Payload for all EditModes that edit text
        sealed interface EditText {
            val text: String
        }
        // Payload for all EditModes that edit date or time or both
        sealed interface EditDateTime {
            val dateTime: ZonedDateTime
        }


        // • (3) FINALLY "Save Data" Events - Delivers the edited data payload to the ViewModel
        data class SaveText(val text: String) : EventScreenEvent
        data class SaveDateTime(val dateTime: ZonedDateTime) : EventScreenEvent
        data class SavePhoto(val photo: Photo) : EventScreenEvent
    }
}