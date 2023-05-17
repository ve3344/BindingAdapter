package me.lwb.adapter.demo.ui.activity.wheel

import android.graphics.Color
import me.lwb.adapter.demo.databinding.ItemWheelVerticalBinding
import me.lwb.adapter.BindingAdapter
import me.lwb.adapter.wheel.isWheelItemSelected

fun WheelAdapter() =
    BindingAdapter<CharSequence, ItemWheelVerticalBinding>(ItemWheelVerticalBinding::inflate) { _, item ->
        itemBinding.text.text = item
        itemBinding.text.setTextColor(if (isWheelItemSelected) Color.BLACK else Color.GRAY)
    }
