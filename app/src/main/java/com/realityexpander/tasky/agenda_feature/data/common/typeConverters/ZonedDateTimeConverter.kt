package com.realityexpander.tasky.agenda_feature.data.common.typeConverters

import androidx.room.TypeConverter
import java.time.ZonedDateTime

class ZonedDateTimeConverter {
    @TypeConverter
    fun fromZonedDateTime(value: ZonedDateTime?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toZonedDateTime(value: String?): ZonedDateTime? {
        return value?.let { ZonedDateTime.parse(it) }
    }
}