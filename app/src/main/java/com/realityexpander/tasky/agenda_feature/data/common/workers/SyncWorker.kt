package com.realityexpander.tasky.agenda_feature.data.common.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.realityexpander.tasky.agenda_feature.domain.IAgendaRepository
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val workerParams: WorkerParameters,
    val agendaRepository: IAgendaRepository
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val result = agendaRepository.syncAgenda()

        return when (result) {
            is ResultUiText.Success -> Result.success()
            is ResultUiText.Error -> Result.failure()
        }
    }
}