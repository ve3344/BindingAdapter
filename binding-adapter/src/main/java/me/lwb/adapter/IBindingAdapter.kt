package me.lwb.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

interface IBindingAdapter<V : ViewBinding> {
    val recyclerView: RecyclerView?
    var onAttachedToRecyclerViewListener: ((RecyclerView) -> Unit)?
    var onDetachedToRecyclerViewListener: ((RecyclerView) -> Unit)?
    var onBindViewHolderDelegate: (holder: BindingViewHolder<V>, position: Int, payloads: List<Any>) -> Unit
    var onCreateViewHolderDelegate: (parent: ViewGroup, viewType: Int) -> BindingViewHolder<V>
}