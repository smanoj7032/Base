package com.manoj.baseproject.network.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class NetworkMonitor(
    @ApplicationContext context: Context
) {

    enum class NetworkState {
        Available, Lost;

        fun isAvailable() = this == Available
        fun isLost() = this == Lost
    }

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var previousState: NetworkState? = null
    val networkState: Flow<NetworkState> = callbackFlow {

        launch { send(getInitialState()) }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val currentState = NetworkState.Available
                if (currentState != previousState) {
                    launch { send(currentState) }
                    previousState = currentState
                    Log.d("NetworkState-->>>", "NetworkMonitor: onAvailable()")
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                val currentState = NetworkState.Lost
                if (currentState != previousState) {
                    launch { send(currentState) }
                    previousState = currentState
                    Log.d("NetworkState-->>>", "NetworkMonitor: onLost()")
                }
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)

        awaitClose {
            Log.d("XXX", "NetworkMonitor: awaitClose")
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    private fun getInitialState(): NetworkState =
        if (connectivityManager.activeNetwork != null) NetworkState.Available else NetworkState.Lost
}