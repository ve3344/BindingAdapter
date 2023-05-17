package me.lwb.adapter.select

import me.lwb.adapter.MultiTypeBindingAdapter
import me.lwb.adapter.ext.doBeforeBindViewHolder

/**
 * 单选的帮助类
 * Created by ve3344@qq.com.
 * @param adapter adapter
 * @param selector 元素唯一标识
 */
class SingleSelectModuleByKey<I : Any> internal constructor(
    override val adapter: MultiTypeBindingAdapter<I, *>,
    val selector: I.() -> Any,
) : AbstractSingleSelectModule<I, I?>() {
    companion object {
        val ITEM_UNSELECTED = null
    }
    init {
        adapter.doBeforeBindViewHolder { holder, position ->
            val selectedKey = adapter.data.getOrNull(position)
            val selected = isSelected(selectedKey)
            holder.isItemSelected = selected
            holder.itemView.setOnClickListener {
                val selectedKeyNoNull = selectedKey?:return@setOnClickListener
                if (onUserSelectListener?.invoke(selectedKeyNoNull,selected)==true) {
                    return@setOnClickListener
                }
                toggleSelect(selectedKeyNoNull)
            }
        }
    }

    override var selectedKey: I? by ::selectedItem

    override var selectedItem: I? = ITEM_UNSELECTED
        set(value) {
            var targetItem = value
            if (targetItem !in adapter.data) {
                targetItem = ITEM_UNSELECTED
            }

            if (field == targetItem) {
                return
            }
            field = targetItem

            notifyItemsChanged()
        }

    override fun isSelected(selectedKey: I?): Boolean {
        val select = selectedItem
        return selectedKey != ITEM_UNSELECTED && select != ITEM_UNSELECTED && selectedKey.selector() == select.selector()
    }
    override fun clearSelected() {
        selectedItem = ITEM_UNSELECTED
    }
    override fun toggleSelect(selectedKey: I?) {
        selectedItem = if (enableUnselect) {
            if (isSelected(selectedKey)) {
                ITEM_UNSELECTED
            } else {
                selectedKey
            }
        } else {
            selectedKey
        }
    }
}

/**
 * 单选
 */
fun <I : Any> MultiTypeBindingAdapter<I, *>.setupSingleSelectModuleByKey(selector: (I) -> Any): SingleSelectModuleByKey<I> {
    return SingleSelectModuleByKey(adapter = this, selector = selector)
}
