package com.manoj.baseproject.core.common.basedialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.manoj.baseproject.R

open class BaseBottomSheetDialog<Binding : ViewDataBinding>(
    context: Context,
    @LayoutRes private val layoutRes: Int,
    private val onBind: (binding: Binding) -> Unit
) : BottomSheetDialog(context, R.style.BottomSheetDialogTheme) {

    private lateinit var binding: Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutRes, null, false)
        setContentView(binding.root)

        onBind(binding)

        setOnShowListener {
            val bottomSheet = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.apply {
                background = ContextCompat.getDrawable(context, R.drawable.bg_sheet_white)
                val behavior = BottomSheetBehavior.from(this)
                behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }
}