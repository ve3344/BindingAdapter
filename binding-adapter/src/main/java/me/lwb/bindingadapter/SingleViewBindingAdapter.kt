package me.lwb.bindingadapter

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 * Created by ve3344@qq.com on 2021/9/23.
 */
/**
 * 单个view的adapter，一般作为header 或者footer
 */
open class SingleViewBindingAdapter<V : ViewBinding>(
    creator: LayoutCreator<V>,
    converter: LayoutConverter1<V> = {}

) : BindingAdapter<Unit, V>(creator, SINGLE_ITEM_LIST, {  _, _ -> converter() }) {
    var bindingContent: V? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<V> {
        return super.onCreateViewHolder(parent, viewType).also { bindingContent = it.binding }
    }

    companion object {
        private val SINGLE_ITEM_LIST = listOf(Unit)
    }
}