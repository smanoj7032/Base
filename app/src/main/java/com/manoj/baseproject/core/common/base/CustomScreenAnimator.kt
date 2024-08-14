package com.manoj.baseproject.core.common.base

import android.animation.ObjectAnimator
import android.graphics.Path
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.splashscreen.SplashScreenViewProvider

object CustomScreenAnimator {

     fun slideUpAnimation(splashScreenView: SplashScreenViewProvider): ObjectAnimator {
        return ObjectAnimator.ofFloat(
            splashScreenView.view,
            View.TRANSLATION_Y,
            0f,
            -splashScreenView.view.height.toFloat(),
        )
    }

    fun slideLeftAnimation(splashScreenView: SplashScreenViewProvider): ObjectAnimator {
        return ObjectAnimator.ofFloat(
            splashScreenView.view,
            View.TRANSLATION_X,
            0f,
            -splashScreenView.view.width.toFloat()
        )
    }

    fun scaleXAnimation(splashScreenView: SplashScreenViewProvider): ObjectAnimator {
        return ObjectAnimator.ofFloat(
            splashScreenView.view,
            View.SCALE_X,
            1.0f,
            0f
        )
    }

     fun scaleXYAnimation(splashScreenView: SplashScreenViewProvider): ObjectAnimator {
        val path = Path()
        path.moveTo(1.0f, 1.0f)
        path.lineTo(0f, 0f)

        return ObjectAnimator.ofFloat(
            splashScreenView.view,
            View.SCALE_X,
            View.SCALE_Y,
            path
        )
    }

     fun alphaAnimation(splashScreenView: SplashScreenViewProvider): ObjectAnimator {
        return ObjectAnimator.ofFloat(
            splashScreenView.view,
            View.ALPHA,
            1f,
            0f
        ).apply {
            duration = 2000
            interpolator = AccelerateDecelerateInterpolator()
        }
    }
}