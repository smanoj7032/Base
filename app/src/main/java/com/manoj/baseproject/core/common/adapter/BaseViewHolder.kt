package com.manoj.baseproject.core.common.adapter

import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
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
    private fun setAnimation(viewToAnimate: View) {
        if (viewToAnimate.animation == null) {
            val anim = ScaleAnimation(
                0.0f, 1.0f,
                0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            anim.duration = 300
            viewToAnimate.startAnimation(anim)
        }
    }
}
