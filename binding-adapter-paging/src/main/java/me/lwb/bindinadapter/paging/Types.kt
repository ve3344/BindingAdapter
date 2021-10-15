package me.lwb.bindinadapter.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import me.lwb.bindingadapter.BindingViewHolder

/**
 * Created by ve3344@qq.com on 2021/10/12.
 */
internal typealias LayoutCreator<V> = (inflater: LayoutInflater, root: ViewGroup, attachToRoot: Boolean) -> V

internal typealias LayoutConverter<I, V> = BindingViewHolder<V>.(position: Int, item: I) -> Unit

internal typealias LayoutConverter1<V> = BindingViewHolder<V>.() -> Unit

internal typealias LayoutTypeExtractor<I> = (position: Int, item: I) -> Int

internal typealias LayoutConverterLoadState<V> = BindingViewHolder<V>.(item: LoadState) -> Unit

