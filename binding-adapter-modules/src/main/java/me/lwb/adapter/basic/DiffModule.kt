package me.lwb.adapter.basic

import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.AsyncListDiffer.ListListener
import androidx.recyclerview.widget.DiffUtil
import me.lwb.adapter.MultiTypeBindingAdapter

/**
 * Created by ve3344@qq.com.
 */
open class DiffModule<T : Any>(
    val adapter: MultiTypeBindingAdapter<T, *>,
    config: AsyncDifferConfig<T>
) {
    private val mDiffer: AsyncListDiffer<T> =
        AsyncListDiffer(AdapterListUpdateCallback(adapter), config)

    init {
        adapter.changeDataList(LazyListWrapper { currentList })
    }

    fun addListListener(listListener: ListListener<T>) = mDiffer.addListListener(listListener)

    @JvmOverloads
    fun submitList(list: List<T>?, commitCallback: Runnable? = null) {
        mDiffer.submitList(list, commitCallback)
    }

    val currentList get() = mDiffer.currentList


}


fun <T : Any> MultiTypeBindingAdapter<T, *>.setupDiffModule(config: AsyncDifferConfig<T>) =
    DiffModule(this, config)

fun <T : Any> MultiTypeBindingAdapter<T, *>.setupDiffModule(diffCallback: DiffUtil.ItemCallback<T>) =
    DiffModule(this, AsyncDifferConfig.Builder<T>(diffCallback).build())