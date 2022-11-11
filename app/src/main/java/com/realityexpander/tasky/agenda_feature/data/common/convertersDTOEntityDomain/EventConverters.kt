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
        photoIds = photoIds,
        deletedPhotoKeys = deletedPhotoKeys,
        isDeleted = isDeleted,
        // todo add attendees and photos & converters
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
        photoIds = photoIds,
        deletedPhotoKeys = deletedPhotoKeys,
    )
}

//// from DTO to Domain (also converts UTC time to local ZonedDateTime)
//fun EventDTO1.toDomain(): AgendaItem.Event {
//   return AgendaItem.Event(
//        id = id,
//        title = title,
//        description = description,
//        from = from.toZonedDateTime(),
//        to = to.toZonedDateTime(),
//        remindAt = remindAt.toZonedDateTime(),
//        host = host,
//        isUserEventCreator = isUserEventCreator,
//        isGoing = isGoing,
//        attendeeIds = attendeeIds,
//        photoIds = photos,
//        deletedPhotoKeys = deletedPhotoKeys,
//    )
//}
//
//// from Domain to DTO (also converts local ZonedDateTime to UTC time)
//fun AgendaItem.Event.toDTO(): EventDTO1 {
//    return EventDTO1(
//        id = id,
//        title = title,
//        description = description,
//        from = from.toUtcMillis(),
//        to = to.toUtcMillis(),
//        remindAt = remindAt.toUtcMillis(),
//        host = host,
//        isUserEventCreator = isUserEventCreator,
//        isGoing = isGoing,
//        attendeeIds = attendeeIds,
//        photos = photoIds,
//        deletedPhotoKeys = deletedPhotoKeys,
//    )
//}

// from DTO to Domain (also converts UTC time millis to local ZonedDateTime)
fun EventDTO.toDomain(): AgendaItem.Event {
    when (this) {
        is EventDTO.Create -> {
            return AgendaItem.Event(
                id = id,
                title = title,
                description = description,
                from = from.toZonedDateTime(),
                to = to.toZonedDateTime(),
                remindAt = remindAt.toZonedDateTime(),

                attendeeIds = attendeeIds,
            )
        }
        is EventDTO.Update -> {
            return AgendaItem.Event(
                id = id,
                title = title,
                description = description,
                from = from.toZonedDateTime(),
                to = to.toZonedDateTime(),
                remindAt = remindAt.toZonedDateTime(),

                isGoing = isGoing,
                attendeeIds = attendeeIds,
                deletedPhotoKeys = deletedPhotoIds,
            )
        }
        is EventDTO.Response -> {
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
                attendees = attendees.map { it.toDomain() },
                attendeeIds = attendees.map { it.id },      // extract the attendees Ids
                photos = photos.map { it.toDomain() },
                photoIds = photos.map { it.id },            // extract the photo Ids
            )
        }
        else -> {
            throw Exception("EventAbstractDTO.toDomain() not implemented for ${this::class.simpleName}")
        }
    }
}

// from Domain to EventDTO.Create (also converts local ZonedDateTime to UTC time millis)
fun AgendaItem.Event.toEventDTOCreate(): EventDTO.Create {
    return EventDTO.Create(
        id = id,
        title = title,
        description = description,
        from = from.toUtcMillis(),
        to = to.toUtcMillis(),
        remindAt = remindAt.toUtcMillis(),
        attendeeIds = attendeeIds,
    )
}


// from Domain to EventDTO.Update (also converts local ZonedDateTime to UTC time millis)
fun AgendaItem.Event.toEventDTOUpdate(): EventDTO.Update {
    return EventDTO.Update(
        id = id,
        title = title,
        description = description,
        from = from.toUtcMillis(),
        to = to.toUtcMillis(),
        remindAt = remindAt.toUtcMillis(),
        isGoing = isGoing ?: false,
        attendeeIds = attendeeIds,
        deletedPhotoIds = deletedPhotoKeys,
    )
}

// from Domain to EventDTO.Response (also converts local ZonedDateTime to UTC time millis)
// Note: this is used for fake implementations internally. It is not used in the normal app.
fun AgendaItem.Event.toEventDTOResponse(): EventDTO.Response {
    return EventDTO.Response(
        id = id,
        title = title,
        description = description,
        from = from.toUtcMillis(),
        to = to.toUtcMillis(),
        remindAt = remindAt.toUtcMillis(),
        isGoing = isGoing ?: false,
        photos = photos.map { it.toDTO() },
        attendees = attendees.map { it.toDTO() },
        isUserEventCreator = isUserEventCreator ?: false,
        host = host ?: "",
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
        photoIds = listOf("photoId1", "photoId2"),
        deletedPhotoKeys = listOf("deletedPhotoId1", "deletedPhotoId2"),
    )
    val eventDTO = event.toEventDTOCreate()
    val eventEntity = event.toEntity()
    val eventDomain = eventEntity.toDomain()
    val eventDomain2 = eventDTO.toDomain()

    println(event)
    println(eventDTO == event.toEventDTOCreate())
    println(eventEntity == event.toEntity())
    println(eventDomain == eventEntity.toDomain())
    println(eventDomain2 == eventDTO.toDomain())
}