package com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.AgendaItemTypeForSync
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.ModificationTypeForSync
import com.realityexpander.tasky.core.util.UuidStr

@Entity(tableName = "modified_agenda_items")
data class ModifiedAgendaItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val agendaItemId: UuidStr,
    val agendaItemTypeForSync: AgendaItemTypeForSync,       // Event, Task, Reminder
    val modificationTypeForSync: ModificationTypeForSync    // Created, Updated or Deleted
)