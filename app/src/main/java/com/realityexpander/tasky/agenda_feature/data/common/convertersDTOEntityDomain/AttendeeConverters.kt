package com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.AttendeeEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.AttendeeDTO
import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.core.util.toEpochMilli
import com.realityexpander.tasky.core.util.toZonedDateTime


// from Domain to Entity
fun Attendee.toEntity() =
    AttendeeEntity(
        id = id,
        eventId = eventId,
        email = email,
        fullName = fullName,
        isGoing = isGoing,
        remindAt = remindAt,
        photo = photo,
    )

// from Domain to DTO (also converts local ZonedDateTime to UTC time)
fun Attendee.toDTO() =
    AttendeeDTO(
        id = id,
        eventId = eventId,
        email = email,
        fullName = fullName,
        isGoing = isGoing,
        remindAt = remindAt?.toEpochMilli(),
        photo = photo,
    )

fun AttendeeEntity.toDomain() =
    Attendee(
        id = id,
        eventId = eventId,
        email = email,
        fullName = fullName,
        isGoing = isGoing,
        remindAt = remindAt,
        photo = photo,
    )

fun AttendeeDTO.toDomain() =
    Attendee(
        id = id,
        eventId = eventId,
        email = email,
        fullName = fullName,
        isGoing = isGoing,
        remindAt = remindAt?.toZonedDateTime(),
        photo = photo,
    )