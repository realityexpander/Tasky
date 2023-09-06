package com.realityexpander.tasky.core.util.InternetConnectivityObserver

import com.realityexpander.observeconnectivity.IInternetConnectivityObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ConnectivityObserverFake : IInternetConnectivityObserver {
    override val onlineStateFlow =
        flow<IInternetConnectivityObserver.OnlineStatus> {
            emit(IInternetConnectivityObserver.OnlineStatus.ONLINE)
        }

    override fun connectivityFlow(): Flow<IInternetConnectivityObserver.ConnectivityStatus> {
        return onlineStateFlow.map {
            if (it == IInternetConnectivityObserver.OnlineStatus.ONLINE)
                IInternetConnectivityObserver.ConnectivityStatus.Available
            else
                IInternetConnectivityObserver.ConnectivityStatus.Unavailable
        }
    }
}
