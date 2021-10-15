package me.lwb.bindingadapter

import androidx.annotation.RestrictTo
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by ve3344@qq.com on 2021/10/12.
 */
/**
 * 变更visible时 刷新 adapter
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object AdapterVisibleUtils {
    fun RecyclerView.Adapter<*>.updateOnVisibleChange(
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
}
