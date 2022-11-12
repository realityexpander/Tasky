package com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain

import com.google.gson.GsonBuilder
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.EventEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.PhotoDTO
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.agenda_feature.domain.Photo
import com.realityexpander.tasky.core.util.toUtcMillis
import com.realityexpander.tasky.core.util.toZonedDateTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

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
        attendees = attendees.map { it.toEntity() },
        photos = photos.map { it.toEntity() },
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
        attendees = attendees.map { it.toDomain() },
        photos = photos.map { it.toDomain() },
        deletedPhotoKeys = deletedPhotoKeys,
    )
}

// from DTO to Domain (also converts UTC time millis to local ZonedDateTime)
fun EventDTO.toDomain(): AgendaItem.Event {
    when (this) {
        is EventDTO.Create -> {
            throw java.lang.IllegalStateException("EventDTO.Create should not be converted to Domain")
//            return AgendaItem.Event(
//                id = id,
//                title = title,
//                description = description,
//                from = from.toZonedDateTime(),
//                to = to.toZonedDateTime(),
//                remindAt = remindAt.toZonedDateTime(),
//
//                attendeeIds = attendeeIds,
//            )
        }
        is EventDTO.Update -> {
            throw java.lang.IllegalStateException("EventDTO.Update should not be converted to Domain")
//            return AgendaItem.Event(
//                id = id,
//                title = title,
//                description = description,
//                from = from.toZonedDateTime(),
//                to = to.toZonedDateTime(),
//                remindAt = remindAt.toZonedDateTime(),
//
//                isGoing = isGoing,
//                attendeeIds = attendees.map { it.id },
//                deletedPhotoKeys = deletedPhotoIds,
//            )
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
//                attendeeIds = attendees.map { it.id },      // extract the attendees Ids
                photos = photos.map { it.toDomain() },
//                photoIds = photos.map { it.id },            // extract the photo Ids
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
        attendeeIds = attendees.map { it.id },
        photos = photosToUpload.map {
                PhotoDTO(
                    it.id,
                    uri = it.uri
                )
            }
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
        attendeeIds = attendees.map { it.id },
        deletedPhotoIds = deletedPhotoKeys,
    )
}

// from Domain to EventDTO.Response (also converts local ZonedDateTime to UTC time millis)
//   Note: this is used for fake implementations internally. It is not used in the normal app.
fun AgendaItem.Event.toEventDTOResponse(): EventDTO.Response {
    return EventDTO.Response(
        id = id,
        title = title,
        description = description,
        from = from.toUtcMillis(),
        to = to.toUtcMillis(),
        remindAt = remindAt.toUtcMillis(),
        isGoing = isGoing ?: false,
        photos = photos.map {
                    it.toDTO()
                },
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
        from = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES),
        to = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES),
        remindAt = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES),
        host = "host",
        isUserEventCreator = true,
        isGoing = true,
        attendees = listOf(
            Attendee(
                id = "id1",
                email = "email",
                photo = "photoUrl",
                fullName = "fullName",
            ),
            Attendee(
                id = "id2",
                email = "email2",
                photo = "photoUrl2",
                fullName = "fullName2",
            ),
        ),
        photos = listOf(
            Photo.Remote(
                id = "id1",
                url = "url1",
            ),
            Photo.Remote(
                id = "id2",
                url = "url2",
            ),
        ),
    )

    val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    // Simulate EventDTO.Response
    val eventDTO = event.toEventDTOResponse()
    val eventDTOtoDomain = eventDTO.toDomain()
    val eventDTOtoDomainToEntity = eventDTOtoDomain.toEntity()
    val eventDTOtoDomainToEntityToDomain = eventDTOtoDomainToEntity.toDomain()

    //println(gson.toJson(event))
    println(gson.toJson(eventDTOtoDomainToEntityToDomain) == gson.toJson(event))


    // Simulate EventDTO.Create
    val eventDTO2 = event.toEventDTOCreate()
    try {
        val eventDTO2toDomain = eventDTO2.toDomain()
    } catch (e: Exception) {
        println("EventDTO.Create should not be converted to Domain = true")
    }

    // Simulate EventDTO.Update
    val eventDTO3 = event.toEventDTOUpdate()
    try {
        val eventDTO3toDomain = eventDTO3.toDomain()
    } catch (e: Exception) {
        println("EventDTO.Update should not be converted to Domain = true")
    }
}