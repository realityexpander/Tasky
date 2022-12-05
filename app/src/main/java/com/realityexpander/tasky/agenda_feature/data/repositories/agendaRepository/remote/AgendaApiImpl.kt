package com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote

import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.DTOs.AgendaDayDTO
import com.realityexpander.tasky.core.data.remote.TaskyApi
import com.realityexpander.tasky.core.util.rethrowIfCancellation
import com.realityexpander.tasky.core.util.toUtcMillis
import java.time.ZonedDateTime
import javax.inject.Inject

class AgendaApiImpl @Inject constructor(
    private val taskyApi: TaskyApi
): IAgendaApi {

    override suspend fun getAgenda(zonedDateTime: ZonedDateTime): Result<AgendaDayDTO> {
        return try {
            val response = taskyApi.getAgenda(
                ZonedDateTime.now().zone.id.toString(),
                zonedDateTime.toUtcMillis()
            )

            if (response.isSuccessful) {
                val agendaDayDTO = response.body()

                agendaDayDTO ?: Result.failure<AgendaDayDTO>(Exception("Response body is null"))
                Result.success(agendaDayDTO!!)
            } else {
                Result.failure(Exception("Error getting agenda"))
            }
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            Result.failure(Exception("Error getting agenda"))
        }
    }
}