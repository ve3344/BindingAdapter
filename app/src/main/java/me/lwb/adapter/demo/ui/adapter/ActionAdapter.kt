package me.lwb.adapter.demo.ui.adapter

import me.lwb.adapter.demo.databinding.ItemActionBinding
import me.lwb.adapter.BindingAdapter

/**
 * Created by ve3344@qq.com.
 */
fun ActionAdapter(vararg list: Pair<CharSequence,Runnable>) =
    BindingAdapter(ItemActionBinding::inflate, list.asList()) { _, item ->
        itemBinding.action.text = item.first
        itemBinding.action.setOnClickListener { item.second.run() }
    }