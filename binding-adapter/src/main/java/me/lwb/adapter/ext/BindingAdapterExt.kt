package me.lwb.adapter.ext

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import me.lwb.adapter.BindingViewHolder
import me.lwb.adapter.IBindingAdapter
import me.lwb.adapter.ItemViewMapperStore
import me.lwb.adapter.MultiTypeBindingAdapter
import kotlin.experimental.ExperimentalTypeInference

/**
 * Created by ve3344@qq.com.
 */
/**
 * 监听onBindViewHolder
 */
fun <V : ViewBinding> IBindingAdapter<V>.doAfterBindViewHolder(listener: (holder: BindingViewHolder<V>, position: Int) -> Unit): IBindingAdapter<V> {
    val onBindViewHolderDelegateOrigin = onBindViewHolderDelegate
    onBindViewHolderDelegate = { holder, position, p ->
        onBindViewHolderDelegateOrigin(holder, position, p)
        listener(holder, position)
    }
    return this
}

/**
 * 监听onBindViewHolder
 */
fun <V : ViewBinding> IBindingAdapter<V>.doBeforeBindViewHolder(listener: (holder: BindingViewHolder<V>, position: Int) -> Unit): IBindingAdapter<V> {
    val onBindViewHolderDelegateOrigin = onBindViewHolderDelegate
    onBindViewHolderDelegate = { holder, position, p ->
        listener(holder, position)
        onBindViewHolderDelegateOrigin(holder, position, p)
    }
    return this
}

/**
 * 监听onBindViewHolder
 */
fun <V : ViewBinding> IBindingAdapter<V>.doAfterBindViewHolder(listener: (holder: BindingViewHolder<V>, position: Int, payloads: List<Any>) -> Unit): IBindingAdapter<V> {
    val onBindViewHolderDelegateOrigin = onBindViewHolderDelegate
    onBindViewHolderDelegate = { holder, position, p ->
        onBindViewHolderDelegateOrigin(holder, position, p)
        listener(holder, position, p)
    }
    return this
}

/**
 * 监听onBindViewHolder
 */
fun <V : ViewBinding> IBindingAdapter<V>.doBeforeBindViewHolder(listener: (holder: BindingViewHolder<V>, position: Int, payloads: List<Any>) -> Unit): IBindingAdapter<V> {
    val onBindViewHolderDelegateOrigin = onBindViewHolderDelegate
    onBindViewHolderDelegate = { holder, position, p ->
        listener(holder, position, p)
        onBindViewHolderDelegateOrigin(holder, position, p)
    }
    return this
}

/**
 * 拦截onCreateViewHolder
 */
fun <V : ViewBinding, A : IBindingAdapter<V>> A.interceptCreateViewHolder(listener: (parent: ViewGroup, viewType: Int, origin: (parent: ViewGroup, viewType: Int) -> BindingViewHolder<V>) -> BindingViewHolder<V>): IBindingAdapter<V> {
    val onCreateViewHolderDelegateOrigin = onCreateViewHolderDelegate
    onCreateViewHolderDelegate = { parent, viewType ->
        listener(parent, viewType, onCreateViewHolderDelegateOrigin)
    }
    return this
}

/**
 * 监听onCreateViewHolder
 */
fun <V : ViewBinding, A : IBindingAdapter<V>> A.doAfterCreateViewHolder(listener: (holder: BindingViewHolder<V>, parent: ViewGroup, viewType: Int) -> Unit): A {
    val onCreateViewHolderDelegateOrigin = onCreateViewHolderDelegate
    onCreateViewHolderDelegate = { parent, viewType ->
        onCreateViewHolderDelegateOrigin.invoke(parent, viewType).also {
            listener(it, parent, viewType)
        }

    }
    return this
}


@SuppressLint("NotifyDataSetChanged")
fun <I : Any, V : ViewBinding> MultiTypeBindingAdapter<I, V>.replaceData(newData: Collection<I>?) {
    data.clear()
    if (!newData.isNullOrEmpty()) {
        data.addAll(newData)
    }
    notifyDataSetChanged()
}

fun <I : Any, V : ViewBinding> MultiTypeBindingAdapter<I, V>.appendData(appendData: Collection<I>?) {
    if (appendData.isNullOrEmpty()) {
        return
    }
    val originSize = data.size
    data.addAll(appendData)
    notifyItemRangeInserted(originSize, appendData.size)
}

/**
 * 使用配置创建多布局Adapter
 */
fun <I : Any, V : ViewBinding> ItemViewMapperStore<I, V>.asAdapter(list: List<I> = ArrayList()) =
    MultiTypeBindingAdapter(this, list)

/**
 * 拷贝Adapter
 * @param newData 新数据
 */
fun <I : Any, V : ViewBinding> MultiTypeBindingAdapter<I, V>.copy(newData: List<I> = data): MultiTypeBindingAdapter<I, V> {
    return MultiTypeBindingAdapter(
        itemViewMapperStore,
        if (newData === data) ArrayList(data) else newData
    )
}

@OptIn(ExperimentalTypeInference::class)
@BuilderInference
@Suppress("NOTHING_TO_INLINE")
inline fun <I : Any> buildMultiTypeAdapterByIndex(noinline build: MultiTypeAdapterIndexConfigBuilder<I>.() -> Unit): MultiTypeBindingAdapter<I, ViewBinding> =
    createMultiTypeConfigByIndex(build).asAdapter()

@OptIn(ExperimentalTypeInference::class)
@BuilderInference
@Suppress("NOTHING_TO_INLINE")
inline fun <I : Any> buildMultiTypeAdapterByType(noinline build: MultiTypeAdapterTypeConfigBuilder<I>.() -> Unit): MultiTypeBindingAdapter<I, ViewBinding> =
    createMultiTypeConfigByType(build).asAdapter()

@OptIn(ExperimentalTypeInference::class)
@BuilderInference
@Suppress("NOTHING_TO_INLINE")
inline fun <I : Any> buildMultiTypeAdapterByMap(noinline build: MultiTypeAdapterMapConfigBuilder<I>.() -> Unit): MultiTypeBindingAdapter<I, ViewBinding> =
    createMultiTypeConfigByMap(build).asAdapter()