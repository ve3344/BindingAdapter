package me.lwb.adapter_demo.base.ui

import android.annotation.SuppressLint
import androidx.databinding.ObservableList
import androidx.databinding.ObservableList.OnListChangedCallback
import androidx.recyclerview.widget.RecyclerView

class OnListChangedNotifier<T >(
    val adapter: RecyclerView.Adapter<*>,
) : OnListChangedCallback<ObservableList<T>>() {

    @SuppressLint("NotifyDataSetChanged")
    override fun onChanged(sender: ObservableList<T>) {
        adapter.notifyDataSetChanged()
    }

    override fun onItemRangeChanged(
        sender: ObservableList<T>,
        positionStart: Int,
        itemCount: Int
    ) {
        adapter.notifyItemRangeChanged(positionStart, itemCount)
    }

    override fun onItemRangeInserted(
        sender: ObservableList<T>,
        positionStart: Int,
        itemCount: Int
    ) {
        adapter.notifyItemRangeInserted(positionStart, itemCount)
    }

    override fun onItemRangeMoved(
        sender: ObservableList<T>,
        fromPosition: Int,
        toPosition: Int,
        itemCount: Int
    ) {
        for (i in 0 until itemCount) {
            adapter.notifyItemMoved(fromPosition + i, toPosition + i)
        }
    }

    override fun onItemRangeRemoved(
        sender: ObservableList<T>,
        positionStart: Int,
        itemCount: Int
    ) {
        adapter.notifyItemRangeRemoved(positionStart, itemCount)
    }


}