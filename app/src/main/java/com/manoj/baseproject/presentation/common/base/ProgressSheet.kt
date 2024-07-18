package com.manoj.baseproject.presentation.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.manoj.baseproject.BR
import com.manoj.baseproject.databinding.ViewProgressSheetBinding

class ProgressSheet(private val callback: BaseCallback) : BottomSheetDialogFragment() {
    private lateinit var binding: ViewProgressSheetBinding
    private var message: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewProgressSheetBinding.inflate(inflater)
        binding.setVariable(BR.callback, callback)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callback.onBind(binding)
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        binding.tvMessage.text = message
        binding.show = true
    }

    fun showMessage(s: String?) {
        this.message = s;
    }

    interface BaseCallback {
        fun onClick(view: View?)
        fun onBind(bind: ViewProgressSheetBinding)
    }

}