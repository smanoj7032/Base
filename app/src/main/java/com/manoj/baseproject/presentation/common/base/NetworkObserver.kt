package com.manoj.baseproject.presentation.common.base

import com.manoj.baseproject.network.helper.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NetworkObserver(
    private val networkMonitor: NetworkMonitor,
    private val scope: CoroutineScope
) {

    private var isApiCallMade = false
    private val listeners = mutableListOf<NetworkStateListener>()

    interface NetworkStateListener {
        fun onNetworkAvailable()
        fun onNetworkLost()
    }

    fun addListener(listener: NetworkStateListener) {
        listeners.add(listener)
        /** Notify immediately if network is available */
        scope.launch {
            if (networkMonitor.hasActiveInternetConnection()) {
                listener.onNetworkAvailable()
            } else {
                listener.onNetworkLost()
            }
        }
    }

    fun removeListener(listener: NetworkStateListener) {
        listeners.remove(listener)
    }

    fun observeNetworkChanges() {
        scope.launch {
            networkMonitor.networkState.collect { state ->
                when (state) {
                    NetworkMonitor.NetworkState.Available -> {
                        if (!isApiCallMade) {
                            isApiCallMade = true
                            listeners.forEach { it.onNetworkAvailable() }
                        }
                    }
                    NetworkMonitor.NetworkState.Lost -> {
                        isApiCallMade = false
                        listeners.forEach { it.onNetworkLost() }
                    }
                }
            }
        }
    }
}
