package com.realityexpander.tasky.agenda_feature.data.common.typeConverters

import androidx.room.TypeConverter
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.AttendeeEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.ZoneId
import java.time.ZonedDateTime

class AttendeeListTypeConverter {

    @TypeConverter
    fun fromAttendeeList(value: List<AttendeeEntity>?): String? {
        return value?.let {
            Json.encodeToString(it)
        }
    }

    @TypeConverter
    fun toAttendeeList(value: String?): List<AttendeeEntity>? {
        return value?.let {
            Json.decodeFromString(it)
        }
    }
}


// Local testing
fun main() {
    val attendeeListConverter = AttendeeListTypeConverter()

    val attendeeList = listOf(
        AttendeeEntity(
            id = "1",
            eventId = "1",
            email = "1",
            fullName = "1",
            isGoing = true,
            remindAt = ZonedDateTime.of(2021, 1, 1, 1, 1, 1, 1, ZoneId.of("UTC")),
            photo = "1",
        ),
        AttendeeEntity(
            id = "2",
            eventId = "2",
            email = "2",
            fullName = "2",
            isGoing = true,
            remindAt = ZonedDateTime.of(2022, 1, 1, 1, 1, 1, 1, ZoneId.of("UTC")),
            photo = "2",
        ),
        AttendeeEntity(
            id = "3",
            eventId = "3",
            email = "3",
            fullName = "3",
            isGoing = true,
            remindAt = ZonedDateTime.of(2023, 1, 1, 1, 1, 1, 1, ZoneId.of("UTC")),
            photo = "3",
        ),
    )
    val attendeeListStr = attendeeListConverter.fromAttendeeList(attendeeList)
    val attendeeList2 = attendeeListConverter.toAttendeeList(attendeeListStr)

    println(attendeeList)
    println(attendeeListStr)
    println(attendeeList2)

    println()
    println("attendeeList == attendeeList2: ${attendeeList == attendeeList2}")
}
