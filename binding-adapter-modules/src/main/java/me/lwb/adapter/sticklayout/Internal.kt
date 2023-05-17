package me.lwb.adapter.sticklayout

import android.view.View
import android.view.ViewGroup
import me.lwb.adapter.modules.R

/**
 * Created by ve3344@qq.com.
 */
internal val View.layoutParamsCast get() = layoutParams as StickContainerLayout.LayoutParams

internal val ViewGroup.childrenSequence
    get() = sequence {
        repeat(childCount) {
            yield(getChildAt(it))
        }
    }

internal val View.measuredHeightWithVisible
    get() = if (visibility==View.VISIBLE) measuredHeight+(layoutParamsCast.topMargin+layoutParamsCast.bottomMargin) else 0

internal val View.viewOffsetHelper: ViewOffsetHelper
    get() = (getTag(R.id.binding_adapter_view_offset_helper) as ViewOffsetHelper?) ?: ViewOffsetHelper(
        this
    ).also {
        setTag(R.id.binding_adapter_view_offset_helper, it)
    }