package com.manoj.baseproject

import android.app.Application
import android.content.Intent
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MyApplication : Application() {
    private var isRestarting: Boolean = false

    override fun onCreate() {
        super.onCreate()
        instance = this
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
