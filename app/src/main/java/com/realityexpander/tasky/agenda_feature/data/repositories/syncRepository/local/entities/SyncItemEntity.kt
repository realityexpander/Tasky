package com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.AgendaItemTypeForSync
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.ModificationTypeForSync
import com.realityexpander.tasky.core.util.UuidStr

@Entity(tableName = "sync_items")
data class SyncItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val modificationTypeForSync: ModificationTypeForSync,    // Created, Updated or Deleted
    val agendaItemTypeForSync: AgendaItemTypeForSync,       // Event, Task, Reminder
    val agendaItemId: UuidStr,
)