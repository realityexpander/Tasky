package com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.EventEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.core.util.toUtcMillis
import com.realityexpander.tasky.core.util.toZonedDateTime
import java.time.ZonedDateTime

// from Domain to Entity
fun AgendaItem.Event.toEntity(): EventEntity {
    return EventEntity(
        id = id,
        title = title,
        description = description,
        remindAt = remindAt,
        from = from,
        to = to,
        host = host,
        isUserEventCreator = isUserEventCreator,
        isGoing = isGoing,
        attendeeIds = attendeeIds,
        photos = photos,
        deletedPhotoKeys = deletedPhotoKeys,
        isDeleted = isDeleted,
    )
}

// from Entity to Domain
fun EventEntity.toDomain(): AgendaItem.Event {
   return AgendaItem.Event(
        id = id,
        title = title,
        description = description,
        from = from,
        to = to,
        remindAt = remindAt,
        host = host,
        isUserEventCreator = isUserEventCreator,
        isGoing = isGoing,
        attendeeIds = attendeeIds,
        photos = photos,
        deletedPhotoKeys = deletedPhotoKeys,
    )
}

// from DTO to Domain (also converts UTC time to local ZonedDateTime)
fun EventDTO.toDomain(): AgendaItem.Event {
   return AgendaItem.Event(
        id = id,
        title = title,
        description = description,
        from = from.toZonedDateTime(),
        to = to.toZonedDateTime(),
        remindAt = remindAt.toZonedDateTime(),
        host = host,
        isUserEventCreator = isUserEventCreator,
        isGoing = isGoing,
        attendeeIds = attendeeIds,
        photos = photos,
        deletedPhotoKeys = deletedPhotoKeys,
    )
}

// from Domain to DTO (also converts local ZonedDateTime to UTC time)
fun AgendaItem.Event.toDTO(): EventDTO {
    return EventDTO(
        id = id,
        title = title,
        description = description,
        from = from.toUtcMillis(),
        to = to.toUtcMillis(),
        remindAt = remindAt.toUtcMillis(),
        host = host,
        isUserEventCreator = isUserEventCreator,
        isGoing = isGoing,
        attendeeIds = attendeeIds,
        photos = photos,
        deletedPhotoKeys = deletedPhotoKeys,
    )
}

fun main() {
    val event = AgendaItem.Event(
        id = "id",
        title = "title",
        description = "description",
        from = ZonedDateTime.now(),
        to = ZonedDateTime.now(),
        remindAt = ZonedDateTime.now(),
        host = "host",
        isUserEventCreator = true,
        isGoing = true,
        attendeeIds = listOf("attendeeId1", "attendeeId2"),
        photos = listOf("photoId1", "photoId2"),
        deletedPhotoKeys = listOf("deletedPhotoId1", "deletedPhotoId2"),
    )
    val eventDTO = event.toDTO()
    val eventEntity = event.toEntity()
    val eventDomain = eventEntity.toDomain()
    val eventDomain2 = eventDTO.toDomain()

    println(event)
    println(eventDTO == event.toDTO())
    println(eventEntity == event.toEntity())
    println(eventDomain == eventEntity.toDomain())
    println(eventDomain2 == eventDTO.toDomain())
}