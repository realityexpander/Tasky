package com.realityexpander.tasky

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.realityexpander.tasky.agenda_feature.data.repositories.TaskyDatabase
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.EventEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.IEventDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZoneId
import java.time.ZonedDateTime

@RunWith(AndroidJUnit4::class)
open class EventsDaoTest {

    private lateinit var taskyDatabase: TaskyDatabase
    private lateinit var eventDao: IEventDao

    @Before
    fun initDb() {
        taskyDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            TaskyDatabase::class.java
        ).build()

        eventDao = taskyDatabase.eventDao()
    }

    @After
    fun closeDb() {
        taskyDatabase.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun createEvent_SavesData() {

        // ARRANGE
        val event = EventEntity(
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

        runTest {
            // ACT
            eventDao.createEvent(event)

            // ASSERT
            val events = eventDao.getEvents()
            assert(events.isNotEmpty())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getEventById_ReturnsEvent() {

        // ARRANGE
        val expectedId = "1"
        val expectedTitle = "Event 1"
        val event = EventEntity(
            expectedId,
            expectedTitle,
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

        runTest {
            // ARRANGE
            eventDao.createEvent(event)

            // ACT
            val eventResult = eventDao.getEventById("1")

            // ASSERT
            assert(eventResult != null)
            assert(eventResult?.id == expectedId)
            assert(eventResult?.title == expectedTitle)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateEvent_UpdatesData() {

        // ARRANGE
        val expectedTitle = "Event 1 UPDATED"
        val event = EventEntity(
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

        runTest {
            // ARRANGE
            eventDao.createEvent(event)

            // ACT
            eventDao.updateEvent(event.copy(title = expectedTitle))

            // ASSERT
            val events = eventDao.getEvents()
            assert(events[0].title == expectedTitle)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun clearAllEvents_DeletesAllEventData() {

        // ARRANGE
        val event1 = EventEntity(
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
        val event2 = EventEntity(
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

        runTest {
            // ARRANGE
            eventDao.createEvent(event1)
            eventDao.createEvent(event2)

            // ACT
            eventDao.clearAllEvents()

            // ASSERT
            val events = eventDao.getEvents()
            assert(events.isEmpty())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun markEventDeletedById_MarksEventDataAsDeleted() {

        // ARRANGE
        val event1 = EventEntity(
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
        val event2 = EventEntity(
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

        runTest {
            // ARRANGE
            eventDao.createEvent(event1)
            eventDao.createEvent(event2)

            // ACT
            eventDao.markEventDeletedById("1")

            // ASSERT
            val eventsAll = eventDao.getAllEvents()
            assert(eventsAll.size == 2)
            assert(eventsAll[0].isDeleted)

            val events = eventDao.getEvents()
            assert(events.size == 1)
            assert(events[0].id == "2")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteFinallyByEventIds_eventDataIsCompletelyDeleted() {

        // ARRANGE
        val expectedDeletedEventId = "1"
        val event1 = EventEntity(
            expectedDeletedEventId,
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
        val event2 = EventEntity(
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

        runTest {
            // ARRANGE
            eventDao.createEvent(event1)
            eventDao.createEvent(event2)
            eventDao.markEventDeletedById(expectedDeletedEventId)

            // ACT
            val markedDeletedEventIds = eventDao.getMarkedDeletedEventIds()
            eventDao.deleteFinallyByEventIds(markedDeletedEventIds)

            // ASSERT
            assert(markedDeletedEventIds.size == 1)
            assert(markedDeletedEventIds[0] == expectedDeletedEventId)
            val eventsAll = eventDao.getAllEvents()
            assert(eventsAll.size == 1)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteEvent_CompletelyDeletesEventData() {

        // ARRANGE
        val expectedRemainingEventId = "2"
        val event1 = EventEntity(
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
        val event2 = EventEntity(
            expectedRemainingEventId,
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

        runTest {
            // ARRANGE
            eventDao.createEvent(event1)
            eventDao.createEvent(event2)

            // ACT
            eventDao.deleteEvent(event1)

            // ASSERT
            val deletedEventIds = eventDao.getMarkedDeletedEventIds()
            assert(deletedEventIds.isEmpty())

            val eventsAll = eventDao.getAllEvents()
            assert(eventsAll.size == 1)
            assert(eventsAll[0].id == expectedRemainingEventId)
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getEventsForDay_OnlyShowsEventsForTheCurrentDay() {

        // ARRANGE
        val expectedForDayEventId = "1"
        val today = ZonedDateTime.of(
            2021, 1, 1,
            0, 0, 0, 0,
            ZoneId.systemDefault()
        )

        val event1 = EventEntity(
            expectedForDayEventId,
            "Event 1",
            "Event 1 description",
            from = today.plusHours(3),
            to = today.plusHours(4),
            remindAt = today.plusHours(2),
            host = "Host 1",
            isUserEventCreator = true,
            isGoing = true,
            attendeeIds = listOf("1", "2", "3"),
            photos = listOf("photo1", "photo2", "photo3"),
            deletedPhotoKeys = listOf(),
            isDeleted = false,
        )
        val event2 = EventEntity( // This event is not for today
            "2",
            "Event 2",
            "Event 2 description",
            from = today.plusDays(1).plusMinutes(0),
            to = today.plusDays(1).plusMinutes(5),
            remindAt = today.plusDays(1).plusMinutes(0),
            host = "Host 2",
            isUserEventCreator = true,
            isGoing = true,
            attendeeIds = listOf("1", "2", "3"),
            photos = listOf("photo1", "photo2", "photo3"),
            deletedPhotoKeys = listOf(),
            isDeleted = false,
        )
        val event3 = EventEntity(  // This event is not for today
            "3",
            "Event 3",
            "Event 3 description",
            from = today.minusDays(1),
            to = today.minusDays(1),
            remindAt = today.minusDays(1),
            host = "Host 2",
            isUserEventCreator = true,
            isGoing = true,
            attendeeIds = listOf("1", "2", "3"),
            photos = listOf("photo1", "photo2", "photo3"),
            deletedPhotoKeys = listOf(),
            isDeleted = false,
        )

        runTest {
            // ARRANGE
            eventDao.createEvent(event1)
            eventDao.createEvent(event2)
            eventDao.createEvent(event3)

            // ACT
            val eventsForDay = eventDao.getEventsForDay(today)

            // ASSERT
            assert(eventsForDay.size == 1)
            assert(eventsForDay[0].id == expectedForDayEventId)
        }
    }
}