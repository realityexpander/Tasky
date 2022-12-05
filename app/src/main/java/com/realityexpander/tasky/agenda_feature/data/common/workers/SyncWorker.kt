package com.realityexpander.tasky.agenda_feature.data.common.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.realityexpander.tasky.agenda_feature.domain.IAgendaRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val agendaRepository: IAgendaRepository  // <-- causes runtime error
): CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        println("SyncWorker.doWork()")
//        val result = agendaRepository.syncAgenda()

        if (runAttemptCount > 2) {
            return Result.failure()
        }

        return Result.success()
//        if(result is ResultUiText.Success)
//            Result.success()
//        else
//            Result.failure()
    }
}