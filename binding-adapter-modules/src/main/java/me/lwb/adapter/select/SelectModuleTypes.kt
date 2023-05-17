package me.lwb.adapter.select

import me.lwb.adapter.BindingViewHolder
import me.lwb.adapter.modules.R

/**
 * Created by ve3344@qq.com.
 */

var BindingViewHolder<*>.isItemSelected: Boolean by me.lwb.adapter.ext.viewHolderTag(
    R.id.binding_adapter_view_holder_tag_selected,
    false
)