package com.realityexpander.tasky.core.util.ConnectivityObserver

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.realityexpander.observeconnectivity.IInternetConnectivityObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import logcat.logcat

class InternetConnectivityObserverImpl(
    private val context: Context
): IInternetConnectivityObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // use connectivityManager.requestNetwork(networkRequest, networkCallback) for api levels lower than 24

    companion object {
        val isInternetAvailable: Boolean
            get() {
                val runtime = Runtime.getRuntime()
                try {
                    logcat { "isInternetAvailable() - Checking internet availability with a ping..." }
                    // send ping to 8.8.8.8, wait max 800ms for reply
                    val ipProcess = runtime.exec("/system/bin/ping -W 800 -c 1 8.8.8.8")

                    val exitValue = ipProcess.waitFor()
                    logcat { "isInternetAvailable() - Connectivity Available=${exitValue==0}" }
                    return (exitValue == 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return false
            }
    }

    override fun observe(): Flow<IInternetConnectivityObserver.Status> {

        return callbackFlow {

            val callback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)

                        logcat { "NetworkCallback - onAvailable" }
                        if(isInternetAvailable)
                            trySend(IInternetConnectivityObserver.Status.Available)
                            //launch { send(ConnectivityObserver.Status.Available) } // can also use `offer`
                        else
                            trySend(IInternetConnectivityObserver.Status.Unavailable)
                    }

                    override fun onLosing(network: Network, maxMsToLive: Int) {
                        super.onLosing(network, maxMsToLive)

                        logcat { "NetworkCallback - onLosing" }
                        trySend(IInternetConnectivityObserver.Status.Losing)
                        //launch { send(ConnectivityObserver.Status.Losing) }
                    }

                    override fun onLost(network: Network) {
                        super.onLost(network)

                        logcat { "NetworkCallback - onLost" }
                        trySend(IInternetConnectivityObserver.Status.Lost)
                        //launch { send(ConnectivityObserver.Status.Lost) }
                    }

                    override fun onUnavailable() {
                        super.onUnavailable()

                        logcat { "NetworkCallback - onUnavailable" }
                        trySend(IInternetConnectivityObserver.Status.Unavailable)
                        //launch { send(ConnectivityObserver.Status.Unavailable) }
                    }
                }

            // Start listening for network changes
            connectivityManager.registerDefaultNetworkCallback(callback)

            // closes only when the scope that launched it is cancelled (e.g. when the activity/fragment is destroyed)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }

    fun isNetworkAvailable(): Boolean {
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}