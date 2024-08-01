package com.manoj.baseproject.presentation.common.base


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
import com.manoj.baseproject.R
import com.manoj.baseproject.data.local.SharedPrefManager
import com.manoj.baseproject.network.helper.NetworkMonitor
import com.manoj.baseproject.utils.hideKeyboard
import com.manoj.baseproject.utils.showErrorToast
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseFragment<Binding : ViewDataBinding> : Fragment(), NetworkObserver.NetworkStateListener {
    val TAG: String = this.javaClass.simpleName
    lateinit var sharedPrefManager: SharedPrefManager
    lateinit var baseContext: Context
    lateinit var binding: Binding

    private lateinit var networkObserver: NetworkObserver

    @Inject
    lateinit var networkMonitor: NetworkMonitor

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
        networkObserver = NetworkObserver(networkMonitor, lifecycleScope)
        networkObserver.addListener(this)
        networkObserver.observeNetworkChanges()

        onCreateView(view, savedInstanceState)
        setObserver()
    }

    protected abstract fun getLayoutResource(): Int
    protected abstract fun getViewModel(): BaseViewModel
    protected abstract fun onCreateView(view: View, saveInstanceState: Bundle?)
    protected abstract fun setObserver()
    protected abstract suspend fun apiCall()

    override fun onNetworkAvailable() {
        networkObserver.addPendingAction {
            lifecycleScope.launch {
                apiCall()
            }
        }
    }

    override fun onNetworkLost() {
        parentActivity?.showErrorToast("No internet connection.")
    }

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
            parentActivity?.showErrorToast(error.message.toString())
            Log.e("Error-->>", "${error.message}")
        }
    }

    override fun onDestroyView() {
        networkObserver.removeListener(this)
        super.onDestroyView()
    }
}
