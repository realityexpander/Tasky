package com.realityexpander.tasky.agenda_feature.data.common.typeConverters

import androidx.room.TypeConverter

class StringListTypeConverter {
    @TypeConverter
    fun fromList(value: List<String>?): String? {
//        if(value?.size == 0) return ""
        if(value?.isEmpty() == true) return ""

        return value?.joinToString(",")
    }

    @TypeConverter
    fun toList(value: String?): List<String>? {
        if(value=="") return emptyList()

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

    println(stringListTypeConverter.toList(null))
    println(stringListTypeConverter.fromList(null))
}
