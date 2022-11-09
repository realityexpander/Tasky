package com.realityexpander.tasky.agenda_feature.data.common.typeConverters

import androidx.room.TypeConverter
import com.realityexpander.tasky.core.util.UuidStr

class UuidStrListConverter {
    @TypeConverter
    fun fromList(value: List<UuidStr>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toList(value: String?): List<UuidStr>? {
        return value?.split(",")?.map { it }
    }
}