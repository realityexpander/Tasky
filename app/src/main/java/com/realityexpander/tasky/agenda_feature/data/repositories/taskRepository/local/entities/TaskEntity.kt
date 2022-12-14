package com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.realityexpander.tasky.agenda_feature.domain.AbstractAgendaItem
import com.realityexpander.tasky.agenda_feature.domain.UsesZonedDateTime
import com.realityexpander.tasky.core.util.UuidStr
import java.time.ZonedDateTime

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = false)
    override val id: UuidStr,

    override val title: String,
    override val description: String,
    override val remindAt: ZonedDateTime,
    val time: ZonedDateTime,
    val isDone: Boolean,

    val isSynced: Boolean = false,
) : AbstractAgendaItem(), UsesZonedDateTime