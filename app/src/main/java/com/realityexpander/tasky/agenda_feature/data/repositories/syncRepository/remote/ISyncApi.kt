package com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.remote

interface ISyncApi {

    suspend fun syncAgenda(syncAgendaRequestDTO: SyncAgendaRequestDTO): Result<Unit>
}