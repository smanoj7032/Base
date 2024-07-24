package com.manoj.baseproject.presentation.common.adapter

import androidx.databinding.ViewDataBinding

class CallBackModel<Binding : ViewDataBinding, Model>(
    val id: Int,
    val block: (model: Model, position: Int, binding: Binding) -> Unit
)

typealias Callbacks<Binding, Model> = ArrayList<CallBackModel<Binding, Model>>
