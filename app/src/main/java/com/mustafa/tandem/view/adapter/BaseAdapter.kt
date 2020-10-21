package com.mustafa.tandem.view.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T, V : ViewDataBinding> :
    RecyclerView.Adapter<BaseViewHolder<V>>() {

    private val itemsList = ArrayList<T>()

    fun submitList(newItems: List<T>) {
        val oldSize = itemsList.size
        itemsList.addAll(newItems)
        val newSize = itemsList.size
        notifyItemRangeChanged(oldSize, newSize - 1)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<V> {
        val binding = createBinding(parent)
        return BaseViewHolder(binding)
    }

    protected abstract fun createBinding(parent: ViewGroup): V

    override fun onBindViewHolder(holder: BaseViewHolder<V>, position: Int) {
        bind(holder.binding, itemsList[position])
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    protected abstract fun bind(binding: V, item: T)
}