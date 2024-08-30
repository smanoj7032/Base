package com.manoj.base.core.common.base

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import com.manoj.base.BR
import com.manoj.base.core.network.helper.NetworkMonitor
import com.manoj.base.core.network.helper.SystemVariables
import com.manoj.base.core.utils.Logger
import com.manoj.base.core.utils.dispatchers.DispatchersProvider
import com.manoj.base.core.utils.extension.Ids
import com.manoj.base.core.utils.extension.showErrorToast
import com.manoj.base.data.local.DataStoreManager
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
    lateinit var credentialManager: CredentialManager

    @Inject
    lateinit var dataStoreManager: DataStoreManager
    @Inject
    lateinit var dispatchersProvider: DispatchersProvider

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
        lifecycleScope.launch { setObserver() }
    }

    protected abstract suspend fun apiCall()
    protected abstract fun getLayoutResource(): Int
    protected abstract fun onCreateView()
    protected abstract suspend fun setObserver()
    protected open fun getLoaderView(): ViewDataBinding? = binding
    fun onError(errorMessage: String?, showErrorView: Boolean) {
        if (showErrorView) showErrorToast(errorMessage)
    }

    fun onLoading(show: Boolean) = getLoaderView()?.setVariable(BR.show, show)

    private fun isMain(): Boolean = this.javaClass.simpleName == "MainActivity"

    /** override fun dispatchTouchEvent(event: MotionEvent): Boolean {
    if (event.action == MotionEvent.ACTION_DOWN) {
    val v = currentFocus
    if (v is EditText) {
    val outRect = Rect()
    v.getGlobalVisibleRect(outRect)
    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
    v.clearFocus()
    val imm =
    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
    }
    }
    }
    return super.dispatchTouchEvent(event)
    }*/
}
