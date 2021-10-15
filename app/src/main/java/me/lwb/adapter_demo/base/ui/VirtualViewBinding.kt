package me.lwb.adapter_demo.base.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 * Created by luowenbin on 2021/10/13.
 */
open class VirtualViewBinding<V : View>(val contentView: V) : ViewBinding {
    override fun getRoot(): View = contentView
}
//    val contentView: V by lazy(LazyThreadSafetyMode.NONE) { creator() }
class VirtualViewBindingInflater<V : View>(private val creator: (inflater: LayoutInflater, root: ViewGroup, attachToRoot: Boolean) -> V){
    fun inflate(inflater: LayoutInflater, root: ViewGroup, attachToRoot: Boolean):VirtualViewBinding<V> =
        VirtualViewBinding(creator(inflater, root, attachToRoot))
}