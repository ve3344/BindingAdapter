package me.lwb.adapter.ext

import androidx.recyclerview.widget.RecyclerView

@Suppress("NOTHING_TO_INLINE")
inline fun RecyclerView.postAvoidComputingLayout(block: Runnable) {
    if (this.isComputingLayout) {
        this.post(block)
    } else {
        block.run()
    }
}
