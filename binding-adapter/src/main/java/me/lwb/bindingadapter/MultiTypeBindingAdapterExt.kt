package me.lwb.bindingadapter

/**
 * Created by luowenbin on 2021/10/14.
 */
/**
 * 使用配置创建多布局Adapter
 */
fun <I : Any> MultiTypeAdapterConfig<I>.asAdapter(list: List<I> = ArrayList()) =
    MultiTypeBindingAdapter(this, list)