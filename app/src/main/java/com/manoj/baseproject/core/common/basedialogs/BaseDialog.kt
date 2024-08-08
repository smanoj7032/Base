package com.manoj.baseproject.core.common.basedialogs

import android.app.Dialog
import android.content.Context
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

open class BaseDialog<T>(
    context: Context,
    private var layoutRes: Int = 0,
    var onBind: (binding: T) -> Unit,
    var onCancelListener: () -> Unit
) : Dialog(context) where T : ViewDataBinding {


    lateinit var binding: T


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutRes, null, false)
        setContentView(binding.root)
        onBind(binding)
        setOnCancelListener {
            onCancelListener()
        }
        setOnCancelListener {
            onCancelListener()
        }
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }
}