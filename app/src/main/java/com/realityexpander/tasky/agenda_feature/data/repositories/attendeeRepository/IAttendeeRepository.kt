package com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository

import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText
import com.realityexpander.tasky.core.util.Email

interface IAttendeeRepository {
    suspend fun getAttendee(email: Email): ResultUiText<Attendee>
    suspend fun removeLoggedInUserFromEvent(event: AgendaItem.Event): ResultUiText<Void>
}
