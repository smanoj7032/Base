package com.manoj.base.core.utils

import android.os.SystemClock
import android.view.View

abstract class SingleClickListener : View.OnClickListener {

    /**
     * The shortest time interval between click events
     */
    companion object {
        private const val MIN_CLICK_INTERVAL: Long = 1000
    }

    /**
     * Last click time
     */
    private var lastClickTime: Long = 0

    /**
     * Click response function
     *
     * @param v The view that was clicked.
     */
    abstract fun onClickView(v: View)

    override fun onClick(v: View) {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - lastClickTime
        lastClickTime = currentClickTime

        if (elapsedTime <= MIN_CLICK_INTERVAL) return

        onClickView(v)
    }
}
