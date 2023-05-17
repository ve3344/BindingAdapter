package me.lwb.adapter.select

import me.lwb.adapter.MultiTypeBindingAdapter
import me.lwb.adapter.ext.doBeforeBindViewHolder

/**
 * Created by ve3344@qq.com.
 */
class MultiSelectModule<I : Any> internal constructor(
    override val adapter: MultiTypeBindingAdapter<I, *>,
) : AbstractMultiSelectModule<I, Int>() {
    private val mutableSelectedIndexes: MutableSet<Int> = HashSet()

    override var selectKeys: Collection<Int> by ::selectedIndexes


    var selectedIndexes: Collection<Int>
        get() = mutableSelectedIndexes
        set(value) {
            setSelects(value)
        }

    override val selectedItems: Collection<I>
        get() = mutableSelectedIndexes.map { adapter.data[it] }


    init {
        adapter.doBeforeBindViewHolder { holder, position ->
            val selected = isSelected(position)
            holder.isItemSelected = selected
            holder.itemView.setOnClickListener {
                if (onUserSelectListener?.invoke(position, selected) == true) {
                    return@setOnClickListener
                }
                toggleSelect(position)
            }
        }
    }

    /**
     * 清除选择
     */
    override fun clearSelected() {
        mutableSelectedIndexes.clear()
        notifyItemsChanged()
    }


    /**
     * 设置选择的地址
     */
    private fun setSelects(selectKeys: Iterable<Int>) {
        mutableSelectedIndexes.clear()
        mutableSelectedIndexes.addAll(selectKeys)
        notifyItemsChanged()
    }

    /**
     * 反选
     */
    override fun invertSelected() {
        val selectStates = BooleanArray(adapter.itemCount) { false }
        mutableSelectedIndexes.forEach {
            selectStates[it] = true
        }
        mutableSelectedIndexes.clear()
        selectStates.forEachIndexed { index, select ->
            if (!select) {
                mutableSelectedIndexes.add(index)
            }
        }
        notifyItemsChanged()
    }

    override val selectedAll
        get() = mutableSelectedIndexes.size == adapter.itemCount


    override fun selectItem(selectKey: Int, choose: Boolean) {
        if (choose) {
            mutableSelectedIndexes.add(selectKey)
        } else {
            mutableSelectedIndexes.remove(selectKey)
        }
        notifyItemsChanged()
    }


    override fun selectAll() {
        mutableSelectedIndexes.clear()
        //添加所有索引
        for (i in 0 until adapter.itemCount) {
            mutableSelectedIndexes.add(i)
        }
        notifyItemsChanged()
    }


    override fun isSelected(selectKey: Int): Boolean {
        return selectedIndexes.contains(selectKey)
    }


}


fun <I : Any> MultiTypeBindingAdapter<I, *>.setupMultiSelectModule(): MultiSelectModule<I> {
    return MultiSelectModule(adapter = this)
}

