package me.lwb.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RestrictTo
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Created by ve3344@qq.com.
 */

internal typealias LayoutCreator<V> = (inflater: LayoutInflater, root: ViewGroup, attachToRoot: Boolean) -> V

internal typealias LayoutConverter0<V> = BindingViewHolder<V>.() -> Unit
internal typealias LayoutConverter2<I, V> = BindingViewHolder<V>.(position: Int, item: I) -> Unit
internal typealias LayoutConverter<I, V> = BindingViewHolder<V>.(position: Int, item: I, payloads: List<Any>) -> Unit

internal typealias LayoutTypeExtractor<I> = (position: Int, item: I) -> Int

@Suppress("NOTHING_TO_INLINE")
internal inline fun <I, V : ViewBinding> LayoutConverter2<I, V>.asConverter(): LayoutConverter<I, V> =
    { position: Int, item: I, _: List<Any> ->
        val out = this@asConverter
        val holder = this
        out.apply {
            holder.out(position, item)
        }
    }


/**
 * 变更visible时 刷新 adapter
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal fun RecyclerView.Adapter<*>.updateOnVisibleChange(
    oldVisible: Boolean,
    newVisible: Boolean,
    count: Int
) {
    if (oldVisible == newVisible) return
    when {
        oldVisible && !newVisible -> notifyItemRangeRemoved(0, count)
        newVisible && !oldVisible -> notifyItemRangeInserted(0, count)
        oldVisible && newVisible -> notifyItemRangeChanged(0, count)
    }

}


@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS,)
annotation class ExperimentalAdapterModule
