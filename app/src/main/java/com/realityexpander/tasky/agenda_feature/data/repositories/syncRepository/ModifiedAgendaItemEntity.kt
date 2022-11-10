package com.realityexpander.tasky.agenda_feature.data.repositories

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.realityexpander.tasky.core.util.UuidStr

@Entity(tableName = "modified_agenda_items")
data class ModifiedAgendaItemEntity(
    @PrimaryKey(autoGenerate = false)
    val agendaItemId: UuidStr,

    val type: ModificationType // could be Created, Updated or Deleted
)

enum class ModificationType {
    Created,
    Updated,
    Deleted
}