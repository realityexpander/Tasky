package com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.reminderApi

import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.DTOs.ReminderDTO


interface IReminderApi {

    suspend fun createReminder(task: ReminderDTO): Result<Unit>
    suspend fun getReminder(taskId: ReminderId): ReminderDTO
    suspend fun updateReminder(task: ReminderDTO): Result<Unit>
    suspend fun deleteReminder(taskId: ReminderId): Result<Unit>
}