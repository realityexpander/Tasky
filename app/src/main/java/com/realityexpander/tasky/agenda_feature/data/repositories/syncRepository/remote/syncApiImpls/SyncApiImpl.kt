package com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.remote.syncApiImpls

import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.remote.ISyncApi
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.remote.SyncAgendaRequestDTO
import com.realityexpander.tasky.core.data.remote.TaskyApi
import javax.inject.Inject


class SyncApiImpl @Inject constructor(
    private val taskyApi: TaskyApi,
) : ISyncApi {

    override suspend fun syncAgenda(syncAgendaRequestDTO: SyncAgendaRequestDTO): Result<Unit> {
        try {
            val response = taskyApi.syncAgenda(syncAgendaRequestDTO)
            if (response.isSuccessful) {
                return Result.success(Unit)
            } else {
                return Result.failure(Exception("Error syncing agenda: ${response.errorBody()}"))
            }
        } catch (e: Exception) {
            return Result.failure(Exception("Error syncing agenda: ${e.localizedMessage}"))
        }
    }
}