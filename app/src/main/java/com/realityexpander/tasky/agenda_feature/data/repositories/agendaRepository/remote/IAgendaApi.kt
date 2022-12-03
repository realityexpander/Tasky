package com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote

import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.DTOs.AgendaDayDTO
import java.time.ZonedDateTime

interface IAgendaApi {

    suspend fun getAgenda(zonedDateTime: ZonedDateTime): Result<AgendaDayDTO>

}