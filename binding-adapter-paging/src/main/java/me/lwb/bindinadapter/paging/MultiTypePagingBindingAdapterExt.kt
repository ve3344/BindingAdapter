package me.lwb.bindinadapter.paging

import androidx.recyclerview.widget.DiffUtil
import me.lwb.bindingadapter.MultiTypeAdapterConfig

/**
 * Created by luowenbin on 2021/10/14.
 */
/**
 * 使用配置创建多布局分页Adapter
 */
fun <I : Any> MultiTypeAdapterConfig<I>.asPagingAdapter(
    dc: DiffUtil.ItemCallback<I> = DiffCallbackUtils.create()
) =
    MultiTypePagingBindingAdapter(this, dc)
