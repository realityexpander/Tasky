package com.realityexpander.observeconnectivity

import kotlinx.coroutines.flow.Flow

interface IInternetConnectivityObserver {

    fun observe(): Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}