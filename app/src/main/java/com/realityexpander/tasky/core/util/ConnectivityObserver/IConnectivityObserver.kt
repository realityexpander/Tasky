package com.realityexpander.observeconnectivity

import kotlinx.coroutines.flow.Flow

interface IConnectivityObserver {

    fun observe(): Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}