package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.eventDaoImpls

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.EventEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.IEventDao
import com.realityexpander.tasky.agenda_feature.util.EventId
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.ZonedDateTime
import javax.inject.Inject

// Simulates a local database

class EventDaoFakeImpl @Inject constructor(): IEventDao {

    // • CREATE

    override suspend fun createEvent(event: EventEntity) {
        createEventInFakeDatabase(event)
    }


    // • READ

    override suspend fun getEventsForDay(zonedDateTime: ZonedDateTime): List<EventEntity> {
        return getEventsForDayInFakeDatabase(zonedDateTime)
    }

    override suspend fun getAllEventsFlow(): Flow<List<EventEntity>> {
        return getAllEventsFlowInFakeDatabase()
    }

    override suspend fun getEventById(eventId: EventId): EventEntity? {
        return getEventByIdInFakeDatabase(eventId)
    }

    override suspend fun getAllEvents(): List<EventEntity> {
        return getAllEventsInFakeDatabase()
    }


    // • UPDATE

    override suspend fun updateEvent(event: EventEntity): Boolean {
        return try {
            updateEventInFakeDatabase(event)
            true
        } catch (e: Exception) {
            false
        }
    }

    // • DELETE

    // Only marks the event as deleted
    override suspend fun deleteEventById(eventId: EventId): Boolean {
        return try {
            deleteEventByIdInFakeDatabase(eventId)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteFinallyByEventIds(eventIds: List<EventId>): Boolean {
        return try {
            deleteFinallyByEventIdsInFakeDatabase(eventIds)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getDeletedEventIds(): List<EventId> {
        return getDeletedEventIdsInFakeDatabase()
    }

    override suspend fun clearAllEvents(): Boolean {
        return try {
            clearAllEventsInFakeDatabase()
            true
        } catch (e: Exception) {
            false
        }
    }

    ////////////////////////////////// FAKE DATABASE //////////////////////////////////

    private val events = mutableListOf<EventEntity>()
    private val eventsFlow = flow {
        var lastEvents= emptyList<EventEntity>()
        while(true) {
            if(!lastEvents.containsAll(events)) {
                lastEvents = events.toList()
                emit(events)
            }
            delay(100)
        }
    }

    // • CREATE

    private suspend fun createEventInFakeDatabase(event: EventEntity) {
        events.add(event)
    }


    // • READ

    private suspend fun getEventsForDayInFakeDatabase(zonedDateTime: ZonedDateTime): List<EventEntity> {
        return events.filter {
            ( it.from.toLocalDate() >= zonedDateTime.toLocalDate()
              || it.to.toLocalDate() <= zonedDateTime.toLocalDate()
            ) && !it.isDeleted
        }
    }

    private suspend fun getAllEventsFlowInFakeDatabase(): Flow<List<EventEntity>> {
        return eventsFlow
            .map { events ->
                events.filter { event ->
                    !event.isDeleted
                }
            }
    }

    private suspend fun getEventByIdInFakeDatabase(eventId: EventId): EventEntity? {
        return events.find {
            it.id == eventId && !it.isDeleted
        }
    }

    private suspend fun getAllEventsInFakeDatabase(): List<EventEntity> {
        return events
            .filter { event ->
                !event.isDeleted
            }
    }


    // • UPDATE

    private suspend fun updateEventInFakeDatabase(event: EventEntity) {
        val index = events.indexOfFirst { it.id == event.id }
        if (index == -1) return

        events[index] = event
    }


    // • DELETE

    private suspend fun deleteEventByIdInFakeDatabase(eventId: EventId) {
        val index = events.indexOfFirst { it.id == eventId }
        if (index == -1) return

        events[index] = events[index].copy(isDeleted = true)
    }

    private suspend fun deleteFinallyByEventIdsInFakeDatabase(eventIds: List<EventId>) {
        events.removeAll {
            eventIds.contains(it.id)
        }
    }

    private suspend fun getDeletedEventIdsInFakeDatabase(): List<EventId> {
        return events.filter {
            it.isDeleted
        }.map {
            it.id
        }.also{
        }
    }

    private suspend fun clearAllEventsInFakeDatabase() {
        events.clear()
    }


}




///////////////////////////////////////////////////////////////////////////////////

// Local Testing

fun main() {

    val db = EventDaoFakeImpl()


    runBlocking {

        //CoroutineScope(coroutineContext).apply {
        val job = launch {
            db.getAllEventsFlow().collect {
                println("Flow: ${it.map { it.title }}")
            }
        }

        db.createEvent(
            EventEntity(
                "1",
                "Event 1",
                "2021-01-01T00:00:00.000Z",
                from = ZonedDateTime.now(),
                to = ZonedDateTime.now(),
                remindAt = ZonedDateTime.now(),
                host = "Host 1",
                isUserEventCreator = true,
                isGoing = true,
                attendeeIds = listOf("1", "2", "3"),
                photos = listOf("photo1", "photo2", "photo3"),
                deletedPhotoKeys = listOf(),
                isDeleted = false,
                isUploaded = false
            )
        )

        db.createEvent(
            EventEntity(
                "2",
                "Event 2",
                "2021-01-01T00:00:00.000Z",
                from = ZonedDateTime.now(),
                to = ZonedDateTime.now(),
                remindAt = ZonedDateTime.now(),
                host = "Host 2",
                isUserEventCreator = true,
                isGoing = true,
                attendeeIds = listOf("1", "2", "3"),
                photos = listOf("photo1", "photo2", "photo3"),
                deletedPhotoKeys = listOf(),
                isDeleted = false,
                isUploaded = false
            )
        )

        db.createEvent(
            EventEntity(
                "3",
                "Event 3",
                "2021-01-01T00:00:00.000Z",
                from = ZonedDateTime.now(),
                to = ZonedDateTime.now(),
                remindAt = ZonedDateTime.now(),
                host = "Host 3",
                isUserEventCreator = true,
                isGoing = true,
                attendeeIds = listOf("1", "2", "3"),
                photos = listOf("photo1", "photo2", "photo3"),
                deletedPhotoKeys = listOf(),
                isDeleted = false,
                isUploaded = false
            )
        )

        delay(500)

        db.createEvent(
            EventEntity(
                "4",
                "Event 4",
                "2021-01-01T00:00:00.000Z",
                from = ZonedDateTime.now(),
                to = ZonedDateTime.now(),
                remindAt = ZonedDateTime.now(),
                host = "Host 4",
                isUserEventCreator = true,
                isGoing = true,
                attendeeIds = listOf("1", "2", "3"),
                photos = listOf("photo1", "photo2", "photo3"),
                deletedPhotoKeys = listOf(),
                isDeleted = false,
                isUploaded = false
            )
        )

        delay(1000)
        job.cancelAndJoin()

    }
}
