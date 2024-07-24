package com.manoj.baseproject.presentation.common.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewHolder<Binding : ViewDataBinding>(var binding: Binding) :
    RecyclerView.ViewHolder(binding.root) {
    fun <Model> bindTo(variableId: Int, model: Model, onBind: ((Binding, Model) -> Unit)? = null) {
        binding.setVariable(variableId, model)
        onBind?.invoke(binding, model)
        binding.executePendingBindings()
    }

    fun <Model> bindClickListener(model: Model, callbacks: Callbacks<Binding, Model>?) {
        callbacks?.forEach { callback ->
            binding.root.findViewById<View>(callback.id).setOnClickListener {
                callback.block(model, bindingAdapterPosition, binding)
            }
        }
    }
}