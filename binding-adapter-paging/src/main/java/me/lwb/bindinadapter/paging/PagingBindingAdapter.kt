package me.lwb.bindinadapter.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import me.lwb.bindingadapter.BindingViewHolder

/**
 * 分页adapter
 * @param creator 布局创建
 * @param dc DiffUtil.ItemCallback<I>
 * @param converter 布局更新
 */
open class PagingBindingAdapter<I : Any, V : ViewBinding>(
    val creator: LayoutCreator<V>,
    diffCallback: DiffUtil.ItemCallback<I> = DiffCallbackUtils.create(),
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    workerDispatcher: CoroutineDispatcher = Dispatchers.Default,
    val converter: LayoutConverter<I, V>
) :
    PagingDataAdapter<I, BindingViewHolder<V>>(diffCallback,mainDispatcher, workerDispatcher)  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<V> {
        val binding = creator(LayoutInflater.from(parent.context), parent, false)
        return BindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<V>, position: Int) {
        val item: I = getItem(position) ?: return
        holder.converter( position, item)
    }

}