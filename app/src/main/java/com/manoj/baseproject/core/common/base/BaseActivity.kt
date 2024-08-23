package com.manoj.baseproject.core.common.base

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import com.manoj.baseproject.BR
import com.manoj.baseproject.core.network.helper.NetworkMonitor
import com.manoj.baseproject.core.network.helper.SystemVariables
import com.manoj.baseproject.core.utils.Logger
import com.manoj.baseproject.core.utils.extension.Ids
import com.manoj.baseproject.core.utils.extension.showErrorToast
import com.manoj.baseproject.data.local.SharedPrefManager
import kotlinx.coroutines.launch
import javax.inject.Inject

/** ENJOY CODING */
abstract class BaseActivity<Binding : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {
    abstract val viewModel: VM
    val emptyView: View by lazy { findViewById(Ids.emptyView) }
    val container: FragmentContainerView by lazy { findViewById(Ids.container) }
    val btnRetry: Button by lazy { findViewById(Ids.btnRetry) }
    val tvErrorText: TextView by lazy { findViewById(Ids.tvErrorText) }
    private val progressBar: View by lazy { findViewById(Ids.progress_bar) }
    private val splashManager: SplashManager by lazy { SplashManager(this, TIMER_ANIMATION) }
    lateinit var binding: Binding
    private val TIMER_ANIMATION: Long = 400

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isMain()) splashManager.setupSplashScreen()
        binding = DataBindingUtil.setContentView(this, getLayoutResource())
        binding.setVariable(BR.vm, viewModel)
        lifecycleScope.launch { apiCall() }
        SystemVariables.onNetworkChange = {
            Logger.e("onNetworkChange", "Activity------>> $it")
            when (it) {
                NetworkMonitor.NetworkState.Available -> {
                    lifecycleScope.launch { apiCall() }
                }

                NetworkMonitor.NetworkState.Lost -> {
                    onLoading(false)
                }
            }
        }
        onCreateView()
        setObserver()
    }

    protected abstract suspend fun apiCall()
    protected abstract fun getLayoutResource(): Int
    protected abstract fun onCreateView()
    protected abstract fun setObserver()
    protected open fun getLoaderView(): ViewDataBinding? = binding
    fun onError(errorMessage: String?, showErrorView: Boolean) {
        if (showErrorView) showErrorToast(errorMessage)
    }

    fun onLoading(show: Boolean) = getLoaderView()?.setVariable(BR.show, show)

    private fun isMain(): Boolean = this.javaClass.simpleName == "MainActivity"

}
