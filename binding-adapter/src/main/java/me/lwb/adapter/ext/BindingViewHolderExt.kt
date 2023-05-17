package me.lwb.adapter.ext

import androidx.annotation.IdRes
import me.lwb.adapter.BindingViewHolder
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by ve3344@qq.com.
 */
fun <V> viewHolderTag(@IdRes id: Int, defaultValue: V) =
    object : ReadWriteProperty<BindingViewHolder<*>, V> {
        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: BindingViewHolder<*>, property: KProperty<*>): V =
            thisRef.getTag(id) as? V ?: defaultValue

        override fun setValue(thisRef: BindingViewHolder<*>, property: KProperty<*>, value: V) =
            thisRef.setTag(id, value)
    }