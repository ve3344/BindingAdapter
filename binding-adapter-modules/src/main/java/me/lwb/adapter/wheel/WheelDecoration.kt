package me.lwb.adapter.wheel

import androidx.recyclerview.widget.RecyclerView

abstract class WheelDecoration() : RecyclerView.ItemDecoration() {
    internal var module: RecyclerWheelViewModule? = null
    val wheelItemSize: Int get() = module?.itemSize ?: 0
    val wheelOffset: Int get() = module?.offset ?: 0
    val orientation: Int get() = module?.orientation ?: RecyclerWheelViewModule.VERTICAL
}