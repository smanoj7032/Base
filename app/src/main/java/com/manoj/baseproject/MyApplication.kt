package com.manoj.baseproject

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.manoj.baseproject.core.common.base.NetworkObserver
import com.manoj.baseproject.core.network.helper.NetworkMonitor
import com.manoj.baseproject.core.network.helper.SystemVariables
import com.manoj.baseproject.core.utils.dispatchers.DispatchersProvider
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltAndroidApp
class MyApplication : Application() {
    private var isRestarting: Boolean = false

    @Inject
    lateinit var networkObserver: NetworkObserver

    override

    fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        instance = this
        networkObserver.observeNetworkChanges()
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
