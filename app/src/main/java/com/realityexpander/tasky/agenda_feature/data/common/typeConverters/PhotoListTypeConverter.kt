package com.realityexpander.tasky.agenda_feature.data.common.typeConverters

import androidx.room.TypeConverter
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.PhotoRemoteEntity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PhotoListTypeConverter {
    @TypeConverter
    fun fromPhotoList(value: List<PhotoRemoteEntity>?): String? {
        return value?.let {
            Json.encodeToString(it)
        }
    }

    @TypeConverter
    fun toPhotoList(value: String?): List<PhotoRemoteEntity>? {
        return value?.let {
            Json.decodeFromString(it)
        }
    }
}


 // Local testing
fun main() {
    val photoListConverter = PhotoListTypeConverter()

    val photoList = listOf(
        PhotoRemoteEntity(
            id = "1",
            url = "1",
        ),
        PhotoRemoteEntity(
            id = "2",
            url = "2",
        ),
        PhotoRemoteEntity(
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