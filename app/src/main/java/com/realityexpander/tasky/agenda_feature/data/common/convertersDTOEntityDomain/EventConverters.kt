package com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.EventEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.core.util.toUtcLong
import com.realityexpander.tasky.core.util.toZonedDateTime

// from Domain to Entity
fun AgendaItem.Event.toEntity(): EventEntity {
    return this.let {
        EventEntity(
            id = it.id,
            title = it.title,
            description = it.description,
            from = it.from,
            to = it.to,
            remindAt = it.remindAt,
            host = it.host,
            isUserEventCreator = it.isUserEventCreator,
            isGoing = it.isGoing,
            attendeeIds = it.attendeeIds,
            photos = it.photos,
            deletedPhotoKeys = it.deletedPhotoKeys,
        )
    }
}

// from Entity to Domain
fun EventEntity.toDomain(): AgendaItem.Event {
    return this.let {
        AgendaItem.Event(
            id = it.id,
            title = it.title,
            description = it.description,
            from = it.from,
            to = it.to,
            remindAt = it.remindAt,
            host = it.host,
            isUserEventCreator = it.isUserEventCreator,
            isGoing = it.isGoing,
            attendeeIds = it.attendeeIds,
            photos = it.photos,
            deletedPhotoKeys = it.deletedPhotoKeys,
        )
    }
}

// from DTO to Domain (converts UTC to local ZonedDateTime)
fun EventDTO.toDomain(): AgendaItem.Event {
    return this.let {
        AgendaItem.Event(
            id = it.id,
            title = it.title,
            description = it.description,
            from = it.from.toZonedDateTime(),
            to = it.to.toZonedDateTime(),
            remindAt = it.remindAt.toZonedDateTime(),
            host = it.host,
            isUserEventCreator = it.isUserEventCreator,
            isGoing = it.isGoing,
            attendeeIds = it.attendeeIds,
            photos = it.photos,
            deletedPhotoKeys = it.deletedPhotoKeys,
        )
    }
}

// from Domain to DTO (converts local ZonedDateTime to UTC)
fun AgendaItem.Event.toDTO(): EventDTO {
    return this.let {
        EventDTO(
            id = it.id,
            title = it.title,
            description = it.description,
            from = it.from.toUtcLong(),
            to = it.to.toUtcLong(),
            remindAt = it.remindAt.toUtcLong(),
            host = it.host,
            isUserEventCreator = it.isUserEventCreator,
            isGoing = it.isGoing,
            attendeeIds = it.attendeeIds,
            photos = it.photos,
            deletedPhotoKeys = it.deletedPhotoKeys,
        )
    }
}