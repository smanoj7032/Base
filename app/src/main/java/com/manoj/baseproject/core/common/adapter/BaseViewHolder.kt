package com.manoj.baseproject.core.common.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.manoj.baseproject.core.utils.extension.setSingleClickListener

class BaseViewHolder<Binding : ViewDataBinding, Model>(
    private val binding: Binding,
    private val variableId: Int,
    callbacks: Callbacks<Binding, Model>?,
    private val onBind: ((Binding, Model, Int) -> Unit)? = null
) : RecyclerView.ViewHolder(binding.root) {
    private var currentModel: Model? = null

    init {
        callbacks?.forEach { callback ->
            binding.root.findViewById<View>(callback.id).setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    currentModel?.let { it1 -> callback.block(it1, position, binding) }
                }
            }
        }
    }

    fun bind(model: Model, position: Int) {
        currentModel = model
        binding.setVariable(variableId, model)
        onBind?.invoke(binding, model, position)
        binding.executePendingBindings()
    }
}
