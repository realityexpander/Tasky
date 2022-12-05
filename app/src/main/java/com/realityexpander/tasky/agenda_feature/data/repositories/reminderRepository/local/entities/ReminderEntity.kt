package com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.realityexpander.tasky.core.util.UuidStr
import java.time.ZonedDateTime

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = false)
    val id: UuidStr,

    val title: String,
    val description: String,
    val remindAt: ZonedDateTime,
    val time: ZonedDateTime,

    val isSynced: Boolean = false
)