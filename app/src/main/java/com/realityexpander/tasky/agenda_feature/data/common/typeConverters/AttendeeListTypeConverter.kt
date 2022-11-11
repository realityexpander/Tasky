package com.realityexpander.tasky.agenda_feature.data.common.typeConverters

import androidx.room.TypeConverter
import com.realityexpander.tasky.agenda_feature.domain.Attendee
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AttendeeListTypeConverter {

    @TypeConverter
    fun fromAttendeeList(value: List<Attendee>?): String? {
        return value?.let {
            Json.encodeToString(it)
        }
    }

    @TypeConverter
    fun toAttendeeList(value: String?): List<Attendee>? {
        return value?.let {
            Json.decodeFromString(it)
        }
    }
}


// Local testing
fun main() {
    val attendeeListConverter = AttendeeListTypeConverter()

    val attendeeList = listOf(
        Attendee(
            id = "1",
            eventId = "1",
            email = "1",
            fullName = "1",
            isGoing = true,
            remindAt = 1,
            photo = "1",
        ),
        Attendee(
            id = "2",
            eventId = "2",
            email = "2",
            fullName = "2",
            isGoing = true,
            remindAt = 2,
            photo = "2",
        ),
        Attendee(
            id = "3",
            eventId = "3",
            email = "3",
            fullName = "3",
            isGoing = true,
            remindAt = 3,
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