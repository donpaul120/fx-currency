package com.fx.exchange_experiment.core.views.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.fx.exchange_experiment.BR

/**
 * @author Paul Okeke
 */

abstract class EXDataBindingAdapter<T>(private val list: ArrayList<T>): RecyclerView.Adapter<EXDataBindingAdapter.EXViewHolder<T>>() {

    open fun dispatchUpdates(list:List<T>) {
        this.list.clear()
        this.list.addAll(list)
        notifyItemRangeChanged(0, list.size)
    }

    fun getCurrentList() = list

    override fun getItemCount(): Int = list.size

    open fun getHandler(): ListItemHandler<T>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EXViewHolder<T> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
//        getHandler()?.apply { binding.setVariable(BR.handler, this) }
        return EXViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EXViewHolder<T>, position: Int) {
        list[position]?.let { holder.bind(it) }
    }

    /**
     * EXViewHolder
     */
    class EXViewHolder<T>(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T) {
            binding.setVariable(BR.item, item)
            binding.setVariable(BR.position, this.adapterPosition)
            binding.executePendingBindings()
        }
    }

}