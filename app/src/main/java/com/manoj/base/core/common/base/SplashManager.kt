package com.manoj.base.core.common.base

import android.animation.AnimatorSet
import android.os.SystemClock
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashManager(private val activity: AppCompatActivity, private val animationDuration: Long) {

    fun setupSplashScreen() {
        val splashScreen = activity.installSplashScreen()
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val animator = CustomScreenAnimator.alphaAnimation(splashScreenView)
            val animatorSet = AnimatorSet().apply {
                this.playTogether(animator)
                duration = animationDuration
                doOnEnd { splashScreenView.remove() }
            }
            animatorSet.start()
        }

        val content: View = activity.findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (isDataReady()) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else false
                }
            }
        )
    }

    companion object {
        const val WORK_DURATION = 2000L
    }

    private val initTime = SystemClock.uptimeMillis()
    private fun isDataReady() = SystemClock.uptimeMillis() - initTime > WORK_DURATION
}
