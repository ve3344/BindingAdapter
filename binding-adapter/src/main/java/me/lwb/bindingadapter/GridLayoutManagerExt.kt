package me.lwb.bindingadapter

import androidx.recyclerview.widget.GridLayoutManager

/**
 * Created by luowenbin on 2021/10/14.
 */


fun GridLayoutManager.configSingleViewSpan(range: (position: Int) -> Boolean) {
    val oldSpanSizeLookup = spanSizeLookup
    val oldSpanCount = spanCount

    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (range(position)) oldSpanCount else oldSpanSizeLookup.getSpanSize(position)
        }
    }
}

