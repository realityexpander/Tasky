package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.eventDaoImpls

import com.realityexpander.tasky.agenda_feature.domain.EventId
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.IEventDao
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.AttendeeEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.EventEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.PhotoEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

// Simulates a local database

class EventDaoFakeImpl @Inject constructor(): IEventDao {

    // • CREATE

    override suspend fun createEvent(event: EventEntity) {
        createEventInFakeDatabase(event)
    }

    override suspend fun upsertEvent(event: EventEntity) {
//        createEventInFakeDatabase(event)  // todo implement
    }

    // • READ

    override suspend fun getEventsForDay(zonedDateTime: ZonedDateTime): List<EventEntity> {
        return getEventsForDayInFakeDatabase(zonedDateTime)
    }

    override fun getEventsForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<EventEntity>> {
        return getEventsForDayFlowInFakeDatabase(zonedDateTime)
    }

    // returns flow of events that are *NOT* marked as deleted.
    override fun getEventsFlow(): Flow<List<EventEntity>> {
        return getAllEventsFlowInFakeDatabase()
    }

    override fun getLocalEventsForRemindAtDateTimeRangeFlow(
        startDateTime: ZonedDateTime,
        endDateTime: ZonedDateTime
    ): Flow<List<EventEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getEventById(eventId: EventId): EventEntity? {
        return getEventByIdInFakeDatabase(eventId)
    }

//    // returns only the events that are *NOT* marked as deleted.
//    override suspend fun getEvents(): List<EventEntity> {
//        return getEventsInFakeDatabase()
//    }

    // returns all events, including the deleted ones.
    override suspend fun getEvents(): List<EventEntity> {
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

    override suspend fun deleteEventById(eventId: EventId): Int {
        return try {
            return deleteFinallyByEventIdsInFakeDatabase(listOf(eventId))
        } catch (e: Exception) {
            0
        }
    }


    // • DELETE

//    // Only marks the event as deleted
//    override suspend fun markEventDeletedById(eventId: EventId): Int {
//        return try {
//            return markEventDeletedByIdInFakeDatabase(eventId)
//        } catch (e: Exception) {
//            0
//        }
//    }

    override suspend fun deleteByEventIds(eventIds: List<EventId>): Int {
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

//    override suspend fun getMarkedDeletedEventIds(): List<EventId> {
//        return getMarkedDeletedEventIdsInFakeDatabase()
//    }

    override suspend fun clearAllEvents(): Int {
        return try {
            return clearAllEventsInFakeDatabase()
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun clearAllSyncedEventsForDay(zonedDateTime: ZonedDateTime): Int {
        return try {
            0
//            clearAllEventsForDayInFakeDatabase(zonedDateTime)  // todo implement
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
        return eventsDBTable.filter { entity ->
            isEventVisibleForDay(entity, zonedDateTime)
        }
    }

    private fun getEventsForDayFlowInFakeDatabase(zonedDateTime: ZonedDateTime): Flow<List<EventEntity>> {
        return eventsDBTableFlow.map { events ->
            events.filter { entity ->
                isEventVisibleForDay(entity, zonedDateTime)

            }
        }
    }

    private fun isEventVisibleForDay(entity: EventEntity, zonedDateTime: ZonedDateTime): Boolean {
        return (
//            !entity.isDeleted
//             &&
            (
                ( (entity.from >= zonedDateTime) && (entity.from   < zonedDateTime.plusDays(1) )) // -- event starts within day

//                ( (entity.from >= zonedDateTime) && (entity.to   < zonedDateTime.plusDays(1) )) // -- event fits within a day
//                ||
//                ( (entity.from >  zonedDateTime) && (entity.from < zonedDateTime.plusDays(1) )) // -- `from` starts today
//                ||
//                ( (entity.to   >  zonedDateTime) && (entity.to   < zonedDateTime.plusDays(1) )) // -- `to` ends today
//                ||
//                ( (entity.from <= zonedDateTime) && (entity.to   > zonedDateTime.plusDays(1) )) // -- event straddles today
            )
        )
    }

    private fun getAllEventsFlowInFakeDatabase(): Flow<List<EventEntity>> {
        return eventsDBTableFlow
            .map { events ->
//                events.filter { event ->
//                    !(event.isDeleted)
//                }
                events
            }
    }

    private suspend fun getEventByIdInFakeDatabase(eventId: EventId): EventEntity? {
        return eventsDBTable.find {
            it.id == eventId //&& !it.isDeleted
        }
    }

//    // Gets all events except the deleted ones
//    private suspend fun getEventsInFakeDatabase(): List<EventEntity> {
//        return eventsDBTable
//            .filter { event ->
//                !event.isDeleted
//            }
//    }

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

//    // only marks the event as deleted
//    private suspend fun markEventDeletedByIdInFakeDatabase(eventId: EventId): Int {
//        val index = eventsDBTable.indexOfFirst { it.id == eventId }
//        if (index == -1) return 0
//
//        eventsDBTable[index] = eventsDBTable[index].copy(isDeleted = true)
//        eventsDBTableFlow.update { eventsDBTable.toList() }
//        return 1
//    }

    private suspend fun deleteFinallyByEventIdsInFakeDatabase(eventIds: List<EventId>): Int {
        val eventIdsDeleteSize = eventIds.size

        eventsDBTable.removeAll {
            eventIds.contains(it.id)
        }
        eventsDBTableFlow.update { eventsDBTable.toList() }

        return eventIdsDeleteSize
    }

//    private suspend fun getMarkedDeletedEventIdsInFakeDatabase(): List<EventId> {
//        return eventsDBTable.filter {
//            it.isDeleted
//        }.map {
//            it.id
//        }.also{
//        }
//    }

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

    val dao = EventDaoFakeImpl()

    // Test Fake Database Flows
    runBlocking {

        val test = 0
//        val test = 1

        when(test) {
            0 -> runGeneralDBFlowTest(dao)
            1 -> runGetEventsForDayFlowTest(dao)
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
suspend fun runGetEventsForDayFlowTest(dao: IEventDao) {

    // Should only show Event 1 & 4

    val today = ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault())

    GlobalScope.launch {
        dao.getEventsForDayFlow(today).collect {
            println("EntityDBTable getEventsForDayFlow: ${it.map { it.title }}")
        }
    }

    dao.createEvent(
        EventEntity(
            "1",
            "Event 1",
            "Event 1 Description",
            from = today.plusHours(1),
            to = today.plusHours(2),
            remindAt = today.plusMinutes(30),
            host = "Host 1",
            isUserEventCreator = true,
            attendees = listOf(
                AttendeeEntity(
                    "1",
                    "Attendee 1",
                    "Email1@email.com",
                    "FullName",
                    isGoing = true,
                    remindAt = ZonedDateTime.now(),
                    photo = "https://www.google.com"
                ),
            ),
            photos = listOf(
                PhotoEntity("1", "https://www.google.com")
            ),
            deletedPhotoIds = listOf(),
//            isDeleted = false,
        )
    )

    delay(100)

    dao.createEvent(
        EventEntity(
            "2",
            "Event 2",
            "Event 2 Description",
            from = today.plusDays(1),
            to = today.plusDays(1).plusHours(1),
            remindAt = today.plusDays(1),
            host = "Host 2",
            isUserEventCreator = true,
            attendees = listOf(
                AttendeeEntity(
                    "1",
                    "Attendee 1",
                    "Email1@email.com",
                    "FullName",
                    isGoing = true,
                    remindAt = ZonedDateTime.now(),
                    photo = "https://www.google.com"
                ),
            ),
            photos = listOf(
                PhotoEntity("1", "https://www.google.com")
            ),
            deletedPhotoIds = listOf(),
//            isDeleted = false,
        )
    )

    dao.createEvent(
        EventEntity(
            "3",
            "Event 3",
            "Event 3 Description",
            from = today.minusDays(1).plusHours(2),
            to = today.minusDays(1).plusHours(3),
            remindAt = today.minusDays(1).plusHours(1),
            host = "Host 2",
            isUserEventCreator = true,
            attendees = listOf(
                AttendeeEntity(
                    "1",
                    "Attendee 1",
                    "Email1@email.com",
                    "FullName",
                    isGoing = true,
                    remindAt = ZonedDateTime.now(),
                    photo = "https://www.google.com"
                ),
            ),
            photos = listOf(
                PhotoEntity("1", "https://www.google.com")
            ),
            deletedPhotoIds = listOf(),
//            isDeleted = false,
        )
    )

    dao.createEvent(
        EventEntity(
            "4",
            "Event 4",
            "Event 4 Description",
            from = today.plusHours(10),
            to = today.plusHours(11),
            remindAt = today.plusMinutes(30),
            host = "Host 1",
            isUserEventCreator = true,
            attendees = listOf(
                AttendeeEntity(
                    "1",
                    "Attendee 1",
                    "Email1@email.com",
                    "FullName",
                    isGoing = true,
                    remindAt = ZonedDateTime.now(),
                    photo = "https://www.google.com"
                ),
            ),
            photos = listOf(
                PhotoEntity("1", "https://www.google.com")
            ),
            deletedPhotoIds = listOf(),
//            isDeleted = false,
        )
    )

    delay(1000)
}

@OptIn(DelicateCoroutinesApi::class)
suspend fun runGeneralDBFlowTest(dao: IEventDao) {
    GlobalScope.launch {
        dao.getEventsFlow().collect {
            println("EntityDBTable Flow: ${it.map { it.title }}")
        }
    }

    dao.createEvent(
        EventEntity(
            "1",
            "Event 1",
            "2021-01-01T00:00:00.000Z",
            from = ZonedDateTime.now(),
            to = ZonedDateTime.now(),
            remindAt = ZonedDateTime.now(),
            host = "Host 1",
            isUserEventCreator = true,
            attendees = listOf(
                AttendeeEntity(
                    "1",
                    "Attendee 1",
                    "Email1@email.com",
                    "FullName",
                    isGoing = true,
                    remindAt = ZonedDateTime.now(),
                    photo = "https://www.google.com"
                ),
            ),
            photos = listOf(
                PhotoEntity("1", "https://www.google.com")
            ),
            deletedPhotoIds = listOf(),
//            isDeleted = false,
        )
    )

    delay(400)

    dao.createEvent(
        EventEntity(
            "2",
            "Event 2",
            "2021-01-01T00:00:00.000Z",
            from = ZonedDateTime.now(),
            to = ZonedDateTime.now(),
            remindAt = ZonedDateTime.now(),
            host = "Host 2",
            isUserEventCreator = true,
            attendees = listOf(
                AttendeeEntity(
                    "1",
                    "Attendee 1",
                    "Email1@email.com",
                    "FullName",
                    isGoing = true,
                    remindAt = ZonedDateTime.now(),
                    photo = "https://www.google.com"
                ),
            ),
            photos = listOf(
                PhotoEntity("1", "https://www.google.com")
            ),
            deletedPhotoIds = listOf(),
//            isDeleted = false,
        )
    )

    dao.createEvent(
        EventEntity(
            "3",
            "Event 3",
            "2021-01-01T00:00:00.000Z",
            from = ZonedDateTime.now(),
            to = ZonedDateTime.now(),
            remindAt = ZonedDateTime.now(),
            host = "Host 3",
            isUserEventCreator = true,
            attendees = listOf(
                AttendeeEntity(
                    "1",
                    "Attendee 1",
                    "Email1@email.com",
                    "FullName",
                    isGoing = true,
                    remindAt = ZonedDateTime.now(),
                    photo = "https://www.google.com"
                ),
            ),
            photos = listOf(
                PhotoEntity("1", "https://www.google.com")
            ),
            deletedPhotoIds = listOf(),
//            isDeleted = false,
        )
    )

    delay(100)

    dao.createEvent(
        EventEntity(
            "4",
            "Event 4",
            "2021-01-01T00:00:00.000Z",
            from = ZonedDateTime.now(),
            to = ZonedDateTime.now(),
            remindAt = ZonedDateTime.now(),
            host = "Host 4",
            isUserEventCreator = true,
            attendees = listOf(
                AttendeeEntity(
                    "1",
                    "Attendee 1",
                    "Email1@email.com",
                    "FullName",
                    isGoing = true,
                    remindAt = ZonedDateTime.now(),
                    photo = "https://www.google.com"
                ),
            ),
            photos = listOf(
                PhotoEntity("1", "https://www.google.com")
            ),
            deletedPhotoIds = listOf(),
//            isDeleted = false,
        )
    )

    delay(100)
    println()

    print(".updateEvent(eventId=4) -> ")
    dao.updateEvent(
        EventEntity(
            "4",
            "Event 4 - updated",
            "Description updated",
            from = ZonedDateTime.now(),
            to = ZonedDateTime.now(),
            remindAt = ZonedDateTime.now(),
            host = "Host 4",
            isUserEventCreator = true,
            attendees = listOf(
                AttendeeEntity(
                    "1",
                    "Attendee 1",
                    "Email1@email.com",
                    "FullName",
                    isGoing = true,
                    remindAt = ZonedDateTime.now(),
                    photo = "https://www.google.com"
                ),
            ),
            photos = listOf(
                PhotoEntity("1", "https://www.google.com")
            ),
            deletedPhotoIds = listOf(),
//            isDeleted = false,
        )
    )

    delay(100)
    println()

    print(".deleteEventById(eventId=4) -> ")
    dao.deleteEventById("4")

//    val deletedEventIds =
//        dao.getMarkedDeletedEventIds().also { eventIds ->
//            println("EventIds Marked as Deleted: $eventIds")
//        }

    delay(100)
    println()

//    print(".deleteByEventIds(deletedEventIds) -> ")
//    dao.deleteByEventIds(deletedEventIds)

    delay(100)

//    dao.getMarkedDeletedEventIds().also { eventIds ->
//        println("EventIds Marked as Deleted: $eventIds")
//    }

    println()
    print(".clearAllEvents() -> ")
    dao.clearAllEvents()

    delay(2000)
}
