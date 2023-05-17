package me.lwb.adapter.wheel.decoration

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView
import me.lwb.adapter.wheel.RecyclerWheelViewModule
import me.lwb.adapter.wheel.WheelDecoration

class DrawableWheelDecoration(
    val drawable: Drawable,
    @Px private val size: Int,
) : WheelDecoration() {
    @Px
    var marginStart: Int = 0

    @Px
    var marginEnd: Int = 0
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        if (orientation == RecyclerWheelViewModule.VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val top: Int
        val bottom: Int
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.clipToPadding) {
            top = parent.paddingTop + marginStart
            bottom = parent.height - parent.paddingBottom - marginEnd
            canvas.clipRect(parent.paddingLeft, top,
                parent.width - parent.paddingRight, bottom)
        } else {
            top = 0 + marginStart
            bottom = parent.height - marginEnd
        }


        val left = wheelItemSize * wheelOffset
        val right = (left + wheelItemSize)
        drawable.setBounds(left,
            top,
            left + size,
            bottom)
        drawable.draw(canvas)

        drawable.setBounds(
            right,
            top,
            right + size,
            bottom)
        drawable.draw(canvas)
        canvas.restore()
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val top = wheelItemSize * wheelOffset
        val bottom = top + wheelItemSize

        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft + marginStart
            right = parent.width - parent.paddingRight - marginEnd
            canvas.clipRect(left, parent.paddingTop, right,
                parent.height - parent.paddingBottom)
        } else {
            left = 0 + marginStart
            right = parent.width - marginEnd
        }
        drawable.setBounds(
            left,
            top,
            right,
            top+size,
        )
        drawable.draw(canvas)

        drawable.setBounds(left,
            bottom,
            right,
            bottom+size)
        drawable.draw(canvas)

        canvas.restore()
    }
}