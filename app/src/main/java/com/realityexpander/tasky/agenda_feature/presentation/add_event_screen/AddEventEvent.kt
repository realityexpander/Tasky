package com.realityexpander.tasky.agenda_feature.presentation.add_event_screen

import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.agenda_feature.domain.Photo
import com.realityexpander.tasky.core.presentation.common.util.UiText
import java.time.ZonedDateTime

sealed interface AddEventEvent {
    data class SetIsLoaded(val isLoaded: Boolean) : AddEventEvent
    data class ShowProgressIndicator(val isShowing: Boolean) : AddEventEvent

    // • Is Event Editable?
    data class SetIsEditable(val isEditable: Boolean) : AddEventEvent

    // • The Current EditMode of Event (Title, Description, FromDateTime, ToDateTime, RemindAt, Photos)
    data class SetEditMode(val editMode: EditMode) : AddEventEvent
    data class WriteEditModeText(val text:String) : AddEventEvent
    object CancelEditMode : AddEventEvent

    // • Errors
    data class Error(val message: UiText) : AddEventEvent

    // • One-time events
    sealed interface StatefulOneTimeEvent {
//        object ResetScrollTo                                        : StatefulOneTimeEvent, AddEventEvent
    }

    sealed interface EditMode {

        abstract val title: String

        // For all EditModes that edit text
        sealed interface EditModeText {
            val text: String
        }

        // For all EditModes that edit date
        sealed interface EditModeDate {
            val date: ZonedDateTime
        }

        // For all EditModes that edit time
        sealed interface EditModeTime {
            val time: ZonedDateTime
        }

        // For all EditModes that edit both date and time
        sealed interface EditModeDateTime {
            val dateTime: ZonedDateTime
        }

        data class TitleText(
            override val text: String = "",
            override val title: String = "EDIT TITLE",
        ) : EditMode, EditModeText
        data class DescriptionText(
            override val text: String = "",
            override val title: String = "EDIT DESCRIPTION",
        ) : EditMode, EditModeText

        data class FromDate(
            override val date: ZonedDateTime = ZonedDateTime.now(),
            override val title: String = "EDIT FROM DATE",
        ) : EditMode, EditModeDate
        data class FromTime(
            override val time: ZonedDateTime = ZonedDateTime.now(),
            override val title: String = "EDIT FROM TIME",
        ) : EditMode, EditModeTime

        data class ToDate(
            override val date: ZonedDateTime = ZonedDateTime.now(),
            override val title: String = "EDIT TO DATE",
        ) : EditMode, EditModeDate
        data class ToTime(
            override val time: ZonedDateTime = ZonedDateTime.now(),
            override val title: String = "EDIT TO TIME",
        ) : EditMode, EditModeTime

        data class RemindAtDateTime(
            override val dateTime: ZonedDateTime = ZonedDateTime.now(),
            override val title: String = "EDIT REMIND AT DATE TIME",
        ) : EditMode, EditModeDateTime

        data class Photos(
            val photos: List<Photo> = emptyList(),
            override val title: String = "EDIT PHOTOS",
        ) : EditMode

        data class AddAttendee(
            val attendees: Attendee? = null,
            override val title: String = "ADD ATTENDEES",
        ) : EditMode
        data class Attendees(
            val attendees: List<Attendee> = emptyList(),
            override val title: String = "EDIT ATTENDEES",
        ) : EditMode
    }
}