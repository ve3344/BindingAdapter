package me.lwb.adapter.select

import me.lwb.adapter.MultiTypeBindingAdapter
import me.lwb.adapter.ext.doBeforeBindViewHolder

/**
 * 单选的帮助类
 * Created by ve3344@qq.com.
 * @param initSelectIndex 初始选中位置
 * @param adapter adapter
 */
class SingleSelectModule<I : Any> internal constructor(
    override val adapter: MultiTypeBindingAdapter<I, *>,
) : AbstractSingleSelectModule<I, Int>() {
    companion object {
        const val INDEX_UNSELECTED = -1
    }

    override var selectedKey: Int by ::selectIndex

    init {
        adapter.doBeforeBindViewHolder { holder, position ->
            val selected = isSelected(position)
            holder.isItemSelected = selected
            holder.itemView.setOnClickListener {
                if (onUserSelectListener?.invoke(position,selected)==true) {
                    return@setOnClickListener
                }
                toggleSelect(position)
            }
        }
    }


    var selectIndex: Int = INDEX_UNSELECTED
        set(value) {
            var targetIndex = value
            if (targetIndex !in 0 until adapter.itemCount) {
                targetIndex = INDEX_UNSELECTED
            }
            if (field == targetIndex) {
                return
            }
            field = targetIndex

            notifyItemsChanged()
        }
    override val selectedItem: I?
        get() = adapter.data.getOrNull(selectIndex)

    override fun clearSelected() {
        selectIndex = INDEX_UNSELECTED
    }
    override fun isSelected(selectedKey: Int): Boolean {
        return selectedKey != INDEX_UNSELECTED && this.selectedKey != INDEX_UNSELECTED && selectedKey == this.selectedKey
    }
    override fun toggleSelect(selectedKey: Int) {
        selectIndex = if (enableUnselect) {
            if (isSelected(selectedKey)) {
                INDEX_UNSELECTED
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
fun <I : Any> MultiTypeBindingAdapter<I, *>.setupSingleSelectModule(): SingleSelectModule<I> {
    return SingleSelectModule(adapter = this)
}
