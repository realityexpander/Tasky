package com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.syncRepositoryImpls

import com.realityexpander.tasky.agenda_feature.common.util.AgendaItemId
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.ISyncRepository
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.AgendaItemTypeForSync
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.ISyncDao
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.ModificationTypeForSync
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.entities.ModifiedAgendaItemEntity
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
    override suspend fun addCreatedItem(agendaItem: AgendaItem): ResultUiText<Void> {
        addModifiedAgendaItem(
            agendaItem.id,
            agendaItem.toAgendaItemTypeForSync(),
            ModificationTypeForSync.Created
        )
        return ResultUiText.Success(null)  // todo error checks
    }

    override suspend fun removeCreatedItem(agendaItem: AgendaItem): ResultUiText<Void> {
        syncDao.deleteModifiedAgendaItemByAgendaItemId(
            agendaItem.id,
            ModificationTypeForSync.Created
        )
        return ResultUiText.Success(null) // todo error checks
    }

    override suspend fun addUpdatedItem(agendaItem: AgendaItem): ResultUiText<Void> {
        addModifiedAgendaItem(
            agendaItem.id,
            agendaItem.toAgendaItemTypeForSync(),
            ModificationTypeForSync.Updated,
        )
        return ResultUiText.Success(null) // todo error checks
    }

    override suspend fun removeUpdatedItem(agendaItem: AgendaItem): ResultUiText<Void> {
        syncDao.deleteModifiedAgendaItemByAgendaItemId(agendaItem.id, ModificationTypeForSync.Updated)
        return ResultUiText.Success(null)
    }

    override suspend fun addDeletedItem(agendaItem: AgendaItem): ResultUiText<Void> {
        addModifiedAgendaItem(
            agendaItem.id,
            agendaItem.toAgendaItemTypeForSync(),
            ModificationTypeForSync.Deleted
        )
        return ResultUiText.Success(null)  // todo error checks
    }

    override suspend fun removeDeletedItem(agendaItem: AgendaItem): ResultUiText<Void> {
        val deleted = syncDao.deleteModifiedAgendaItemByAgendaItemId(agendaItem.id, ModificationTypeForSync.Deleted)
        return if(deleted > 0)
            ResultUiText.Success(null)
        else
            ResultUiText.Error(UiText.Str("Failed to remove deleted item"))
    }

    override suspend fun syncDeletedAgendaItems(modifiedAgendaItems: List<ModifiedAgendaItemEntity>) : ResultUiText<Void> {
        val result = syncApi.syncAgenda(modifiedAgendaItems.toSyncAgendaRequestDTO())

        if (result.isSuccess) {
            modifiedAgendaItems.forEach {
                deleteModifiedAgendaItemByAgendaItemId(it.agendaItemId, it.modificationTypeForSync)
            }
            return ResultUiText.Success(null)
        } else {
            return ResultUiText.Error(UiText.Str(result.exceptionOrNull()?.localizedMessage ?: "Unknown error"))
        }
    }

    override suspend fun getModifiedAgendaItemsForSync() : List<ModifiedAgendaItemEntity> =
        syncDao.getModifiedAgendaItemsForSync()

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

    private suspend fun addModifiedAgendaItem(
        agendaItemId: AgendaItemId,
        agendaItemTypeForSync: AgendaItemTypeForSync,
        modificationTypeForSync: ModificationTypeForSync
    ) {
        syncDao.addModifiedAgendaItem(
            ModifiedAgendaItemEntity(
                agendaItemId = agendaItemId,
                agendaItemTypeForSync = agendaItemTypeForSync,
                modificationTypeForSync = modificationTypeForSync
            )
        )
    }

    override suspend fun deleteModifiedAgendaItemByAgendaItemId(agendaItemId: AgendaItemId, modificationTypeForSync: ModificationTypeForSync) =
        syncDao.deleteModifiedAgendaItemByAgendaItemId(agendaItemId, modificationTypeForSync)

    // Prepare the OFFLINE-DELETED AgendaItems to be sent to the server
    private fun List<ModifiedAgendaItemEntity>.toSyncAgendaRequestDTO() =
        SyncAgendaRequestDTO(
            deletedEventIds =
            this
                .filter {
                    it.agendaItemTypeForSync == AgendaItemTypeForSync.Event
                            && it.modificationTypeForSync == ModificationTypeForSync.Deleted
                }
                .map {
                    it.agendaItemId
                },
            deletedTaskIds =
            this
                .filter {
                    it.agendaItemTypeForSync == AgendaItemTypeForSync.Task
                            && it.modificationTypeForSync == ModificationTypeForSync.Deleted
                }
                .map {
                    it.agendaItemId
                },
            deletedReminderIds =
            this
                .filter {
                    it.agendaItemTypeForSync == AgendaItemTypeForSync.Reminder
                            && it.modificationTypeForSync == ModificationTypeForSync.Deleted
                }
                .map {
                    it.agendaItemId
                },
        )

}
