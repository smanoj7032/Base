package com.manoj.baseproject.core.common.base

import com.manoj.baseproject.core.network.helper.NetworkMonitor
import com.manoj.baseproject.core.network.helper.SystemVariables.isInternetConnected
import com.manoj.baseproject.core.network.helper.SystemVariables.onNetworkChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkObserver @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val scope: CoroutineScope
) {


    fun observeNetworkChanges() {
        scope.launch {
            networkMonitor.networkState.collect { state ->
                when (state) {
                    NetworkMonitor.NetworkState.Available -> {
                        isInternetConnected = true
                        onNetworkChange(state)
                    }

                    NetworkMonitor.NetworkState.Lost -> {
                        isInternetConnected = false
                        onNetworkChange(state)
                    }
                }
            }
        }
    }
}
