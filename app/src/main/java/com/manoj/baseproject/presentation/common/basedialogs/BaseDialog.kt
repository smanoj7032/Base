package com.manoj.baseproject.presentation.common.basedialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment

open class BaseDialog<T>() : DialogFragment() where T : ViewDataBinding {

    private var layoutRes: Int = 0
    var onBind: (binding: T) -> Unit = { _ -> }
    var onCancelListener: () -> Unit = {}

    constructor(
        @LayoutRes layoutRes: Int,
        onBind: (binding: T) -> Unit,
        onCancelListener: () -> Unit = {}
    ) : this() {
        this.layoutRes = layoutRes
        this.onBind = onBind
        this.onCancelListener = onCancelListener
    }

    lateinit var binding: ViewDataBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireActivity(), theme) {

        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            it.setOnCancelListener {
                onCancelListener()
            }
            it.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
            binding.root
        } catch (e: Exception) {
            Log.e(
                "BaseDialogException",
                "Inflating Error, You had forget to convert your layout to data binding layout"
            )
            null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            onBind(binding as T)
        } catch (e: Exception) {
            Log.e(
                "BaseDialogException",
                "Casting error, please make sure you're passing the right binding class",
            )
        }
    }
}