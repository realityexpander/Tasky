package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.realityexpander.tasky.agenda_feature.util.PhotoId
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

    val host: UuidStr? = null,
    val isUserEventCreator: Boolean? = null,
    val isGoing: Boolean? = null,

    val attendees: List<AttendeeEntity> = emptyList(),
    val photos: List<PhotoRemoteEntity> = emptyList(),

    val deletedPhotoKeys: List<PhotoId> = emptyList(),

    val isDeleted: Boolean = false,
)