package me.lwb.adapter.select

import android.annotation.SuppressLint
import me.lwb.adapter.MultiTypeBindingAdapter
import me.lwb.adapter.ext.postAvoidComputingLayout

/**
 * Created by ve3344@qq.com.
 */
abstract class AbstractMultiSelectModule<I : Any, K> {
    abstract val adapter: MultiTypeBindingAdapter<I, *>


    private var onSelectChange: (selectKeys: Collection<K>) -> Unit = {}

    protected var onUserSelectListener: ((selectKey: K, currentSelected: Boolean) -> Boolean)? =
        null

    /**
     * 监听用户点击选择
     * @param onUserSelect 监听器，返回true 表示处理了此事件
     */
    fun doOnUserSelect(onUserSelect: (selectKey: K, currentSelected: Boolean) -> Boolean) {
        onUserSelectListener = onUserSelect
    }

    /**
     * 监听变化
     * @param onSelectChange 监听器
     */
    fun doOnSelectChange(
        onSelectChange: (selectKeys: Collection<K>) -> Unit,
    ) {
        this.onSelectChange = onSelectChange
    }

    /**
     * 观察变化（回调当前值）
     * @param onSelectChange 监听器
     */
    fun observeSelectChange(
        onSelectChange: (selectKeys: Collection<K>) -> Unit,
    ) {
        this.onSelectChange = onSelectChange
        onSelectChange(selectKeys)
    }


    @SuppressLint("NotifyDataSetChanged")
    protected fun notifyItemsChanged() {
        adapter.recyclerView?.postAvoidComputingLayout {
            adapter.notifyDataSetChanged()
        }
        onSelectChange(selectKeys)
    }


    abstract fun isSelected(selectKey: K): Boolean

    abstract var selectKeys: Collection<K>

    abstract val selectedItems: Collection<I>

    abstract fun clearSelected()

    open fun toggleSelect(selectKey: K) {
        selectItem(selectKey, !isSelected(selectKey))
    }


    /**
     * 反选
     */
    abstract fun invertSelected()

    abstract val selectedAll: Boolean

    abstract fun selectAll()

    abstract fun selectItem(selectKey: K, choose: Boolean)
}