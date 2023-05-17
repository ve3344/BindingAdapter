package me.lwb.adapter.select


import me.lwb.adapter.MultiTypeBindingAdapter
import me.lwb.adapter.ext.doBeforeBindViewHolder

/**
 * Created by ve3344@qq.com.
 * @param selector 元素唯一标识
 */
class MultiSelectModuleByKey<I : Any> internal constructor(
    override val adapter: MultiTypeBindingAdapter<I, *>,
    val selector: I.() -> Any,
) : AbstractMultiSelectModule<I, I>() {
    override var selectKeys: Collection<I> by ::selectedItems

    private val mutableSelectedItems: MutableMap<Any, I> = HashMap()
    override var selectedItems: Collection<I>
        get() = mutableSelectedItems.values
        set(value) {
            setSelects(value)
        }


    init {
        adapter.doBeforeBindViewHolder { holder, position ->
            val selected =
                isSelected(adapter.data.getOrNull(position) ?: return@doBeforeBindViewHolder)
            holder.isItemSelected = selected
            holder.itemView.setOnClickListener {
                val selectKey = adapter.data.getOrNull(position) ?: return@setOnClickListener
                if (onUserSelectListener?.invoke(selectKey, selected) == true) {
                    return@setOnClickListener
                }
                toggleSelect(selectKey)
            }
        }
    }

    /**
     * 清除选择
     */
    override fun clearSelected() {
        mutableSelectedItems.clear()
        notifyItemsChanged()
    }

    /**
     * 设置选择的地址
     */
    private fun setSelects(selects: Iterable<I>) {
        mutableSelectedItems.clear()
        selects.forEach {
            mutableSelectedItems[it.selector()] = it
        }
        notifyItemsChanged()
    }

    /**
     * 反选
     */
    override fun invertSelected() {
        val other = adapter.data
            .asSequence()
            .filter { it !in mutableSelectedItems }
            .map { it.selector() to it }
            .toList()

        mutableSelectedItems.clear()
        mutableSelectedItems.putAll(other)

        notifyItemsChanged()
    }

    override val selectedAll
        get() = mutableSelectedItems.size == adapter.itemCount


    override fun selectItem(selectKey: I, choose: Boolean) {
        val id = selectKey.selector()
        if (choose) {
            mutableSelectedItems[id] = selectKey
        } else {
            mutableSelectedItems.remove(id)
        }
        notifyItemsChanged()
    }


    override fun selectAll() {
        mutableSelectedItems.clear()
        mutableSelectedItems.putAll(adapter.data.map { it.selector() to it })
        notifyItemsChanged()
    }


    override fun isSelected(selectKey: I): Boolean {
        return mutableSelectedItems.containsKey(selectKey.selector())
    }


}


/**
 * @param identifier 标识符，一般是item 的 id，需要实现hashcode和equals方法
 */
fun <I : Any> MultiTypeBindingAdapter<I, *>.setupMultiSelectModuleByKey(identifier: (I) -> Any = { it }): MultiSelectModuleByKey<I> {
    return MultiSelectModuleByKey(adapter = this, selector = identifier)
}

