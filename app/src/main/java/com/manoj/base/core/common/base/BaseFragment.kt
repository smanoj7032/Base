package com.manoj.base.core.common.base


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
import com.manoj.base.BR
import com.manoj.base.core.utils.dispatchers.DispatchersProvider
import com.manoj.base.core.utils.extension.Drw
import com.manoj.base.core.utils.extension.hideKeyboard
import com.manoj.base.core.utils.picker.MediaModel
import com.manoj.base.core.utils.picker.MediaType
import com.manoj.base.core.utils.picker.PickerDialogHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseFragment<Binding : ViewDataBinding, VM : BaseViewModel> : Fragment() {
    @Inject
    lateinit var dispatchersProvider: DispatchersProvider
    val TAG: String = this.javaClass.simpleName
    lateinit var baseContext: Context
    lateinit var binding: Binding
    abstract val viewModel: VM
    private var isApiCallMade = true
    lateinit var picker: PickerDialogHelper
    val parentActivity: BaseActivity<*, *>?
        get() = activity as? BaseActivity<*, *>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val layout: Int = getLayoutResource()
        binding = DataBindingUtil.inflate(layoutInflater, layout, container, false)
        binding.setVariable(BR.vm, viewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateView(view, savedInstanceState)
        parentActivity?.setupRetryButton { onRetry()}
        lifecycleScope.launch { apiCall() }
        viewLifecycleOwner.lifecycleScope.launch { setObserver() }
    }

    protected abstract fun getLayoutResource(): Int
    protected abstract fun onCreateView(view: View, saveInstanceState: Bundle?)
    protected abstract suspend fun setObserver()
    protected abstract suspend fun apiCall()
    protected abstract  fun onRetry()
    protected open fun getLoaderView(): ViewDataBinding? = binding
    override fun onPause() {
        super.onPause()
        activity?.hideKeyboard()
    }

    protected fun onLoading(show: Boolean) = getLoaderView()?.setVariable(BR.show, show)

    fun onError(errorMessage: String?, showErrorView: Boolean) = errorMessage?.let { msg ->
        parentActivity?.onError(msg, showErrorView)
        Log.e("Error-->>", msg)
    }

    override fun onDestroy() {
        super.onDestroy()
        parentActivity?.clearRetryButtonListener()
    }
}
