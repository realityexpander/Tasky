package com.realityexpander.tasky.agenda_feature.data.common.typeConverters

import androidx.room.TypeConverter
import com.realityexpander.tasky.core.util.UtcSeconds
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class ZonedDateTimeConverter {
    @TypeConverter
    fun fromZonedDateTime(value: ZonedDateTime?): UtcSeconds? {  // saves as a Long (UTC epochSeconds) in DB Tables
        return value?.toInstant()
            ?.atZone(ZoneOffset.UTC)
            ?.truncatedTo(ChronoUnit.MINUTES)
            ?.toEpochSecond()
    }

    @TypeConverter
    fun toZonedDateTime(value: UtcSeconds?): ZonedDateTime? {   // restores from a Long (UTC epochSeconds) in DB Tables
        return value?.let {
            ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(value),
                ZoneId.systemDefault()  // convert to current local time zone
            )
        }
    }
}


//// Local Test ////

fun main() {
    val zonedDateTime = ZonedDateTime.now()
        .truncatedTo(ChronoUnit.MINUTES)
    val zonedDateTimeConverter = ZonedDateTimeConverter()
    val zonedDateTimeLong = zonedDateTimeConverter.fromZonedDateTime(zonedDateTime)
    val zonedDateTime2 = zonedDateTimeConverter.toZonedDateTime(zonedDateTimeLong)
    val zonedDateTime2Long = zonedDateTimeConverter.fromZonedDateTime(zonedDateTime2)

    println(zonedDateTime)
    println(zonedDateTimeLong)
    println(zonedDateTime2)
    println(zonedDateTime2Long)
    println(zonedDateTime2 == zonedDateTime)
    println(zonedDateTime2Long == zonedDateTimeLong)

    println()

    val nycNow = ZonedDateTime.now(ZoneId.of("America/New_York"))
        .truncatedTo(ChronoUnit.MINUTES)
    val nycNowLong = zonedDateTimeConverter.fromZonedDateTime(nycNow)
    val nycNow2 = zonedDateTimeConverter.toZonedDateTime(nycNowLong)
    val nycNowLong2 = zonedDateTimeConverter.fromZonedDateTime(nycNow2)
    println(nycNow)
    println(nycNowLong)
    println(nycNow2)
    println(nycNowLong2)
    println(nycNowLong == nycNowLong2)
}