package com.realityexpander.tasky.agenda_feature.data.common.typeConverters

import androidx.room.TypeConverter
import com.realityexpander.tasky.core.util.UtcSeconds
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class ZonedDateTimeTypeConverter {
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
    val zonedDateTimeTypeConverter = ZonedDateTimeTypeConverter()
    val zonedDateTimeLong = zonedDateTimeTypeConverter.fromZonedDateTime(zonedDateTime)
    val zonedDateTime2 = zonedDateTimeTypeConverter.toZonedDateTime(zonedDateTimeLong)
    val zonedDateTime2Long = zonedDateTimeTypeConverter.fromZonedDateTime(zonedDateTime2)

    println(zonedDateTime)
    println(zonedDateTimeLong)
    println(zonedDateTime2)
    println(zonedDateTime2Long)
    println(zonedDateTime2 == zonedDateTime)
    println(zonedDateTime2Long == zonedDateTimeLong)

    println()

    val nycNow = ZonedDateTime.now(ZoneId.of("America/New_York"))
        .truncatedTo(ChronoUnit.MINUTES)
    val nycNowLong = zonedDateTimeTypeConverter.fromZonedDateTime(nycNow)
    val nycNow2 = zonedDateTimeTypeConverter.toZonedDateTime(nycNowLong)
    val nycNowLong2 = zonedDateTimeTypeConverter.fromZonedDateTime(nycNow2)
    println(nycNow)
    println(nycNowLong)
    println(nycNow2)
    println(nycNowLong2)
    println(nycNowLong == nycNowLong2)
}