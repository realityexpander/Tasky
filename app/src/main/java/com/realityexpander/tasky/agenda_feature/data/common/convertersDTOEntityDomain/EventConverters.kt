package com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain

import com.google.gson.GsonBuilder
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.EventEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.PhotoDTO
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.agenda_feature.domain.Photo
import com.realityexpander.tasky.agenda_feature.presentation.common.util.isUserIdGoingAsAttendee
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.toEpochMilli
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
        attendees = attendees.map { it.toEntity() },
        photos = photos
            .filterIsInstance<Photo.Remote>()  // don't save local photos (uri's are not valid after app restart)
            .map {
                it.toEntity()
            },
        deletedPhotoIds = deletedPhotoIds,
        isSynced = isSynced
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
        attendees = attendees.map { it.toDomain() },
        photos = photos.map { it.toDomain() },
        deletedPhotoIds = deletedPhotoIds,
        isSynced = isSynced
    )
}

// from DTO to Domain (also converts UTC time millis to local ZonedDateTime)
fun EventDTO.toDomain(): AgendaItem.Event {
    when (this) {
        is EventDTO.Create -> {
            throw java.lang.IllegalStateException("EventDTO.Create should not be converted to Domain")
        }
        is EventDTO.Update -> {
            throw java.lang.IllegalStateException("EventDTO.Update should not be converted to Domain")
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
                attendees = attendees.map { it.toDomain() },
                photos = photos.map { it.toDomain() },
                deletedPhotoIds = emptyList(),
                isSynced = true,
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
        from = from.toEpochMilli(),
        to = to.toEpochMilli(),
        remindAt = remindAt.toEpochMilli(),
        attendeeIds = attendees.map { attendee ->
            attendee.id
        },
        photos = photos
            .filterIsInstance<Photo.Local>()        // guarantee only upload Local photos
            .map { it.toDTO() },
    )
}

// from Domain to EventDTO.Update (also converts local ZonedDateTime to UTC time millis)
// Note: Automatically sets `isGoing` for `EventDTO.Update`based using authUserId == logged-in user id
fun AgendaItem.Event.toEventDTOUpdate(authUserId: UserId? = null): EventDTO.Update {
    return EventDTO.Update(
        id = id,
        title = title,
        description = description,
        from = from.toEpochMilli(),
        to = to.toEpochMilli(),
        remindAt = remindAt.toEpochMilli(),
        isGoing = isUserIdGoingAsAttendee(authUserId, attendees),
        attendeeIds = attendees.map { attendee ->
            attendee.id
        },
        photos = photos
            .filterIsInstance<Photo.Local>()        // guarantee only upload Local photos
            .map { it.toDTO() },
        deletedPhotoIds = deletedPhotoIds,
    )
}

// from Domain to EventDTO.Response (also converts local ZonedDateTime to UTC time millis)
//   Note: this is used for fake implementations internally. It is *NOT* used in the normal app.
fun AgendaItem.Event.toEventDTOResponse(): EventDTO.Response {
    return EventDTO.Response(
        id = id,
        title = title,
        description = description,
        from = from.toEpochMilli(),
        to = to.toEpochMilli(),
        remindAt = remindAt.toEpochMilli(),
        photos = photos
            .map { it.toDTO() }
            .filterIsInstance<PhotoDTO.Remote>(),        // guarantee only respond with Remote photos
        attendees = attendees.map { it.toDTO() },
        isUserEventCreator = isUserEventCreator,
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
    println("event == eventDTOtoDomainToEntityToDomain: ${gson.toJson(eventDTOtoDomainToEntityToDomain) == gson.toJson(event)}")


    // Simulate EventDTO.Create
    val eventDTO2 = event.toEventDTOCreate()
    try {
        val eventDTO2toDomain = eventDTO2.toDomain()
        println("eventDTO2toDomain: $eventDTO2toDomain") // should never get here
    } catch (e: Exception) {
        println("EventDTO.Create should not be converted to Domain = true")
    }

    // Simulate EventDTO.Update
    val eventDTO3 = event.toEventDTOUpdate()
    try {
        val eventDTO3toDomain = eventDTO3.toDomain()
        println("eventDTO3toDomain: $eventDTO3toDomain") // should never get here
    } catch (e: Exception) {
        println("EventDTO.Update should not be converted to Domain = true")
    }
}
