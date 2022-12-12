package com.realityexpander.tasky.agenda_feature.data.common.workers

import androidx.work.WorkerParameters
import logcat.logcat

fun WorkerParameters.log() {
    logcat { "┌-WorkerParameters:"}
    this.inputData.keyValueMap.entries.forEach { workerParam ->
        logcat {
            "┡-" + workerParam.key + " : " + workerParam.value
        }
    }
}