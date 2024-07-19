package com.manoj.baseproject.presentation.common.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.manoj.baseproject.BR
import com.manoj.baseproject.R
import com.manoj.baseproject.data.local.SharedPrefManager
import com.manoj.baseproject.databinding.ViewProgressSheetBinding
import com.manoj.baseproject.utils.hideKeyboard
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseFragment<Binding : ViewDataBinding> : Fragment() {
    val TAG: String = this.javaClass.simpleName
    lateinit var sharedPrefManager: SharedPrefManager
    lateinit var baseContext: Context
    lateinit var binding: Binding
    private var isApiHit = false

    val parentActivity: BaseActivity<*>?
        get() = activity as? BaseActivity<*>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentActivity?.let {
            sharedPrefManager = it.sharepref
        }
        onCreateView(view, savedInstanceState)
        isApiHit = false
        lifecycleScope.launch {
            parentActivity?.networkMonitor?.networkState?.collectLatest {
                if (it.isAvailable()) {
                    if (!isApiHit) {
                        apiCall()
                    }
                } else {
                    isApiHit = false
                    parentActivity?.showToast("Check internet connection")
                }
            }
        }
        setObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val layout: Int = getLayoutResource()
        binding = DataBindingUtil.inflate(layoutInflater, layout, container, false)
        binding.setVariable(BR.vm, getViewModel())
        return binding.root
    }

    protected abstract fun getLayoutResource(): Int
    protected abstract fun getViewModel(): BaseViewModel
    protected abstract fun onCreateView(view: View, saveInstanceState: Bundle?)
    protected abstract fun setObserver()
    protected abstract fun apiCall()

    override fun onPause() {
        super.onPause()
        activity?.hideKeyboard()
    }

    fun onLoading(show: Boolean) {
        val progressBar: View = requireActivity().findViewById(R.id.progress_bar)
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
    fun onError(error: Throwable, showErrorView: Boolean) {
        if (showErrorView) {
            parentActivity?.showErrorDialog(error.message.toString())
            Log.e("Error-->>", "${error.message}")
        }
    }
}