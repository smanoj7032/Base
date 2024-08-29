package com.manoj.base

import android.app.Application
import com.manoj.base.core.common.base.NetworkObserver
import com.manoj.base.core.network.helper.SystemVariables
import com.manoj.base.core.utils.Logger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var networkObserver: NetworkObserver

    override

    fun onCreate() {
        super.onCreate()
        instance = this
        networkObserver.observeNetworkChanges()

        /** Registers a callback for network change events. When the network status changes,
        this callback will be triggered, logging an error message with the class name of
        the current context and the new network status (represented by `it`). */
        SystemVariables.onNetworkChange = {
            Logger.e("onNetworkChange", "${this.javaClass.simpleName}------>> $it")
        }

    }

    fun onLogout() {
        restartApp()
    }

    companion object {
        @get:Synchronized
        lateinit var instance: MyApplication
    }

    private fun restartApp() {
        /** WRITE CODE HERE TO NAVIGATE TO LOGIN SCREEN */
    }
}
