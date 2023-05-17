package me.lwb.adapter.basic

import androidx.recyclerview.widget.RecyclerView

class ObservableListWrapper<T>(val data: MutableList<T>,var adapter: RecyclerView.Adapter<*>? = null) :
    MutableList<T> by data {

    override fun add(element: T): Boolean {
        data.add(element)
        notifyAdd(size - 1, 1)
        return true
    }

    override fun add(index: Int, element: T) {
        data.add(index, element)
        notifyAdd(index, 1)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val oldSize = size
        val added = data.addAll(elements)
        if (added) {
            notifyAdd(oldSize, size - oldSize)
        }
        return added
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val added = data.addAll(index, elements)
        if (added) {
            notifyAdd(index, elements.size)
        }
        return added
    }

    override fun clear() {
        val oldSize = size
        data.clear()
        if (oldSize != 0) {
            notifyRemove(0, oldSize)
        }
    }

    override fun removeAt(index: Int): T {
        val `val` = data.removeAt(index)
        notifyRemove(index, 1)
        return `val`
    }

    override fun remove(element: T): Boolean {
        val index = data.indexOf(element)
        return if (index >= 0) {
            removeAt(index)
            true
        } else {
            false
        }
    }

    override fun set(index: Int, element: T): T {
        val old = data.set(index, element)
        adapter?.notifyItemRangeChanged(index, 1)
        return old
    }

    private fun notifyAdd(start: Int, count: Int) {
        adapter?.notifyItemRangeInserted(start, count)
        adapter?.notifyItemRangeChanged(start+count,data.size)

    }

    private fun notifyRemove(start: Int, count: Int) {
        adapter?.notifyItemRangeRemoved(start, count)
        adapter?.notifyItemRangeChanged(start+count-1,data.size)

    }

    override fun toString(): String {
        return data.toString()
    }
}
