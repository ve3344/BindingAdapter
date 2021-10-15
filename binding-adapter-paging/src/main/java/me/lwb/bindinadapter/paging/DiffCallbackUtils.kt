package me.lwb.bindinadapter.paging

import androidx.recyclerview.widget.DiffUtil

/**
 * 通用 DiffCallback
 */
object DiffCallbackUtils {
    /**
     * 默认DiffCallback
     */
    fun <T> create() = object : DiffUtil.ItemCallback<T>() {

        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
            areItemsTheSame(oldItem, newItem)

    }
    /**
     * 映射id的DiffCallback
     */
     fun <T> create(keyExtractor:(T)->Any?) = object : DiffUtil.ItemCallback<T>() {

        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
            keyExtractor(oldItem)== keyExtractor(newItem)

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
            areItemsTheSame(oldItem, newItem)

    }
}