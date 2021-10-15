package me.lwb.bindingadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Created by ve3344@qq.com on 2021/9/23.
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