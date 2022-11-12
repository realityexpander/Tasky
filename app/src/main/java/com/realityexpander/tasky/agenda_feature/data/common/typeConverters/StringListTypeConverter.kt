package com.realityexpander.tasky.agenda_feature.data.common.typeConverters

import androidx.room.TypeConverter

class StringListTypeConverter {
    @TypeConverter
    fun fromList(value: List<String>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toList(value: String?): List<String>? {
        return value?.split(",")?.map { it }
    }
}

fun main() {
    val stringListTypeConverter = StringListTypeConverter()

    val stringList = listOf("1", "2", "3")
    val stringListStr = stringListTypeConverter.fromList(stringList)
    val stringList2 = stringListTypeConverter.toList(stringListStr)

    println(stringListStr)
    println(stringList2)

    println(stringList == stringList2)
}