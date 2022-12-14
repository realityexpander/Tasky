package com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.local.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.core.util.UuidStr
import java.time.ZonedDateTime

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = false)
    override val id: UuidStr,

    override val title: String,
    override val description: String,
    override val remindAt: ZonedDateTime,
    val time: ZonedDateTime,

    val isSynced: Boolean = false,
) : AgendaItem() {

    @Ignore
    override val startTime: ZonedDateTime = time
}