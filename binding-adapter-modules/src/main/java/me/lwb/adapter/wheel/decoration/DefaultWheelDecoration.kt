package me.lwb.adapter.wheel.decoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView
import me.lwb.adapter.wheel.RecyclerWheelViewModule
import me.lwb.adapter.wheel.WheelDecoration

class DefaultWheelDecoration(
    @Px private val marginStart: Int = 0,
    @Px private val marginEnd: Int = 0,
    @Px private val lineWidth: Int,
    @ColorInt private val lineColor: Int = Color.BLACK,
) : WheelDecoration() {

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        if (orientation == RecyclerWheelViewModule.VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    var linePaint = Paint().apply {
        color = lineColor
        strokeWidth = lineWidth.toFloat()
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val top: Int
        val bottom: Int
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.clipToPadding) {
            top = parent.paddingTop + marginStart
            bottom = parent.height - parent.paddingBottom - marginEnd
            canvas.clipRect(
                parent.paddingLeft, top,
                parent.width - parent.paddingRight, bottom
            )
        } else {
            top = 0 + marginStart
            bottom = parent.height - marginEnd
        }


        val left = wheelItemSize * wheelOffset.toFloat()
        val right = (left + wheelItemSize)
        canvas.drawLine(
            left,
            top.toFloat(),
            left,
            bottom.toFloat(),
            linePaint
        )
        canvas.drawLine(
            right,
            top.toFloat(),
            right,
            bottom.toFloat(),
            linePaint
        )
        canvas.restore()
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val top = wheelItemSize * wheelOffset.toFloat()
        val bottom = top + wheelItemSize

        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft + marginStart
            right = parent.width - parent.paddingRight - marginEnd
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0 + marginStart
            right = parent.width - marginEnd
        }

        canvas.drawLine(
            left.toFloat(),
            top,
            right.toFloat(),
            top,
            linePaint
        )
        canvas.drawLine(
            left.toFloat(),
            bottom,
            right.toFloat(),
            bottom,
            linePaint
        )
        canvas.restore()
    }
}