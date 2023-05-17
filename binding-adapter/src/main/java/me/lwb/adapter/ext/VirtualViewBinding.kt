package me.lwb.adapter.ext

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 * Created by ve3344@qq.com.
 */
open class VirtualViewBinding<V : View>(val contentView: V) : ViewBinding {
    override fun getRoot(): View = contentView
}

fun <T : View> viewBindingOf(factory: (Context) -> T): ((inflater: LayoutInflater, root: ViewGroup, attachToRoot: Boolean) -> VirtualViewBinding<T>) =
    { _, root, attach ->
        val rootView = factory(root.context)
        if (attach) {
            root.addView(rootView)
        }
        VirtualViewBinding(rootView)
    }
