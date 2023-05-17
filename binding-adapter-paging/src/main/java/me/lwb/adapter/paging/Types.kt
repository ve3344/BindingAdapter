package me.lwb.adapter.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.viewbinding.ViewBinding
import me.lwb.adapter.BindingViewHolder

/**
 * Created by ve3344@qq.com.
 */
internal typealias LayoutCreator<V> = (inflater: LayoutInflater, root: ViewGroup, attachToRoot: Boolean) -> V

internal typealias LayoutConverter0<V> = BindingViewHolder<V>.() -> Unit
internal typealias LayoutConverter2<I, V> = BindingViewHolder<V>.(position: Int, item: I) -> Unit
internal typealias LayoutConverter<I, V> = BindingViewHolder<V>.(position: Int, item: I, payloads: List<Any>) -> Unit


internal typealias LayoutTypeExtractor<I> = (position: Int, item: I) -> Int

internal typealias LayoutConverterLoadState<V> = BindingViewHolder<V>.(item: LoadState) -> Unit

@Suppress("NOTHING_TO_INLINE")
internal inline fun <I, V : ViewBinding> LayoutConverter2<I, V>.asConverter(): LayoutConverter<I, V> =
    { position: Int, item: I, _: List<Any> ->
        val out = this@asConverter
        val holder = this
        out.apply {
            holder.out(position, item)
        }
    }
