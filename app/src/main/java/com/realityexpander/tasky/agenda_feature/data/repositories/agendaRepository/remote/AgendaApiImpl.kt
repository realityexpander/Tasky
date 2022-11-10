package com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote

import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.DTOs.AgendaDayDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.DTOs.AgendaSyncDTO
import com.realityexpander.tasky.core.data.remote.TaskyApi
import com.realityexpander.tasky.core.util.toUtcMillis
import java.time.ZonedDateTime
import java.util.concurrent.CancellationException
import javax.inject.Inject

class AgendaApiImpl @Inject constructor(
    private val taskyApi: TaskyApi
): IAgendaApi {

    override suspend fun getAgenda(zonedDateTime: ZonedDateTime): AgendaDayDTO {
        return try {
            val response = taskyApi.getAgenda(
                ZonedDateTime.now().zone.id,
                zonedDateTime.toUtcMillis()
            )

            if (response.isSuccessful) {
                val responseBody = response.body()

                responseBody ?: throw Exception("Response body is null")
            } else {
                throw Exception("Error getting agenda")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw e  // todo improve error handling
        }
    }

    override suspend fun syncAgenda(agendaSync: AgendaSyncDTO): Boolean {
        return try {
            val response = taskyApi.syncAgenda(agendaSync)

            response.isSuccessful
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw e  // todo improve error handling
        }
    }
}