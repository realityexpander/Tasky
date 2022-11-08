package com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote

import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.DTOs.AgendaDayDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.DTOs.AgendaSyncDTO
import com.realityexpander.tasky.core.data.remote.TaskyApi
import com.realityexpander.tasky.core.util.toUtcLong
import java.time.ZonedDateTime
import javax.inject.Inject

class AgendaApiImpl @Inject constructor(
    private val taskyApi: TaskyApi
): IAgendaApi {

    override suspend fun getAgenda(zonedDateTime: ZonedDateTime): AgendaDayDTO {
        return try {
            val response = taskyApi.getAgenda(
                ZonedDateTime.now().zone.id,
                zonedDateTime.toUtcLong()
            )

            if (response.isSuccessful) {
                val responseBody = response.body()

                responseBody ?: throw Exception("Response body is null")
            } else {
                throw Exception("Error getting agenda")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun syncAgenda(agendaSync: AgendaSyncDTO): Boolean {
        return try {
            val response = taskyApi.syncAgenda(agendaSync)

            response.isSuccessful
        } catch (e: Exception) {
            throw e
        }
    }
}