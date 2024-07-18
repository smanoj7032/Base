package com.manoj.baseproject.presentation.common.base

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.manoj.baseproject.BR
import com.manoj.baseproject.MyApplication
import com.manoj.baseproject.R
import com.manoj.baseproject.data.local.SharedPrefManager
import com.manoj.baseproject.databinding.CustomDialogBinding
import com.manoj.baseproject.databinding.ViewProgressSheetBinding
import com.manoj.baseproject.network.helper.NetworkMonitor
import com.manoj.baseproject.presentation.common.basedialogs.BaseBottomSheetDialog
import com.manoj.baseproject.utils.hide
import com.manoj.baseproject.utils.setSingleClickListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class BaseActivity<Binding : ViewDataBinding> : AppCompatActivity() {
    @Inject
    lateinit var sharepref: SharedPrefManager
    val TAG: String = this.javaClass.simpleName
    private var progressSheet: ProgressSheet? = null
    open val onRetry: (() -> Unit)? = null
    lateinit var binding: Binding
    val app: MyApplication
        get() = application as MyApplication
    private val TIMER_ANIMATION: Long = 1200
    private var successDialog: BaseBottomSheetDialog<CustomDialogBinding>? = null
    private var isApiHit = false

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
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

    private fun showErrorDialog(errorMessage: String) {
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
}