package com.realityexpander.tasky.agenda_feature.data.common.typeConverters

import androidx.room.TypeConverter
import com.realityexpander.tasky.core.util.EpochSecond
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class ZonedDateTimeTypeConverter {
    @TypeConverter
    fun fromZonedDateTime(value: ZonedDateTime?): EpochSecond? {  // saves as a Long (UTC epochSeconds) in DB Tables
        return value?.toInstant()
            ?.atZone(ZoneOffset.UTC)
            ?.truncatedTo(ChronoUnit.MINUTES)
            ?.toEpochSecond()
    }
    @TypeConverter
    fun toZonedDateTime(value: EpochSecond?): ZonedDateTime? {   // restores from a Long (UTC epochSeconds) in DB Tables
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


// LEFT FOR REFERENCE
// Room database does query comparisons in the SQL database based on primitives only. So, if you
// use a custom type, you need to convert it to a primitive type that Room will use to make
// comparisons in the SQL queries. For dates this is always going to be a long.
//
// For example, if you have a custom type called Date, you should convert it to a long value that
// represents the number of milliseconds since January 1, 1970, 00:00:00 GMT. Then, Room will use
// the long value to compare dates in queries.
//
// If you don't convert the custom type to a primitive type that Room can compare numerically
// (like a string), Room will compare the string representation of the custom type, which is
// not what you want.
//
// https://developer.android.com/training/data-storage/room/referencing-data
//
// This shows the wrong approach to converting a custom type that needs to be compared using a Room query.
// In this incorrect example, the custom type is a Date object. The Date object is converted to a string.
// Room uses the string representation of the Date object to compare dates. This is not what you want.
//class ZonedDateTimeTypeConverter2 {
//    @TypeConverter
//    fun fromZonedDateTime(value: ZonedDateTime?): ZonedDateTimeStr? {  // saves as a String (ZonedDateTime Format) in DB Tables
//        return value?.toInstant()
//            ?.atZone(ZoneOffset.UTC)
//            ?.truncatedTo(ChronoUnit.MINUTES)
//            ?.toString()
//    }
//
//    @TypeConverter
//    fun toZonedDateTime(value: ZonedDateTimeStr?): ZonedDateTime? {   // restores from a String (ZonedDateTime Format) in DB Tables
//        return value?.let {
//            ZonedDateTime.ofInstant(
//                Instant.from(
//                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'")
//                        .withZone(ZoneId.of("UTC"))
//                        .parse(value)
//                ),
//                ZoneId.systemDefault()  // convert to current local time zone
//            )
//        }
//    }
//}
