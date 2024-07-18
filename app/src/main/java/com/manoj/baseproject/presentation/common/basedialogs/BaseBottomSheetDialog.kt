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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BaseBottomSheetDialog<T>() : BottomSheetDialogFragment() where T : ViewDataBinding {

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
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                behavior.skipCollapsed = true
            }
        }
        return dialog
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
            e.printStackTrace()
            Log.e(
                "AnimatedBottomSheetDialog",
                "Inflating Error, You had forgotten to convert your layout to a data binding layout"
            )
            null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            onBind(binding as T)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(
                "AnimatedBottomSheetDialog",
                "Casting error, please make sure you're passing the right binding class",
            )
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            it.setOnCancelListener {
                onCancelListener()
            }
            it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}
