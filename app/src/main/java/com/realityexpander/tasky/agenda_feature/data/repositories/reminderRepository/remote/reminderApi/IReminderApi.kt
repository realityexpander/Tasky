package com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.reminderApi

import com.realityexpander.tasky.agenda_feature.domain.ReminderId
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.DTOs.ReminderDTO


interface IReminderApi {

    suspend fun createReminder(reminder: ReminderDTO): Result<Unit>
    suspend fun getReminder(reminderId: ReminderId): ReminderDTO
    suspend fun updateReminder(reminder: ReminderDTO): Result<Unit>
    suspend fun deleteReminder(reminderId: ReminderId): Result<Unit>
}
