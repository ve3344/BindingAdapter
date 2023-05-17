package me.lwb.adapter.select

import android.annotation.SuppressLint
import me.lwb.adapter.MultiTypeBindingAdapter
import me.lwb.adapter.ext.postAvoidComputingLayout

/**
 * Created by ve3344@qq.com.
 */
abstract class AbstractSingleSelectModule<I : Any, K> {
    abstract val adapter: MultiTypeBindingAdapter<I, *>

    /**
     * 是否允许再次点击后取消选择
     */
    var enableUnselect: Boolean = true

    private var onSelectChange: (selectedKey: K) -> Unit = {}

    protected var onUserSelectListener: ((selectedKey: K, currentSelected: Boolean) -> Boolean)? =
        null

    /**
     * 监听用户点击选择
     * @param onUserSelect 监听器，返回true 表示处理了此事件
     */
    fun doOnUserSelect(onUserSelect: (selectedKey: K, currentSelected: Boolean) -> Boolean) {
        onUserSelectListener = onUserSelect
    }

    /**
     * 监听变化
     * @param onSelectChange 监听器
     */
    fun doOnSelectChange(
        onSelectChange: (selectedKey: K) -> Unit,
    ) {
        this.onSelectChange = onSelectChange
    }

    /**
     * 观察变化（回调当前值）
     * @param onSelectChange 监听器
     */
    fun observeSelectChange(
        onSelectChange: (selectedKey: K) -> Unit,
    ) {
        this.onSelectChange = onSelectChange
        onSelectChange(selectedKey)
    }


    @SuppressLint("NotifyDataSetChanged")
    protected fun notifyItemsChanged() {
        adapter.recyclerView?.postAvoidComputingLayout {
            adapter.notifyDataSetChanged()
        }
        onSelectChange(selectedKey)
    }


    abstract fun isSelected(selectedKey: K): Boolean

    abstract var selectedKey: K
    
    abstract val selectedItem: I?

    abstract fun clearSelected()

    abstract fun toggleSelect(selectedKey: K)
}