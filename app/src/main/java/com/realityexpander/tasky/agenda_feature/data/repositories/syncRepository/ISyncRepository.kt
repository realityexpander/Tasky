package com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository

import com.realityexpander.tasky.agenda_feature.common.util.AgendaItemId
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.ModificationTypeForSync
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.entities.ModifiedAgendaItemEntity
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText

interface ISyncRepository {
    suspend fun addCreatedItem(agendaItem: AgendaItem): ResultUiText<Void>
    suspend fun removeCreatedItem(agendaItem: AgendaItem): ResultUiText<Void>

    suspend fun addUpdatedItem(agendaItem: AgendaItem): ResultUiText<Void>
    suspend fun removeUpdatedItem(agendaItem: AgendaItem): ResultUiText<Void>

    suspend fun addDeletedItem(agendaItem: AgendaItem): ResultUiText<Void>
    suspend fun removeDeletedItem(agendaItem: AgendaItem): ResultUiText<Void>

    suspend fun getModifiedAgendaItemsForSync() : List<ModifiedAgendaItemEntity>
    suspend fun deleteModifiedAgendaItemByAgendaItemId(agendaItemId: AgendaItemId, modificationTypeForSync: ModificationTypeForSync): Int

    suspend fun syncDeletedAgendaItems(modifiedAgendaItems: List<ModifiedAgendaItemEntity>): ResultUiText<Void>
}
