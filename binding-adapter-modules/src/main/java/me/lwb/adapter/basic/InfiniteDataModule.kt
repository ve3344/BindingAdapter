package me.lwb.adapter.basic

import androidx.viewbinding.ViewBinding
import me.lwb.adapter.MultiTypeBindingAdapter

/**
 * Created by ve3344@qq.com.
 */
/**
 * 设置无限数据
 */
class InfiniteDataModule<T : Any, V : ViewBinding>(val adapter: MultiTypeBindingAdapter<T, V>) {

    init {

        attach()
    }

    private fun attach() {
        if (adapter.data is InfiniteListWrapper) {
            return
        }
        val inflateList = adapter.data.asInflateList()
        adapter.changeDataList(inflateList)
        val onBindViewHolderDelegateOrigin = adapter.onBindViewHolderDelegate
        adapter.onBindViewHolderDelegate = { holder, position, p ->
            onBindViewHolderDelegateOrigin(holder, inflateList.getDataPosition(position), p)
        }
    }
}

fun <T> MutableList<T>.asInflateList() = InfiniteListWrapper(this)

fun <T : Any, V : ViewBinding, A : MultiTypeBindingAdapter<T, V>> A.setupInfiniteDataModule() =
    InfiniteDataModule(this)