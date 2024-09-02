package com.manoj.base.core.common.base

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.credentials.CredentialManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.manoj.base.BR
import com.manoj.base.core.network.helper.NetworkMonitor
import com.manoj.base.core.network.helper.SystemVariables
import com.manoj.base.core.utils.Logger
import com.manoj.base.core.utils.dispatchers.DispatchersProvider
import com.manoj.base.core.utils.extension.Ids
import com.manoj.base.core.utils.extension.Str
import com.manoj.base.core.utils.extension.hide
import com.manoj.base.core.utils.extension.setSingleClickListener
import com.manoj.base.core.utils.extension.show
import com.manoj.base.core.utils.extension.showErrorToast
import com.manoj.base.core.utils.extension.showToast
import com.manoj.base.data.local.DataStoreManager
import kotlinx.coroutines.launch
import javax.inject.Inject

/** ENJOY CODING */
abstract class BaseActivity<Binding : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {
    abstract val viewModel: VM
    val container: FragmentContainerView by lazy { findViewById(Ids.container) }

    val tvErrorText: TextView by lazy { findViewById(Ids.tvError) }
    val swipeRefreshLayout: SwipeRefreshLayout by lazy { findViewById(Ids.swipeRefreshLayout) }
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
        onCreateView()
        lifecycleScope.launch { setObserver() }
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    protected abstract suspend fun apiCall()
    protected abstract fun getLayoutResource(): Int
    protected abstract fun onCreateView()
    protected abstract suspend fun setObserver()
    protected open fun getLoaderView(): ViewDataBinding? = binding
    fun onError(errorMessage: String?, showErrorView: Boolean) {
        tvErrorText.visibility=View.VISIBLE
        tvErrorText.text = errorMessage
    }

    fun onLoading(show: Boolean) {
        getLoaderView()?.setVariable(BR.show, show)
        tvErrorText.visibility=View.GONE
    }

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
