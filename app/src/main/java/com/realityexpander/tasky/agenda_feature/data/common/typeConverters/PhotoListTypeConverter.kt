package com.realityexpander.tasky.agenda_feature.data.common.typeConverters

import androidx.room.TypeConverter
import com.realityexpander.tasky.agenda_feature.domain.Photo
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PhotoListTypeConverter {
    @TypeConverter
    fun fromPhotoList(value: List<Photo>?): String? {
        return value?.let {
            Json.encodeToString(it)
        }
    }

    @TypeConverter
    fun toPhotoList(value: String?): List<Photo>? {
        return value?.let {
            Json.decodeFromString<List<Photo>>(it)
        }
    }
}


 // Local testing
fun main() {
    val photoListConverter = PhotoListTypeConverter()

    val photoList = listOf(
        Photo(
            id = "1",
            url = "1",
        ),
        Photo(
            id = "2",
            url = "2",
        ),
        Photo(
            id = "3",
            url = "3",
        ),
    )
    val photoListStr = photoListConverter.fromPhotoList(photoList)
    val photoList2 = photoListConverter.toPhotoList(photoListStr)

    println("photoList: $photoList")
    println("photoListStr: $photoListStr")
    println("photoList2: $photoList2")

    println()
    println("photoList == photoList2: ${photoList2 == photoList}")

}