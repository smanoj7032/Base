package com.manoj.baseproject.presentation.common.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.manoj.baseproject.BR
import com.manoj.baseproject.data.local.SharedPrefManager
import com.manoj.baseproject.databinding.ViewProgressSheetBinding
import com.manoj.baseproject.utils.hideKeyboard

abstract class BaseFragment<Binding : ViewDataBinding> : Fragment() {
    val TAG: String = this.javaClass.simpleName
    lateinit var sharedPrefManager: SharedPrefManager
    lateinit var baseContext: Context
    lateinit var binding: Binding
    private var progressSheet: ProgressSheet? = null
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
    override fun onPause() {
        super.onPause()
        activity?.hideKeyboard()
    }


    fun showLoading(@StringRes s: Int) {
        showLoading(getString(s))
    }

    fun showLoading(s: String?) {
        progressSheet?.dismissAllowingStateLoss()
        progressSheet = ProgressSheet(object : ProgressSheet.BaseCallback {
            override fun onClick(view: View?) {}
            override fun onBind(bind: ViewProgressSheetBinding) {
                progressSheet?.showMessage(s);
            }
        })
        progressSheet?.show(childFragmentManager, progressSheet?.tag)

    }

    fun showLoading() {
        //   getLoaderView()?.setVariable(BR.show, true)
    }

    fun hideLoading() {
        progressSheet?.dismissAllowingStateLoss()
        progressSheet = null
        getLoaderView()?.setVariable(BR.show, false)
    }

    protected open fun getLoaderView(): ViewDataBinding? {
        return null;
    }

    override fun onDetach() {
        hideLoading()
        super.onDetach()
    }
}