package me.lwb.adapter.stick

import android.graphics.Rect
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import me.lwb.adapter.ext.countItemsBefore

/**
 * Created by ve3344@qq.com.
 */

internal fun Rect.offset(insets: Rect) {
    left += insets.left
    top += insets.top
    right += insets.right
    bottom += insets.bottom
}

internal fun Rect.inset(insets: Rect) {
    left += insets.left
    top += insets.top
    right -= insets.right
    bottom -= insets.bottom
}

internal fun Rect.isOverlappingVertical(other: Rect): Boolean {
    if (this.top > other.bottom || this.bottom < other.top) {
        return false
    }

    return true
}
internal fun Rect.isOverlappingHorizontal(other: Rect): Boolean {
    if (this.left > other.right || this.right < other.left) {
        return false
    }

    return true
}

@Suppress("UNCHECKED_CAST")
internal fun <VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.bindViewHolderCast(
    viewHolder: RecyclerView.ViewHolder,
    position: Int
) {
    bindViewHolder(viewHolder as VH, position)
}

/**
 * 兼容adapter==this 的情况
 */
internal fun RecyclerView.Adapter<*>.findRelativeAdapterPositionInCompatSelf(
    adapter: RecyclerView.Adapter<*>,
    viewHolder: RecyclerView.ViewHolder,
    viewPosition: Int
): Int {
    if (adapter == this) {
        return viewPosition
    }
    return findRelativeAdapterPositionIn(adapter, viewHolder, viewPosition)
}

internal fun RecyclerView.Adapter<*>.findGlobalAdapterPositionInCompatSelf(
    adapter: RecyclerView.Adapter<*>,
    viewPosition: Int
): Int {
    if (adapter == this) {
        return viewPosition
    }
    val itemsBefore = adapter.countItemsBefore(adapter)
    return itemsBefore + viewPosition
}


internal fun View.getBounds(): Rect {
    return Rect(left, top, right, bottom)
}
internal inline fun View.doOnLayout(crossinline action: (view: View) -> Unit) {
    if (ViewCompat.isLaidOut(this) && !isLayoutRequested) {
        action(this)
    } else {
        doOnNextLayout {
            action(it)
        }
    }
}
internal inline fun View.doOnNextLayout(crossinline action: (view: View) -> Unit) {
    addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(
            view: View,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int
        ) {
            view.removeOnLayoutChangeListener(this)
            action(view)
        }
    })
}

internal fun RecyclerView.LayoutManager.orientation() = when (this) {
    is GridLayoutManager -> this.orientation
    is LinearLayoutManager -> this.orientation
    is StaggeredGridLayoutManager -> this.orientation
    else -> RecyclerView.VERTICAL
}
