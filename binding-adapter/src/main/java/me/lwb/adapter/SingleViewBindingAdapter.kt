package me.lwb.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 * Created by ve3344@qq.com.
 */
/**
 * 单个view的adapter，一般作为header 或者footer
 */
open class SingleViewBindingAdapter<V : ViewBinding>(
    creator: LayoutCreator<V>,
    private var converter: LayoutConverter0<V> = {}
) : BindingAdapter<Unit, V>(creator, SINGLE_ITEM_LIST, { _, _, _ -> converter() }) {
    var bindingViewHolder: BindingViewHolder<V>? = null
        private set
    @SuppressLint("NotifyDataSetChanged")
    fun update(newConverter: LayoutConverter0<V> = converter) {
        converter = newConverter
        val viewHolder = bindingViewHolder
        if (viewHolder == null) {
            notifyDataSetChanged()
        }else{
            viewHolder.converter()
        }
    }

    @Deprecated("Use update", ReplaceWith("update()"))
    fun notifyUpdateView() = update()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<V> {
        return super.onCreateViewHolder(parent, viewType).also {
            bindingViewHolder = it
        }
    }

    companion object {
        private val SINGLE_ITEM_LIST = listOf(Unit)
    }
}