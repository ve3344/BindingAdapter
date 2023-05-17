package me.lwb.bindinadapter.paging

import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import me.lwb.adapter.paging.ext.DiffCallbackUtils
import me.lwb.adapter.ItemViewMapperStore
import me.lwb.adapter.paging.MultiTypePagingBindingAdapter

/**
 * Created by ve3344@qq.com.
 */
/**
 * 使用配置创建多布局分页Adapter
 */
fun <I : Any, V : ViewBinding> ItemViewMapperStore<I, V>.asPagingAdapter(
    dc: DiffUtil.ItemCallback<I> = DiffCallbackUtils.itemCallback()
) =
    MultiTypePagingBindingAdapter(this, dc)