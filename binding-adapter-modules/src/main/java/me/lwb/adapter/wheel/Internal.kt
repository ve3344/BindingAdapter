package me.lwb.adapter.wheel

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import me.lwb.adapter.BindingAdapter
import me.lwb.adapter.ext.doBeforeBindViewHolder

/**
 * Created by ve3344@qq.com.
 */
internal fun View.measureSize(): Rect {
    val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    measure(w, h)

    return Rect(0, 0, measuredWidth, measuredHeight)

}

internal fun RecyclerWheelViewModule.bindStateToAdapter(
    adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?,
) {
    if (adapter is BindingAdapter<*, *>) {
        adapter.doBeforeBindViewHolder { holder, position ->
            holder.isWheelItemSelected = this.selectedPosition == position
        }
    }

}

internal typealias AdapterFactory = (Int) -> BindingAdapter<CharSequence, *>