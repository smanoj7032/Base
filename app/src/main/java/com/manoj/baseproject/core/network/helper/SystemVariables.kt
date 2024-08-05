package com.manoj.baseproject.core.network.helper

object SystemVariables {
    var isInternetConnected = false
    var onNetworkChange: (NetworkMonitor.NetworkState) -> Unit = {}
}