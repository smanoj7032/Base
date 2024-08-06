package com.manoj.baseproject.core.common.base


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.manoj.baseproject.BR
import com.manoj.baseproject.core.network.helper.SystemVariables
import com.manoj.baseproject.core.utils.Logger
import com.manoj.baseproject.core.utils.extension.hideKeyboard
import com.manoj.baseproject.data.local.SharedPrefManager
import kotlinx.coroutines.launch

abstract class BaseFragment<Binding : ViewDataBinding> : Fragment() {
    val TAG: String = this.javaClass.simpleName
    lateinit var sharedPrefManager: SharedPrefManager
    lateinit var baseContext: Context
    lateinit var binding: Binding
    private var isApiCallMade = true

    val parentActivity: BaseActivity<*>?
        get() = activity as? BaseActivity<*>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val layout: Int = getLayoutResource()
        binding = DataBindingUtil.inflate(layoutInflater, layout, container, false)
        binding.setVariable(BR.vm, getViewModel())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentActivity?.let {
            sharedPrefManager = it.sharedPrefManager
        }
        onCreateView(view, savedInstanceState)
        setObserver()
        lifecycleScope.launch { apiCall() }
        SystemVariables.onNetworkChange = {
            lifecycleScope.launch { apiCall() }
            Logger.e("onNetworkChange", "${this.javaClass.simpleName}------>> $it")
        }
    }

    protected abstract fun getLayoutResource(): Int
    protected abstract fun getViewModel(): BaseViewModel
    protected abstract fun onCreateView(view: View, saveInstanceState: Bundle?)
    protected abstract fun setObserver()
    protected abstract suspend fun apiCall()

    override fun onPause() {
        super.onPause()
        activity?.hideKeyboard()
    }

    fun onLoading(show: Boolean) {
        parentActivity?.onLoading(show)
    }

    fun onError(errorMessage: String?, showErrorView: Boolean) = errorMessage?.let { msg ->
        parentActivity?.onError(msg, showErrorView)
        Log.e("Error-->>", msg)
    }
}
