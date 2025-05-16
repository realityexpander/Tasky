package com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository

import com.realityexpander.tasky.agenda_feature.domain.AgendaItemId
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.ModificationTypeForSync
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.entities.SyncItemEntity
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.core.presentation.util.ResultUiText

interface ISyncRepository {
    suspend fun addCreatedSyncItem(agendaItem: AgendaItem): ResultUiText<Void>
    suspend fun removeCreatedSyncItem(agendaItem: AgendaItem): ResultUiText<Void>

    suspend fun addUpdatedSyncItem(agendaItem: AgendaItem): ResultUiText<Void>
    suspend fun removeUpdatedSyncItem(agendaItem: AgendaItem): ResultUiText<Void>

    suspend fun addDeletedSyncItem(agendaItem: AgendaItem): ResultUiText<Void>
    suspend fun removeDeletedSyncItem(agendaItem: AgendaItem): ResultUiText<Void>

    suspend fun getSyncItems() : List<SyncItemEntity>
    suspend fun deleteSyncItemByAgendaItemId(agendaItemId: AgendaItemId, modificationTypeForSync: ModificationTypeForSync): Int

    suspend fun syncDeletedAgendaItems(syncItems: List<SyncItemEntity>): ResultUiText<Void>
}
