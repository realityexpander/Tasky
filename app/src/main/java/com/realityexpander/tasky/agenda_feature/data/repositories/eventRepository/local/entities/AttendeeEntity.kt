package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities

import com.realityexpander.tasky.agenda_feature.util.AttendeeId
import com.realityexpander.tasky.agenda_feature.util.EventId
import com.realityexpander.tasky.agenda_feature.util.UrlStr
import com.realityexpander.tasky.core.util.Email
import java.time.ZonedDateTime

class AttendeeEntity(
    val id: AttendeeId,
    val eventId: EventId,
    val email: Email,
    val fullName: String,
    val isGoing: Boolean,
    val remindAt: ZonedDateTime,
    val photo: UrlStr
)