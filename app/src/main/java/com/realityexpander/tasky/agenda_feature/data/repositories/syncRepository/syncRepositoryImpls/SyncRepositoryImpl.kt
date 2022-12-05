package com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.syncRepositoryImpls

import com.realityexpander.tasky.agenda_feature.common.util.AgendaItemId
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.ISyncRepository
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.AgendaItemTypeForSync
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.ISyncDao
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.ModificationTypeForSync
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.entities.SyncItemEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.remote.ISyncApi
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.remote.SyncAgendaRequestDTO
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText
import com.realityexpander.tasky.core.presentation.common.util.UiText
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val syncApi: ISyncApi,
    private val syncDao: ISyncDao,
) : ISyncRepository {
    override suspend fun addCreatedSyncItem(agendaItem: AgendaItem): ResultUiText<Void> {
        addSyncItem(
            ModificationTypeForSync.Created,
            agendaItem.toAgendaItemTypeForSync(),
            agendaItem.id
        )
        return ResultUiText.Success(null)  // todo error checks
    }

    override suspend fun removeCreatedSyncItem(agendaItem: AgendaItem): ResultUiText<Void> {
        syncDao.deleteSyncAgendaItemByAgendaItemId(
            agendaItem.id,
            ModificationTypeForSync.Created
        )
        return ResultUiText.Success(null) // todo error checks
    }

    override suspend fun addUpdatedSyncItem(agendaItem: AgendaItem): ResultUiText<Void> {
        return addSyncItem(
            ModificationTypeForSync.Updated,
            agendaItem.toAgendaItemTypeForSync(),
            agendaItem.id,
        )
    }

    override suspend fun removeUpdatedSyncItem(agendaItem: AgendaItem): ResultUiText<Void> {
        syncDao.deleteSyncAgendaItemByAgendaItemId(agendaItem.id, ModificationTypeForSync.Updated)
        return ResultUiText.Success(null)
    }

    override suspend fun addDeletedSyncItem(agendaItem: AgendaItem): ResultUiText<Void> {
        // SPECIAL CASE: If the item is created, then it is not yet on the server,
        //   so we can just remove it from the local database and no need to sync.
        val syncAgendaItem = syncDao.getSyncAgendaItems().firstOrNull {
            it.agendaItemId == agendaItem.id
                    && it.modificationTypeForSync == ModificationTypeForSync.Created
        }
        syncAgendaItem?.run {
            removeCreatedSyncItem(agendaItem)
            // no need to add "sync Deleted" to table, because the AgendaItem was deleted before it could be synced.
            return ResultUiText.Success(null)
        }

        // remove any existing update sync items for this Agenda item
        removeUpdatedSyncItem(agendaItem)

        return addSyncItem(
            ModificationTypeForSync.Deleted,
            agendaItem.toAgendaItemTypeForSync(),
            agendaItem.id
        )
    }

    override suspend fun removeDeletedSyncItem(agendaItem: AgendaItem): ResultUiText<Void> {
        val deleted = syncDao.deleteSyncAgendaItemByAgendaItemId(
            agendaItem.id,
            ModificationTypeForSync.Deleted
        )
        return if (deleted > 0)
            ResultUiText.Success(null)
        else
            ResultUiText.Error(UiText.Str("Failed to remove deleted item"))
    }

    override suspend fun syncDeletedAgendaItems(syncItems: List<SyncItemEntity>): ResultUiText<Void> {
        val deleteRequest = syncItems.toSyncAgendaRequestDTO()

        // If there are no items to Delete, then there is nothing to sync.
        if (deleteRequest.deletedEventIds.isNullOrEmpty()
            && deleteRequest.deletedTaskIds.isNullOrEmpty()
            && deleteRequest.deletedReminderIds.isNullOrEmpty()
        ) {
            return ResultUiText.Success(null)
        }

        val result = syncApi.syncAgenda(deleteRequest)

        return if (result.isSuccess) {
            syncItems.forEach {
                deleteSyncItemByAgendaItemId(it.agendaItemId, it.modificationTypeForSync)
            }
            ResultUiText.Success(null)
        } else {
            ResultUiText.Error(
                UiText.Str(
                    result.exceptionOrNull()?.localizedMessage ?: "Unknown error"
                )
            )
        }
    }

    override suspend fun getSyncItems(): List<SyncItemEntity> =
        syncDao.getSyncAgendaItems()

    //////////////////////////
    //// HELPERS

    private fun AgendaItem.toAgendaItemTypeForSync(): AgendaItemTypeForSync {
        return when (this) {
            is AgendaItem.Task -> AgendaItemTypeForSync.Task
            is AgendaItem.Event -> AgendaItemTypeForSync.Event
            is AgendaItem.Reminder -> AgendaItemTypeForSync.Reminder
            else -> {
                throw IllegalArgumentException("Unknown AgendaItem type")
            }
        }
    }

    private suspend fun addSyncItem(
        modificationTypeForSync: ModificationTypeForSync,
        agendaItemTypeForSync: AgendaItemTypeForSync,
        agendaItemId: AgendaItemId
    ): ResultUiText<Void> {
        return try {
            syncDao.addSyncEntity(
                SyncItemEntity(
                    agendaItemId = agendaItemId,
                    agendaItemTypeForSync = agendaItemTypeForSync,
                    modificationTypeForSync = modificationTypeForSync
                )
            )
            ResultUiText.Success(null)
        } catch (e: Exception) {
            return ResultUiText.Error(UiText.Str(e.localizedMessage))
        }
    }

    override suspend fun deleteSyncItemByAgendaItemId(
        agendaItemId: AgendaItemId,
        modificationTypeForSync: ModificationTypeForSync
    ) =
        syncDao.deleteSyncAgendaItemByAgendaItemId(agendaItemId, modificationTypeForSync)

    // Prepare the OFFLINE-DELETED AgendaItems to be sent to the server
    private fun List<SyncItemEntity>.toSyncAgendaRequestDTO(): SyncAgendaRequestDTO {

        val deletedEventIds = this.filter {
                it.agendaItemTypeForSync == AgendaItemTypeForSync.Event
                && it.modificationTypeForSync == ModificationTypeForSync.Deleted
            }.map {
                it.agendaItemId
            }

        val deletedTaskIds = this.filter {
                it.agendaItemTypeForSync == AgendaItemTypeForSync.Task
                && it.modificationTypeForSync == ModificationTypeForSync.Deleted
            }.map {
                it.agendaItemId
            }

        val deletedReminderIds = this.filter {
                it.agendaItemTypeForSync == AgendaItemTypeForSync.Reminder
                && it.modificationTypeForSync == ModificationTypeForSync.Deleted
            }.map {
                it.agendaItemId
            }

        return SyncAgendaRequestDTO(
            deletedEventIds = deletedEventIds,
            deletedTaskIds = deletedTaskIds,
            deletedReminderIds = deletedReminderIds
        )
    }

}
