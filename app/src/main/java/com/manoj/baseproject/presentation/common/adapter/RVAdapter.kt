package com.manoj.baseproject.presentation.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


abstract class RVAdapter<M, B : ViewDataBinding>(
    private val layoutResId: Int,
    private val modelVariableId: Int,
    ) : RecyclerView.Adapter<RVAdapter.Holder<B>>() {

    private val dataList: MutableList<M> = ArrayList()

    class Holder<S : ViewDataBinding>(var binding: S) : RecyclerView.ViewHolder(binding.root)

    fun addData(data: M) {
        val positionStart = dataList.size
        dataList.add(data)
        notifyItemInserted(positionStart)
    }

    fun clearList() {
        dataList.clear()
        notifyDataSetChanged()
    }

    fun updateItem(data: M, position: Int) {
        if (position >= 0 && position < dataList.size) {
            dataList[position] = data
            notifyItemChanged(position)
        }
    }
    var list: List<M>?
        get() = dataList
        set(newDataList) {
            val currentSize: Int = dataList.size
            dataList.clear()
            notifyItemRangeRemoved(0, currentSize)
            if (newDataList != null) dataList.addAll(newDataList)
            notifyItemRangeInserted(0, dataList.size)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder<B> {
        val binding: B =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutResId, parent, false)
        return Holder(binding)
    }

    override fun getItemCount()= dataList.size


    override fun onBindViewHolder(holder: Holder<B>, position: Int) {
        onBind(holder.binding, dataList[position], position)
        setAnimation(holder.binding.root)
        holder.binding.executePendingBindings()
    }
    open fun onBind(binding: B, bean: M, position: Int) {
        binding.setVariable(modelVariableId, bean)
    }

    override fun getItemViewType(position: Int)=
        position
    private fun setAnimation(viewToAnimate: View) {
        val anim = ScaleAnimation(
            0.0f,
            1.0f,
            0.0f,
            1.0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        anim.duration = 300
        viewToAnimate.startAnimation(anim)
    }
}
