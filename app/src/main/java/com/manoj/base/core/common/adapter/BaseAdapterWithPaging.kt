package com.manoj.base.core.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil


open class BaseAdapterWithPaging<M : Any, B : ViewDataBinding>(
    diffCallback: DiffUtil.ItemCallback<M>,
    private val layoutResId: Int,
    private val variableId: Int,
    private val callbacks: Callbacks<B, M>? = null,
) : PagingDataAdapter<M, BaseViewHolder<B, M>>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<B, M> {
        val binding: B = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layoutResId,
            parent,
            false
        )
        return BaseViewHolder(binding, variableId, callbacks)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<B, M>, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(item, position)
        }
    }

    companion object {
        inline fun <reified T : Any> createDiffCallback(
            crossinline compareFunction: (T, T) -> Boolean
        ): DiffUtil.ItemCallback<T> {
            return object : DiffUtil.ItemCallback<T>() {
                override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                    return compareFunction(oldItem, newItem)
                }

                override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                    return compareFunction(oldItem, newItem)
                }
            }
        }
    }

    private fun getItemAtPosition(position: Int): M? {
        return getItem(position)
    }

    fun attachLoadStateListener(
        onLoading: (Boolean) -> Unit,
        onError: (String?, Boolean) -> Unit
    ) {
        this.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading) {
                onLoading(itemCount < 1)
            } else {
                onLoading(false)
                val errorState = when {
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                errorState?.let {
                    if (itemCount < 1) {
                        onError(it.error.message, true)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
