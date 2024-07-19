package com.manoj.baseproject.presentation.common.base

import android.animation.AnimatorSet
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.manoj.baseproject.BR
import com.manoj.baseproject.MyApplication
import com.manoj.baseproject.R
import com.manoj.baseproject.data.local.SharedPrefManager
import com.manoj.baseproject.databinding.CustomDialogBinding
import com.manoj.baseproject.databinding.ViewProgressSheetBinding
import com.manoj.baseproject.network.helper.NetworkMonitor
import com.manoj.baseproject.presentation.common.basedialogs.BaseBottomSheetDialog
import com.manoj.baseproject.utils.Logger
import com.manoj.baseproject.utils.hide
import com.manoj.baseproject.utils.setSingleClickListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class BaseActivity<Binding : ViewDataBinding> : AppCompatActivity() {
    lateinit var splashScreen: SplashScreen




    @Inject
    lateinit var sharepref: SharedPrefManager
    val TAG: String = this.javaClass.simpleName
    private var progressSheet: ProgressSheet? = null
    open val onRetry: (() -> Unit)? = null
    lateinit var binding: Binding
    val app: MyApplication
        get() = application as MyApplication
    val TIMER_ANIMATION: Long = 400
    private var successDialog: BaseBottomSheetDialog<CustomDialogBinding>? = null
    private var isApiHit = false

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        if (isMain()) {
            setSplash()
            Log.e("Splash---->>", "onCreate: Install")
        }
        super.onCreate(savedInstanceState)
        val layout: Int = getLayoutResource()
        binding = DataBindingUtil.setContentView(this, layout)
        val vm = getViewModel()
        binding.setVariable(BR.vm, vm)
        onCreateView()
        isApiHit = false
        lifecycleScope.launch {
            networkMonitor.networkState.collectLatest {
                if (it.isAvailable()) {
                    if (!isApiHit) {
                        apiCall()
                    }
                } else {
                    isApiHit = false
                    showToast("Check internet connection")
                }
            }
        }
        setObserver()
    }

    protected abstract fun apiCall()
    protected abstract fun getLayoutResource(): Int
    protected abstract fun getViewModel(): BaseViewModel
    protected abstract fun onCreateView()
    protected abstract fun setObserver()

    fun showToast(msg: String? = "Something went wrong !!") {
        Toast.makeText(this, msg ?: "Showed null value !!", Toast.LENGTH_SHORT).show()
    }


    fun navigateBack() {
        onBackPressedDispatcher.onBackPressed()
    }

    fun showLoading(s: String?) {
        progressSheet?.dismissAllowingStateLoss()
        progressSheet = ProgressSheet(object : ProgressSheet.BaseCallback {
            override fun onClick(view: View?) {}
            override fun onBind(bind: ViewProgressSheetBinding) {
                progressSheet?.showMessage(s)
            }
        })
        progressSheet?.show(supportFragmentManager, progressSheet?.tag)
    }

    fun onLoading(show: Boolean) {
        val progressBar: View = findViewById(R.id.progress_bar)
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }


    fun hideLoading() {
        progressSheet?.dismissAllowingStateLoss()
        progressSheet = null
        getLoaderView()?.setVariable(BR.show, false)
    }

    protected open fun getLoaderView(): ViewDataBinding? {
        return null
    }

    fun onError(error: Throwable, showErrorView: Boolean) {
        if (showErrorView) {
            showErrorDialog(error.message.toString())
            Log.e("Error-->>", "${error.message}")
        }
    }

    override fun onDestroy() {
        progressSheet?.dismissAllowingStateLoss()
        hideLoading()
        super.onDestroy()
    }

    fun showErrorDialog(errorMessage: String) {
        successDialog = BaseBottomSheetDialog(R.layout.custom_dialog, onBind = { binding ->
            with(binding) {
                tvMessage.text = errorMessage
                btnOk.setSingleClickListener {
                    successDialog?.dismiss()
                }
                btnCancel.hide()
                btnCancel.setSingleClickListener {
                    successDialog?.dismiss()
                }
            }
        }, onCancelListener = {})
        successDialog?.show(supportFragmentManager, "")
    }

    private fun setSplash() {
        splashScreen = installSplashScreen()
        /**keep splash screen on-screen indefinitely.*/
        /*keepSplashScreenIndefinitely()*/

        /**if you want to use custom exit animation.*/
        customSplashAnimator()

        /**keep splash screen when load data viewModel.*/
        splashScreenWhenViewModel()
    }

    /**
     * Use customize exit animation for splash screen.
     */
    private fun customSplashAnimator() {
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val customAnimation = CustomScreenAnimator()
            val animation = customAnimation.slideLeftAnimation(splashScreenView)

            val animatorSet = AnimatorSet()
            animatorSet.duration = TIMER_ANIMATION
            //  animatorSet.interpolator = AnticipateInterpolator()
            animatorSet.playTogether(animation)

            animatorSet.doOnEnd {
                splashScreenView.remove()
            }
            animatorSet.start()
        }
    }

    /**
     * Keep splash screen on-screen indefinitely. This is useful if you're using a custom Activity
     * for routing.
     */
    private fun keepSplashScreenIndefinitely() {
        splashScreen.setKeepOnScreenCondition { true }
    }

    /**
     * Keep splash screen on-screen for longer period. This is useful if you need to load data when
     * splash screen is appearing.
     */
    private fun splashScreenWhenViewModel() {
        val content: View = findViewById(android.R.id.content)
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

    private fun isMain(): Boolean = this.javaClass.simpleName == "MainActivity"
}