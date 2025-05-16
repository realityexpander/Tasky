package com.realityexpander.tasky.core.util.internetConnectivityObserver

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.realityexpander.tasky.core.util.internetConnectivityObserver.IInternetConnectivityObserver.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import logcat.logcat

class InternetConnectivityObserverImpl(
    private val context: Context
) : IInternetConnectivityObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // use connectivityManager.requestNetwork(networkRequest, networkCallback) for api levels lower than 24

    override val onlineStateFlow =
        connectivityFlow().combine(internetReachabilityFlow) { connectedStatus, internetReachableStatus ->
            if (connectedStatus == ConnectivityStatus.Available
                && internetReachableStatus == InternetReachabilityStatus.REACHABLE
            )
                OnlineStatus.ONLINE
            else
                OnlineStatus.OFFLINE
        }.distinctUntilChanged()

    companion object {

        val internetReachabilityFlow =
            MutableStateFlow(InternetReachabilityStatus.UNREACHABLE)

        // Note: Side effect of reading this var is to set internetReachabilityFlow
        val isInternetReachable: Boolean
            get() {
                val runtime = Runtime.getRuntime()
                try {
                    logcat { "isInternetReachable() - ⎾ Checking Internet Reachability with a ping..." }

                    // send ping to 8.8.8.8, wait max 800ms for reply
                    val ipProcess = runtime.exec("/system/bin/ping -W 800 -c 1 8.8.8.8")
                    //    BufferedReader( // LEAVE FOR DEBUGGING
                    //        ipProcess.inputStream.reader()
                    //    ).forEachLine {
                    //        logcat { "isInternetReachable() - ├— $it" }
                    //    }

                    val exitValue = ipProcess.waitFor()

                    logcat { "isInternetReachable() - ⎿ Internet Reachability=${exitValue == 0}" }
                    internetReachabilityFlow.value =
                        if (exitValue == 0)
                            InternetReachabilityStatus.REACHABLE
                        else
                            InternetReachabilityStatus.UNREACHABLE

                    return (exitValue == 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                internetReachabilityFlow.value = InternetReachabilityStatus.UNREACHABLE
                return false
            }
    }

    override fun connectivityFlow(): Flow<ConnectivityStatus> {

        return callbackFlow {

            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)

                    logcat { "NetworkCallback - Connectivity= onAvailable" }
                    isInternetReachable // refresh internet reachability
                    trySend(ConnectivityStatus.Available)
                    //launch { send(ConnectivityObserver.Status.Available) } // can also use `offer`
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)

                    logcat { "NetworkCallback - Connectivity= onLosing" }
                    trySend(ConnectivityStatus.Losing)
                    //launch { send(ConnectivityObserver.Status.Losing) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)

                    logcat { "NetworkCallback - Connectivity= onLost" }
                    trySend(ConnectivityStatus.Lost)
                    //launch { send(ConnectivityObserver.Status.Lost) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()

                    logcat { "NetworkCallback - Connectivity= onUnavailable" }
                    trySend(ConnectivityStatus.Unavailable)
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

    @Suppress("DEPRECATION") // for API < 24
    fun isNetworkAvailable(): Boolean {
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}
