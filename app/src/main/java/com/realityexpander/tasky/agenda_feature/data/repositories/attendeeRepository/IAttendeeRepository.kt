package com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository

import com.realityexpander.tasky.agenda_feature.domain.ResultUiText
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.core.util.Email

interface IAttendeeRepository {
    suspend fun getAttendee(email: Email): ResultUiText<Attendee>
    suspend fun deleteAttendeeFromEvent(eventId: EventId): Boolean
}
