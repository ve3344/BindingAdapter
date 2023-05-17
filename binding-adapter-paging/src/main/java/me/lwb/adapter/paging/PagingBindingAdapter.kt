package me.lwb.adapter.paging

import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import me.lwb.adapter.paging.ext.DiffCallbackUtils
import me.lwb.adapter.ItemViewMapperStore
import me.lwb.adapter.SingleTypeItemViewMapperStore

/**
 * 分页adapter
 * @param creator 布局创建
 * @param dc DiffUtil.ItemCallback<I>
 * @param converter 布局更新
 */
open class PagingBindingAdapter<I : Any, V : ViewBinding>(
    creator: LayoutCreator<V>,
    diffCallback: DiffUtil.ItemCallback<I> = DiffCallbackUtils.itemCallback(),
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    workerDispatcher: CoroutineDispatcher = Dispatchers.Default,
    converter: LayoutConverter<I, V> = { _, _, _->},
) :
    MultiTypePagingBindingAdapter<I, V>(
        SingleTypeItemViewMapperStore(
            ItemViewMapperStore.ItemViewMapper(
                creator,
                converter
            )
        ), diffCallback, mainDispatcher, workerDispatcher
    ) {
    constructor(
        creator: LayoutCreator<V>,
        diffCallback: DiffUtil.ItemCallback<I> = DiffCallbackUtils.itemCallback(),
        mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
        workerDispatcher: CoroutineDispatcher = Dispatchers.Default,
        converter: LayoutConverter2<I, V>,
    ) : this(creator, diffCallback, mainDispatcher, workerDispatcher, converter.asConverter())
}