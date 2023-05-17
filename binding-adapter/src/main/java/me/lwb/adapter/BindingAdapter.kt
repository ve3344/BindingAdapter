package me.lwb.adapter

import androidx.viewbinding.ViewBinding

/**
 * 基本adapter
 * @param I 元素类型
 * @param V 布局类型
 * @param creator 布局创建
 * @param converter 布局更新
 */
open class BindingAdapter<I : Any, V : ViewBinding>(
    creator: LayoutCreator<V>,
    list: List<I> = ArrayList(),
    converter: LayoutConverter<I, V> = { _, _, _->}
) :
    MultiTypeBindingAdapter<I, V>(
        SingleTypeItemViewMapperStore(ItemViewMapperStore.ItemViewMapper(creator, converter)),
        list
    ){
    constructor(creator: LayoutCreator<V>,
                list: List<I> = ArrayList(),
                converter: LayoutConverter2<I, V>
    ) : this(creator,list,converter.asConverter())
}