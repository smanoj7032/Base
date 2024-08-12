package com.manoj.baseproject.core.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class BaseAdapter<Binding : ViewDataBinding, Model>(
    private val layoutId: Int,
    private val variableId: Int, private val emptyView: View? = null,
    private val callbacks: Callbacks<Binding, Model>? = null,
    private val onBind: ((Binding, Model, Int) -> Unit)? = null
) : RecyclerView.Adapter<BaseViewHolder<Binding, Model>>() {

    private var dataList: ArrayList<Model> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<Binding, Model> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<Binding>(inflater, layoutId, parent, false)
        return BaseViewHolder(binding, variableId, callbacks, onBind)
    }

    override fun getItemCount(): Int = dataList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    var list: List<Model>?
        get() = dataList
        set(newDataList) {
            updateList(newDataList ?: emptyList())
        }

    fun replaceList(subList: ArrayList<Model>) {
        updateList(subList)
        checkEmptyView()
    }

    fun getItemList(): ArrayList<Model> = dataList

    fun getItem(position: Int): Model = dataList[position]

    fun clearList() {
        dataList.clear()
        checkEmptyView()
        notifyDataSetChanged()
    }

    fun removeItem(i: Int) {
        if (i in 0 until dataList.size) {
            dataList.removeAt(i)
            notifyItemRemoved(i)
            notifyItemRangeChanged(i, dataList.size)
        }
        checkEmptyView()
    }

    fun addItemAt(position: Int, model: Model) {
        if (position in 0..dataList.size) {
            dataList.add(position, model)
            notifyItemInserted(position)
        }
    }

    fun addItem(model: Model) {
        dataList.add(model)
        notifyItemInserted(dataList.size - 1)
        checkEmptyView()
    }

    fun updateItem(data: Model, position: Int) {
        if (position in 0 until dataList.size) {
            dataList[position] = data
            notifyItemChanged(position)
        }
    }

    fun replaceItemAt(position: Int, model: Model) {
        if (position in 0 until dataList.size) {
            dataList[position] = model
            notifyItemChanged(position)
        }
    }

    fun notifyItemChangedAt(position: Int) {
        notifyItemChanged(position)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                dataList.add(i + 1, dataList.removeAt(i))
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                dataList.add(i - 1, dataList.removeAt(i))
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Binding, Model>, position: Int) {
        holder.bind(dataList[position], position)
    }

    private fun updateList(newDataList: List<Model>) {
        val diffCallback = DiffCallback(dataList, newDataList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        dataList.clear()
        dataList.addAll(newDataList)
        diffResult.dispatchUpdatesTo(this)
        checkEmptyView()
    }

    class DiffCallback<Model>(private val oldList: List<Model>, private val newList: List<Model>) :
        DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    private fun checkEmptyView() {
        emptyView?.visibility = if (dataList.isEmpty()) View.VISIBLE else View.GONE
    }
}