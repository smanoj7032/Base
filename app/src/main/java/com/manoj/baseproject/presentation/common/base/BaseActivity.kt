package com.manoj.baseproject.presentation.common.base

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.manoj.baseproject.BR
import com.manoj.baseproject.data.local.SharedPrefManager
import com.manoj.baseproject.databinding.ViewProgressSheetBinding
import com.manoj.baseproject.network.helper.NetworkMonitor
import com.manoj.baseproject.utils.showErrorToast
import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class BaseActivity<Binding : ViewDataBinding> : AppCompatActivity(),
    NetworkObserver.NetworkStateListener {
    private val splashManager: SplashManager by lazy { SplashManager(this, TIMER_ANIMATION) }
    private val networkObserver: NetworkObserver by lazy { NetworkObserver(networkMonitor, lifecycleScope) }
    lateinit var binding: Binding
    private var progressSheet: ProgressSheet? = null
    private val TIMER_ANIMATION: Long = 400

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isMain()) splashManager.setupSplashScreen()
        binding = DataBindingUtil.setContentView(this, getLayoutResource())
        binding.setVariable(BR.vm, getViewModel())
        networkObserver.addListener(this)
        networkObserver.observeNetworkChanges()

        onCreateView()
        setObserver()
    }

    protected abstract suspend fun apiCall()
    protected abstract fun getLayoutResource(): Int
    protected abstract fun getViewModel(): BaseViewModel
    protected abstract fun onCreateView()
    protected abstract fun setObserver()

    override fun onNetworkAvailable() {
        lifecycleScope.launch { apiCall() }
    }

    override fun onNetworkLost() {
        showErrorToast("No internet connection.")
    }

    fun showLoading(message: String?) {
        progressSheet?.dismissAllowingStateLoss()
        progressSheet = ProgressSheet(object : ProgressSheet.BaseCallback {
            override fun onClick(view: View?) {}
            override fun onBind(bind: ViewProgressSheetBinding) {
                progressSheet?.showMessage(message)
            }
        })
        progressSheet?.show(supportFragmentManager, progressSheet?.tag)
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
            showErrorToast(error.message.toString())
        }
    }

    override fun onDestroy() {
        progressSheet?.dismissAllowingStateLoss()
        networkObserver.removeListener(this)
        super.onDestroy()
    }

    private fun isMain(): Boolean = this.javaClass.simpleName == "MainActivity"
}
