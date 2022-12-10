package com.realityexpander.tasky.core.util.ConnectivityObserver

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.realityexpander.observeconnectivity.IConnectivityObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import logcat.logcat

class ConnectivityObserverImpl(
    private val context: Context
): IConnectivityObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // use connectivityManager.requestNetwork(networkRequest, networkCallback) for api levels lower than 24

    companion object {
        val isInternetAvailable: Boolean
            get() {
                val runtime = Runtime.getRuntime()
                try {
                    val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")

                    val exitValue = ipProcess.waitFor()
                    return (exitValue == 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return false
            }

        var isWifiAvailable = false
            private set
    }

    init {
        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                logcat { "onAvailable" }

                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                networkCapabilities?.apply {
                    val isInternetCapabilitiesAvailable =
                        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    isWifiAvailable =
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    val isCellularAvailable =
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    val isEthernetAvailable =
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    val isMetered =
                        hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                    val isRoaming =
                        hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_ROAMING)
                    val isBackgroundDataRestricted =
                        hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                    val isNotConstrained =
                        hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_CONGESTED)
                    val isNotVpn =
                        hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
                    val isNotRestricted =
                        hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                    val isNotSuspended =
                        hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED)
                }
            }
        })
    }

    override fun observe(): Flow<IConnectivityObserver.Status> {

        return callbackFlow {

            val callback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        trySend(IConnectivityObserver.Status.Available)
                        //launch { send(ConnectivityObserver.Status.Available) } // can also use `offer`
                    }

                    override fun onLosing(network: Network, maxMsToLive: Int) {
                        super.onLosing(network, maxMsToLive)
                        trySend(IConnectivityObserver.Status.Losing)
                        //launch { send(ConnectivityObserver.Status.Losing) }
                    }

                    override fun onLost(network: Network) {
                        super.onLost(network)
                        trySend(IConnectivityObserver.Status.Lost)
                        //launch { send(ConnectivityObserver.Status.Lost) }
                    }

                    override fun onUnavailable() {
                        super.onUnavailable()
                        trySend(IConnectivityObserver.Status.Unavailable)
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