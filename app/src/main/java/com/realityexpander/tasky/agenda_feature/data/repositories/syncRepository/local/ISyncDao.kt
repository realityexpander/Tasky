package com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local

import com.realityexpander.tasky.agenda_feature.domain.AgendaItemId
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.entities.SyncItemEntity
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem

interface ISyncDao {

    suspend fun addSyncEntity(
        item: SyncItemEntity
    ): Unit

    suspend fun deleteSyncAgendaItemByAgendaItemId(
        agendaItemId: AgendaItemId,
        modificationTypeForSync: ModificationTypeForSync
    ): Int
    suspend fun deleteModifiedAgendaItemsByAgendaItemIds(
        agendaItemIds: List<AgendaItemId>,
        modificationTypeForSync: ModificationTypeForSync
    ): Int

    suspend fun getSyncAgendaItems(): List<SyncItemEntity>
}

enum class ModificationTypeForSync {
    Created,
    Updated,
    Deleted
}

enum class AgendaItemTypeForSync(val type: Class<out AgendaItem>) {
    Event(AgendaItem.Event::class.java),
    Task(AgendaItem.Task::class.java),
    Reminder(AgendaItem.Reminder::class.java)
}
