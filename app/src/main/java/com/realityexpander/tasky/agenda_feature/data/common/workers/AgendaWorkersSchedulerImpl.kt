package com.realityexpander.tasky.agenda_feature.data.common.workers

import android.content.Context
import androidx.work.WorkManager
import com.realityexpander.tasky.agenda_feature.domain.IAgendaWorkersScheduler
import com.realityexpander.tasky.agenda_feature.domain.IWorkerScheduler
import javax.inject.Inject
import javax.inject.Named

class AgendaWorkersSchedulerImpl @Inject constructor(
    private val context: Context,
    @Named("SyncAgendaWorkerScheduler")
    private val syncAgendaWorkerScheduler: IWorkerScheduler,
    @Named("RefreshAgendaWeekWorkerScheduler")
    private val refreshAgendaWeekWorkerScheduler: IWorkerScheduler,
) : IAgendaWorkersScheduler {

    override suspend fun startAllWorkers() {
        syncAgendaWorkerScheduler.startWorker()
        refreshAgendaWeekWorkerScheduler.startWorker()
    }

    override suspend fun cancelAllWorkers() {
        WorkManager.getInstance(context).cancelAllWorkByTag(AGENDA_WORKERS_TAG)
    }
}