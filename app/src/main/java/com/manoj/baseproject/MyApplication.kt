package com.manoj.baseproject

import android.app.Application
import android.content.Intent
import com.manoj.baseproject.presentation.view.activity.login.LoginActivity
import com.manoj.baseproject.presentation.view.activity.main.MainActivity
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
        isRestarting = true
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("isComeFrom", "Home")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}
