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
import com.manoj.baseproject.R
import com.manoj.baseproject.core.network.helper.SystemVariables
import com.manoj.baseproject.core.utils.Logger
import com.manoj.baseproject.core.utils.extension.Ids
import com.manoj.baseproject.core.utils.extension.hide
import com.manoj.baseproject.core.utils.extension.show
import com.manoj.baseproject.core.utils.extension.showErrorToast
import com.manoj.baseproject.core.utils.extension.slideIn
import com.manoj.baseproject.core.utils.extension.slideOut
import com.manoj.baseproject.data.local.SharedPrefManager
import kotlinx.coroutines.launch
import javax.inject.Inject

/** ENJOY CODING */
abstract class BaseActivity<Binding : ViewDataBinding> : AppCompatActivity() {
    val emptyView: View by lazy { findViewById(Ids.emptyView) }
    val container: FragmentContainerView by lazy { findViewById(Ids.container) }
    val btnRetry: Button by lazy { findViewById(Ids.btnRetry) }
    val tvErrorText: TextView by lazy { findViewById(Ids.tvErrorText) }
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
        binding.setVariable(BR.vm, getViewModel())
        lifecycleScope.launch { apiCall() }
        SystemVariables.onNetworkChange = {
            Logger.e("onNetworkChange", "Activity------>> $it")
            lifecycleScope.launch { apiCall() }
        }
        onCreateView()
        setObserver()
    }

    protected abstract suspend fun apiCall()
    protected abstract fun getLayoutResource(): Int
    protected abstract fun getViewModel(): BaseViewModel
    protected abstract fun onCreateView()
    protected abstract fun setObserver()


    protected open fun getLoaderView(): ViewDataBinding? {
        return null
    }

    fun onError(errorMessage: String?, showErrorView: Boolean) {
        if (showErrorView) showErrorToast(errorMessage)
    }

    fun onLoading(show: Boolean) {
        val progressBar: View = findViewById(R.id.progress_bar)
        if (show) {
            progressBar.show()
        } else {
            progressBar.hide()
        }
    }

    private fun isMain(): Boolean = this.javaClass.simpleName == "MainActivity"

}
