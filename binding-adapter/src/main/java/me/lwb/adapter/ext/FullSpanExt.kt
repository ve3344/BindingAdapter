package me.lwb.adapter.ext

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import me.lwb.adapter.IBindingAdapter

/**
 * Created by ve3344@qq.com.
 */

/**
 * 配置某些位置独占一行
 * @param range 判断是否只独占一行
 */
@Deprecated("Use configFullSpan()", ReplaceWith("configFullSpan(checkFullSpan)"))
fun GridLayoutManager.configSingleViewSpan(checkFullSpan: (position: Int) -> Boolean): GridLayoutManager {
    return configFullSpan(checkFullSpan)
}

/**
 * 配置某些位置独占一行
 * @param checkFullSpan 判断是否只独占一行
 */
fun <G : GridLayoutManager> G.configFullSpan(checkFullSpan: (position: Int) -> Boolean): G {
    val oldSpanSizeLookup = spanSizeLookup
    val fullSpanCount = spanCount
    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (checkFullSpan(position)) fullSpanCount else oldSpanSizeLookup.getSpanSize(
                position
            )
        }
    }
    return this
}


fun <V : ViewBinding, A : IBindingAdapter<V>> A.setFullSpan() = apply {
    doAfterCreateViewHolder { holder, _, _ ->
        val lp = holder.itemView.layoutParams
        if (lp is StaggeredGridLayoutManager.LayoutParams) {
            lp.isFullSpan = true
        }
    }
}

fun <V : ViewBinding, A : IBindingAdapter<V>> A.setFullSpan(checkFullSpan: (position: Int) -> Boolean) =
    apply {
        doAfterBindViewHolder { holder, position, _ ->
            val lp = holder.itemView.layoutParams
            if (lp is StaggeredGridLayoutManager.LayoutParams) {
                val fullSpan = checkFullSpan(position)
                if (fullSpan != lp.isFullSpan) {
                    lp.isFullSpan = fullSpan
                    holder.itemView.layoutParams = lp
                }
            }
        }
    }