package com.realityexpander.tasky.core.data.remote.utils

import okhttp3.OkHttpClient

fun cancelExistingApiCallWithSameValues(
    okHttpClient: OkHttpClient,
    urlParameter: String,
    urlValue: String
) {
    // A call may transition from queue -> running. Remove queued Calls first.
    okHttpClient.dispatcher.queuedCalls().forEach { call ->
        call.request().url.queryParameter(urlParameter)?.let { param ->
            if (param == urlValue) {
                call.cancel()
            }
        }
    }
    okHttpClient.dispatcher.runningCalls().forEach { call ->
        call.request().url.queryParameter(urlParameter)?.let { param ->
            if (param == urlValue) {
                call.cancel()
            }
        }
    }
}