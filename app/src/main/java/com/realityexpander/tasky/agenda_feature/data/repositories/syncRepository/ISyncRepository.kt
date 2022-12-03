package com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository

import com.realityexpander.tasky.agenda_feature.common.util.AgendaItemId
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.ModificationTypeForSync
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.entities.SyncAgendaItemEntity
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText

interface ISyncRepository {
    suspend fun addCreatedSyncItem(agendaItem: AgendaItem): ResultUiText<Void>
    suspend fun removeCreatedSyncItem(agendaItem: AgendaItem): ResultUiText<Void>

    suspend fun addUpdatedSyncItem(agendaItem: AgendaItem): ResultUiText<Void>
    suspend fun removeUpdatedSyncItem(agendaItem: AgendaItem): ResultUiText<Void>

    suspend fun addDeletedSyncItem(agendaItem: AgendaItem): ResultUiText<Void>
    suspend fun removeDeletedSyncItem(agendaItem: AgendaItem): ResultUiText<Void>

    suspend fun getSyncAgendaItemEntities() : List<SyncAgendaItemEntity>
    suspend fun deleteSyncAgendaItemByAgendaItemId(agendaItemId: AgendaItemId, modificationTypeForSync: ModificationTypeForSync): Int

    suspend fun syncDeletedAgendaItems(modifiedAgendaItems: List<SyncAgendaItemEntity>): ResultUiText<Void>
}
