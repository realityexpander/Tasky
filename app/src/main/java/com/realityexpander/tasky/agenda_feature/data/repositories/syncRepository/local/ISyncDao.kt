package com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local

import com.realityexpander.tasky.agenda_feature.common.util.AgendaItemId
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.entities.ModifiedAgendaItemEntity
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem

interface ISyncDao {

    suspend fun addModifiedAgendaItem(
        item: ModifiedAgendaItemEntity
    ): Unit

    suspend fun deleteModifiedAgendaItemByAgendaItemId(
        agendaItemId: AgendaItemId,
        modificationTypeForSync: ModificationTypeForSync
    ): Int
    suspend fun deleteModifiedAgendaItemsByAgendaItemIds(
        agendaItemIds: List<AgendaItemId>,
        modificationTypeForSync: ModificationTypeForSync
    ): Int

    suspend fun getModifiedAgendaItemsForSync(): List<ModifiedAgendaItemEntity>
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