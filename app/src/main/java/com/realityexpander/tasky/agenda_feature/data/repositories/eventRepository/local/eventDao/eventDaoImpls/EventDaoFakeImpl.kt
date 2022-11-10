package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.eventDaoImpls

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.EventEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.IEventDao
import com.realityexpander.tasky.agenda_feature.util.EventId
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
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

    // returns flow of events that are *NOT* marked as deleted.
    override fun getEventsFlow(): Flow<List<EventEntity>> {
        return getAllEventsFlowInFakeDatabase()
    }

    override suspend fun getEventById(eventId: EventId): EventEntity? {
        return getEventByIdInFakeDatabase(eventId)
    }

    // returns only the events that are *NOT* marked as deleted.
    override suspend fun getEvents(): List<EventEntity> {
        return getEventsInFakeDatabase()
    }

    // returns all events, including the deleted ones.
    override suspend fun getAllEvents(): List<EventEntity> {
        return getAllEventsInFakeDatabase()
    }


    // • UPDATE

    override suspend fun updateEvent(event: EventEntity): Int {
        return try {
            return updateEventInFakeDatabase(event)
        } catch (e: Exception) {
            0
        }
    }


    // • DELETE

    // Only marks the event as deleted
    override suspend fun markEventDeletedById(eventId: EventId): Int {
        return try {
            return markEventDeletedByIdInFakeDatabase(eventId)
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun deleteFinallyByEventIds(eventIds: List<EventId>): Int {
        return try {
            return deleteFinallyByEventIdsInFakeDatabase(eventIds)
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun deleteEvent(event: EventEntity): Int {
        return try {
            return deleteEventInFakeDatabase(event)
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun getMarkedDeletedEventIds(): List<EventId> {
        return getMarkedDeletedEventIdsInFakeDatabase()
    }

    override suspend fun clearAllEvents(): Int {
        return try {
            return clearAllEventsInFakeDatabase()
        } catch (e: Exception) {
            0
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// FAKE DATABASE //////////////////////////////////

    private val eventsDBTable = mutableListOf<EventEntity>()
    private val eventsDBTableFlow = MutableStateFlow<List<EventEntity>>(emptyList())

    // • CREATE

    private suspend fun createEventInFakeDatabase(event: EventEntity) {
        eventsDBTable.add(event)
        eventsDBTableFlow.update { eventsDBTable.toList() }
    }


    // • READ

    private suspend fun getEventsForDayInFakeDatabase(zonedDateTime: ZonedDateTime): List<EventEntity> {
        return eventsDBTable.filter {
            (
                ( it.from.toLocalDate() >= zonedDateTime.toLocalDate()
                  && it.from.toLocalDate() < zonedDateTime.toLocalDate().plusDays(1)
                )
             || ( it.to.toLocalDate() >= zonedDateTime.toLocalDate()
                  && it.to.toLocalDate() < zonedDateTime.toLocalDate().plusDays(1)
                )
            ) && !it.isDeleted
        }
    }

    private fun getAllEventsFlowInFakeDatabase(): Flow<List<EventEntity>> {
        return eventsDBTableFlow
            .map { events ->
                events.filter { event ->
                    !(event.isDeleted ?: false)
                }
            }
    }

    private suspend fun getEventByIdInFakeDatabase(eventId: EventId): EventEntity? {
        return eventsDBTable.find {
            it.id == eventId && !it.isDeleted
        }
    }

    // Gets all events except the deleted ones
    private suspend fun getEventsInFakeDatabase(): List<EventEntity> {
        return eventsDBTable
            .filter { event ->
                !event.isDeleted
            }
    }

    // Gets all events including the deleted ones
    private suspend fun getAllEventsInFakeDatabase(): List<EventEntity> {
        return eventsDBTable
    }


    // • UPDATE

    private suspend fun updateEventInFakeDatabase(event: EventEntity): Int {
        val index = eventsDBTable.indexOfFirst { it.id == event.id }
        if (index == -1) return 0

        eventsDBTable[index] = event
        eventsDBTableFlow.update { eventsDBTable.toMutableList().toList() }
        return 1
    }


    // • DELETE

    // only marks the event as deleted
    private suspend fun markEventDeletedByIdInFakeDatabase(eventId: EventId): Int {
        val index = eventsDBTable.indexOfFirst { it.id == eventId }
        if (index == -1) return 0

        eventsDBTable[index] = eventsDBTable[index].copy(isDeleted = true)
        eventsDBTableFlow.update { eventsDBTable.toList() }
        return 1
    }

    private suspend fun deleteFinallyByEventIdsInFakeDatabase(eventIds: List<EventId>): Int {
        val eventIdsDeleteSize = eventIds.size

        eventsDBTable.removeAll {
            eventIds.contains(it.id)
        }
        eventsDBTableFlow.update { eventsDBTable.toList() }

        return eventIdsDeleteSize
    }

    private suspend fun getMarkedDeletedEventIdsInFakeDatabase(): List<EventId> {
        return eventsDBTable.filter {
            it.isDeleted
        }.map {
            it.id
        }.also{
        }
    }

    private suspend fun deleteEventInFakeDatabase(event: EventEntity): Int {
        val index = eventsDBTable.indexOfFirst { it.id == event.id }
        if (index == -1) return 0

        eventsDBTable.removeAt(index)
        eventsDBTableFlow.update { eventsDBTable.toList() }
        return 1
    }

    private suspend fun clearAllEventsInFakeDatabase(): Int {
        val eventsSize = eventsDBTable.size

        eventsDBTable.clear()
        eventsDBTableFlow.update { eventsDBTable.toList() }

        return eventsSize
    }


}




///////////////////////////////////////////////////////////////////////////////////
// Local Testing

@OptIn(DelicateCoroutinesApi::class)
fun main() {

    val db = EventDaoFakeImpl()


    // Test Database Flow
    runBlocking {

        GlobalScope.launch {
            db.getEventsFlow().collect {
                println("EntityDBTable Flow: ${it.map { it.title }}")
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
            )
        )

        delay(400)

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
            )
        )

        delay(100)

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
            )
        )

        delay(100)
        println()

        print(".updateEvent(eventId=4) -> ")
        db.updateEvent(
            EventEntity(
                "4",
                "Event 4 - updated",
                "Description updated",
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
            )
        )

        delay(100)
        println()

        print(".deleteEventById(eventId=4) -> ")
        db.markEventDeletedById("4")

        val deletedEventIds =
            db.getMarkedDeletedEventIds().also { eventIds ->
                println("EventIds Marked as Deleted: $eventIds")
            }

        delay(100)
        println()

        print(".deleteFinallyByEventIds(deletedEventIds) -> ")
        db.deleteFinallyByEventIds(deletedEventIds)

        delay(100)

        db.getMarkedDeletedEventIds().also { eventIds ->
            println("EventIds Marked as Deleted: $eventIds")
        }

        println()
        print(".clearAllEvents() -> ")
        db.clearAllEvents()

        delay(2000)

    }
}
