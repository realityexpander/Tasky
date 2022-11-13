package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities

import com.realityexpander.tasky.agenda_feature.data.common.serializers.ZonedDateTimeSerializer
import com.realityexpander.tasky.agenda_feature.common.util.AttendeeId
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.common.util.UrlStr
import com.realityexpander.tasky.core.util.Email
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable  // for ZonedDateTimeSerializer
class AttendeeEntity(
    val id: AttendeeId,
    val eventId: EventId? = null,
    val email: Email,
    val fullName: String,
    val isGoing: Boolean,

    @Serializable(with = ZonedDateTimeSerializer::class) // for Room @TypeConverter
    val remindAt: ZonedDateTime? = null,

    val photo: UrlStr? = null,
)