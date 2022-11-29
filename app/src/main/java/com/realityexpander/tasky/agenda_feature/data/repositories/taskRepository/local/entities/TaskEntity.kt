package com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.realityexpander.tasky.core.util.UuidStr
import java.time.ZonedDateTime

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = false)
    val id: UuidStr,

    val title: String,
    val description: String,
    val remindAt: ZonedDateTime,
    val time: ZonedDateTime,
    val isDone: Boolean,

    val isDeleted: Boolean = false,
)