package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.realityexpander.tasky.agenda_feature.common.util.PhotoId
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.core.util.UuidStr
import java.time.ZonedDateTime

@Entity(tableName = "events")
data class EventEntity constructor(
    @PrimaryKey(autoGenerate = false)
    override val id: UuidStr,

    override val title: String,
    override val description: String,
    override val remindAt: ZonedDateTime,
    val from: ZonedDateTime,
    val to: ZonedDateTime,

    val host: UuidStr? = null,
    val isUserEventCreator: Boolean = false,
    val attendees: List<AttendeeEntity> = emptyList(),

    val photos: List<PhotoEntity> = emptyList(),
    val deletedPhotoIds: List<PhotoId> = emptyList(),

    val isSynced: Boolean = false,
) : AgendaItem() {

    @Ignore
    override val startTime: ZonedDateTime = from
}