package com.manoj.base.presentation.activity.main.backpress

import androidx.activity.OnBackPressedCallback
import com.manoj.base.core.common.singletonholder.SingletonHolder
import com.manoj.base.core.utils.extension.showToast
import com.manoj.base.presentation.activity.main.MainActivity

class BackPressHandler(private val activity: MainActivity) : OnBackPressedCallback(true) {

    companion object : SingletonHolder<BackPressHandler, MainActivity>(::BackPressHandler)

    private val TIME_INTERVAL = 2000
    private var doubleBackToExitPressedOnce: Long = 0

    override fun handleOnBackPressed() {
        if (doubleBackToExitPressedOnce + TIME_INTERVAL > System.currentTimeMillis()) {
            activity.finish()
        } else {
            activity.showToast("Please click BACK again to exit")
        }
        doubleBackToExitPressedOnce = System.currentTimeMillis()
    }
}
