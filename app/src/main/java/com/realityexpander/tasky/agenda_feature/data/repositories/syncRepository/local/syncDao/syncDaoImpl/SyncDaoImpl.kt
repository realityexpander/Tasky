package com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.syncDao.syncDaoImpl

import androidx.room.*
import com.realityexpander.tasky.agenda_feature.domain.AgendaItemId
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.ISyncDao
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.ModificationTypeForSync
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.entities.SyncItemEntity

// SyncDao implements a Persistent Set using Room

@Dao
interface SyncDaoImpl : ISyncDao {

    // • CREATE / UPDATE

    @Transaction
    override suspend fun addSyncEntity(
        item: SyncItemEntity
    ) {
        // Delete any existing entries
        getSyncAgendaItems().filter {
            it.agendaItemId == item.agendaItemId
            && it.agendaItemTypeForSync == item.agendaItemTypeForSync
            && it.modificationTypeForSync == item.modificationTypeForSync
        }.forEach {
            deleteSyncAgendaItemByAgendaItemId(it.agendaItemId, it.modificationTypeForSync)
        }

        _insertModifiedAgendaItem(item)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertModifiedAgendaItem(
        item: SyncItemEntity
    )


    // • READ

    @Query("SELECT * FROM sync_items")
    override suspend fun getSyncAgendaItems(): List<SyncItemEntity>


    // • DELETE

    @Query("DELETE FROM sync_items " +
            "WHERE agendaItemId = :agendaItemId " +
            "AND modificationTypeForSync = :modificationTypeForSync")
    override suspend fun deleteSyncAgendaItemByAgendaItemId(
        agendaItemId: AgendaItemId,
        modificationTypeForSync: ModificationTypeForSync
    ): Int

    @Query("DELETE FROM sync_items " +
            "WHERE agendaItemId IN (:agendaItemIds) " +
            "AND modificationTypeForSync = :modificationTypeForSync")
    override suspend fun deleteModifiedAgendaItemsByAgendaItemIds(
        agendaItemIds: List<AgendaItemId>,
        modificationTypeForSync: ModificationTypeForSync
    ): Int
}
