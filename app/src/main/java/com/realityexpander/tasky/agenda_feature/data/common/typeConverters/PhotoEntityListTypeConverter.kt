package com.realityexpander.tasky.agenda_feature.data.common.typeConverters

import android.net.Uri
import androidx.room.TypeConverter
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.PhotoEntity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PhotoEntityListTypeConverter {
    @TypeConverter
    fun fromPhotoList(value: List<PhotoEntity>?): String? {
        return value?.let {
            Json.encodeToString(it)
        }
    }

    @TypeConverter
    fun toPhotoList(value: String?): List<PhotoEntity>? {
        return value?.let {
            Json.decodeFromString(it)
        }
    }
}


// Local testing
fun main() {
    val photoListConverter = PhotoEntityListTypeConverter()

    val photoList = listOf(
        PhotoEntity(
            id = "1",
            url = "1",
            uri = Uri.EMPTY
        ),
        PhotoEntity(
            id = "2",
            url = "2",
            uri = Uri.EMPTY
        ),
        PhotoEntity(
            id = "3",
            url = "3",
            uri = Uri.EMPTY
        ),
    )
    val photoListStr = photoListConverter.fromPhotoList(photoList)
    val photoList2 = photoListConverter.toPhotoList(photoListStr)

    println("photoList: $photoList")
    println("photoListStr: $photoListStr")
    println("photoList2: $photoList2")

    println()
    println("photoList == photoList2: ${photoList2 == photoList}")

    println()
    println(photoListConverter.fromPhotoList(null))
    println(photoListConverter.toPhotoList(null))

}