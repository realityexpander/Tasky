package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.realityexpander.tasky.core.util.UuidStr
import java.time.ZonedDateTime

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = false)
    val id: UuidStr,

    val title: String,
    val description: String,
    val remindAt: ZonedDateTime,
    val from: ZonedDateTime,
    val to: ZonedDateTime,

    val host: UuidStr,
    val isUserEventCreator: Boolean = false,
    val isGoing: Boolean = false,
    val attendeeIds: List<UuidStr> = emptyList(),

    val photos: List<UuidStr> = emptyList(),
    val deletedPhotoKeys: List<UuidStr> = emptyList(),

    val isDeleted: Boolean = false,
    val isUploaded: Boolean = false,
)
