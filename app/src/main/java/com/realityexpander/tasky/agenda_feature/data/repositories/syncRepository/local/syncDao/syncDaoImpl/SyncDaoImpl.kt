package com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.syncDao.syncDaoImpl

import androidx.room.*
import com.realityexpander.tasky.agenda_feature.common.util.AgendaItemId
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.ISyncDao
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.ModificationTypeForSync
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.entities.ModifiedAgendaItemEntity

@Dao
interface SyncDaoImpl : ISyncDao {

    // • CREATE / UPDATE

    @Transaction
    override suspend fun addModifiedAgendaItem(
        item: ModifiedAgendaItemEntity
    ) {
        // Delete the old entry (if any)
        getModifiedAgendaItemsForSync().filter {
            it.agendaItemId == item.agendaItemId
            && it.agendaItemTypeForSync == item.agendaItemTypeForSync
            && it.modificationTypeForSync == item.modificationTypeForSync
        }.forEach {
            deleteModifiedAgendaItemByAgendaItemId(it.agendaItemId, it.modificationTypeForSync)
        }
        insertModifiedAgendaItem(item)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModifiedAgendaItem(
        item: ModifiedAgendaItemEntity
    )


    // • READ

    @Query("SELECT * FROM modified_agenda_items")
    override suspend fun getModifiedAgendaItemsForSync(): List<ModifiedAgendaItemEntity>


    // • DELETE

    @Query("DELETE FROM modified_agenda_items " +
            "WHERE agendaItemId = :agendaItemId " +
            "AND modificationTypeForSync = :modificationTypeForSync")
    override suspend fun deleteModifiedAgendaItemByAgendaItemId(
        agendaItemId: AgendaItemId,
        modificationTypeForSync: ModificationTypeForSync
    ): Int

    @Query("DELETE FROM modified_agenda_items " +
            "WHERE agendaItemId IN (:agendaItemIds) " +
            "AND modificationTypeForSync = :modificationTypeForSync")
    override suspend fun deleteModifiedAgendaItemsByAgendaItemIds(
        agendaItemIds: List<AgendaItemId>,
        modificationTypeForSync: ModificationTypeForSync
    ): Int
}
