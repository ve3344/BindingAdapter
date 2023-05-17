/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.lwb.adapter.sticklayout

import android.view.View
import androidx.core.view.ViewCompat

/**
 * Utility helper for moving a [View] around using [View.offsetLeftAndRight] and
 * [View.offsetTopAndBottom].
 *
 *
 * Also the setting of absolute offsets (similar to translationX/Y), rather than additive
 * offsets.
 */
class ViewOffsetHelper(val view: View) {
    var layoutTop = 0
        private set
    var layoutLeft = 0
        private set
    var topAndBottomOffset = 0
        private set
    var leftAndRightOffset = 0
        private set
    var isVerticalOffsetEnabled = true
    var isHorizontalOffsetEnabled = true
    fun onViewLayout() {
        layoutTop = view.top
        layoutLeft = view.left
    }

    fun applyOffsets() {
        ViewCompat.offsetTopAndBottom(view, topAndBottomOffset - (view.top - layoutTop))
        ViewCompat.offsetLeftAndRight(view, leftAndRightOffset - (view.left - layoutLeft))
    }

    fun setTopAndBottomOffset(offset: Int): Boolean {
        if (isVerticalOffsetEnabled && topAndBottomOffset != offset) {
            topAndBottomOffset = offset
            applyOffsets()
            return true
        }
        return false
    }

    fun setLeftAndRightOffset(offset: Int): Boolean {
        if (isHorizontalOffsetEnabled && leftAndRightOffset != offset) {
            leftAndRightOffset = offset
            applyOffsets()
            return true
        }
        return false
    }
}