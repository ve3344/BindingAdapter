package me.lwb.adapter.paging.ext

import androidx.recyclerview.widget.DiffUtil

/**
 * 通用 DiffCallback
 */
object DiffCallbackUtils {
    /**
     * 默认DiffCallback
     */
    fun <T : Any> itemCallback() = object : DiffUtil.ItemCallback<T>() {

        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
            areItemsTheSame(oldItem, newItem)

    }
    /**
     * 映射id的DiffCallback
     */
    inline fun <T : Any> itemCallback(crossinline checkEquals:(oldItem: T, newItem: T)->Boolean, crossinline uniqueId:(T)->Any?) = object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
            uniqueId(oldItem)== uniqueId(newItem)

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
            areItemsTheSame(oldItem,newItem)&& checkEquals(oldItem, newItem)
    }


}