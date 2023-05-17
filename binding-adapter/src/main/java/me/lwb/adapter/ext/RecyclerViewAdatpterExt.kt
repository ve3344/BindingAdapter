package me.lwb.adapter.ext

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by ve3344@qq.com.
 */

/**
 * 连接2个 adapter
 */
operator fun RecyclerView.Adapter<*>.plus(o: RecyclerView.Adapter<*>): ConcatAdapter {
    if (this is ConcatAdapter) {
        this.addAdapter(o)
        return this
    }
    if (o is ConcatAdapter) {
        o.addAdapter(0, this)
        return o
    }
    return ConcatAdapter(this, o)
}

/**
 * 获取该position 所在的Adapter
 * @param position 位置
 * @return Adapter 所在的Adapter ，null 没有对应的Adapter
 */
fun ConcatAdapter.getAdapterByItemPosition(position: Int): RecyclerView.Adapter<out RecyclerView.ViewHolder>? {
    var pos = position
    val adapters = adapters
    for (adapter in adapters) {
        when {
            pos >= adapter.itemCount -> {
                pos -= adapter.itemCount
            }
            pos < 0 -> return null
            else -> return adapter
        }
    }
    return null
}

/**
 * 把全局位置转换为子Adapter的相对位置
 * @param adapter
 * @param globalPosition
 */
fun ConcatAdapter.findLocalPositionAt(
    adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
    globalPosition: Int
): Int {
    val itemsBefore = countItemsBefore(adapter)
    if (itemsBefore == RecyclerView.NO_POSITION) {
        return RecyclerView.NO_POSITION
    }
    val localPosition: Int = globalPosition - itemsBefore
    val itemCount: Int = adapter.itemCount

    if (localPosition < 0 || localPosition >= itemCount) {
        return RecyclerView.NO_POSITION
    }

    return localPosition
}

fun RecyclerView.Adapter<*>.countItemsBefore(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>): Int {
    if (this == adapter) {
        return 0
    }
    check(this is ConcatAdapter) { "adapter $adapter is not in $this" }
    var count = 0
    val adapters = adapters

    for (item in adapters) {
        if (item !== adapter) {
            count += item.itemCount
        } else {
            return count
        }
    }
    return RecyclerView.NO_POSITION
}

/**
 * 递归遍历所有Adapter包括this
 *
 */
fun RecyclerView.Adapter<*>.allAdapter(): Sequence<RecyclerView.Adapter<*>> {
    if (this !is ConcatAdapter) {
        return sequenceOf(this)
    }
    return adapters.asSequence().flatMap { it.allAdapter() }

}