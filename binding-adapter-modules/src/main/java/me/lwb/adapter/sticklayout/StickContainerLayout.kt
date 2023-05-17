package me.lwb.adapter.sticklayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.*
import me.lwb.adapter.modules.R


/**
 * Created by ve3344@qq.com.
 */

class StickContainerLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs), NestedScrollingParent3, NestedScrollingChild3 {
    private val parentHelper: NestedScrollingParentHelper =
        NestedScrollingParentHelper(this)
    private val childHelper: NestedScrollingChildHelper = NestedScrollingChildHelper(this)

    private var contentView: View? = null

    //模式
    var stickMode: StickMode = StickMode.StickingLatest()
        set(value) {
            if (field == value) {
                return
            }
            value.attach(this)
            field = value
            requestLayout()
        }

    //当前滚动距离
    var headerScrollY = 0
        private set

    //最大滚动距离
    val headerMaxScrollSize get() = stickMode.maxScrollSize


    var stickScrollMode: Int = CONSUME_PREFER_CONTENT_SHOW

    var onScrollChangeListener: ((dy: Int) -> Unit)? = null

    init {
        isChildrenDrawingOrderEnabled = true
        orientation = VERTICAL

        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.StickContainerLayout)
        stickScrollMode = typedArray.getInt(
            R.styleable.StickContainerLayout_stick_scroll_mode,
            CONSUME_PREFER_CONTENT_SHOW
        )
        val mode = typedArray.getInt(R.styleable.StickContainerLayout_stick_mode, STICK_MODE_LATEST)

        typedArray.recycle()

        stickMode = when (mode) {
            STICK_MODE_LATEST -> StickMode.StickingLatest()
            STICK_MODE_ALL -> StickMode.StickingAll()
            else -> StickMode.StickingLatest()
        }
    }

    override fun getChildDrawingOrder(childCount: Int, i: Int) = childCount - i - 1

    override fun onFinishInflate() {
        super.onFinishInflate()
        contentView =
            childrenSequence
                .firstOrNull { (it.layoutParams as LayoutParams).isContent }
    }

    //----


    override fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
        dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int, consumed: IntArray
    ) {
        childHelper.dispatchNestedScroll(
            dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
            offsetInWindow, type, consumed
        )
    }

    // NestedScrollingChild2
    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return childHelper.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        childHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return childHelper.hasNestedScrollingParent(type)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
        dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int
    ): Boolean {
        return childHelper.dispatchNestedScroll(
            dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
            offsetInWindow, type
        )
    }


    override fun dispatchNestedPreScroll(
        dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
        dyUnconsumed: Int, offsetInWindow: IntArray?
    ): Boolean {
        return childHelper.dispatchNestedScroll(
            dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
            offsetInWindow
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?
    ): Boolean {
        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, ViewCompat.TYPE_TOUCH)
    }

    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return childHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return childHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    // NestedScrollingChild
    override fun setNestedScrollingEnabled(enabled: Boolean) {
        childHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return childHelper.isNestedScrollingEnabled
    }

    //NestedScrollingParent
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return isEnabled && ViewCompat.SCROLL_AXIS_VERTICAL and axes != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        parentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        parentHelper.onStopNestedScroll(target)
    }

    override fun getNestedScrollAxes(): Int {
        return parentHelper.nestedScrollAxes
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {

        if (dy < 0 && stickScrollMode.and(CONSUME_BEFORE_CONTENT_SCROLL_DOWN) != 0
            || dy > 0 && stickScrollMode.and(CONSUME_BEFORE_CONTENT_SCROLL_UP) != 0
        ) {
            updateHeaderScroll(dx, dy, consumed, type)
        }

    }


    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return dispatchNestedFling(velocityX, velocityY, consumed)

    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {

        if (dyUnconsumed < 0 && stickScrollMode.and(CONSUME_AFTER_CONTENT_SCROLL_DOWN) != 0
            || dyUnconsumed > 0 && stickScrollMode.and(CONSUME_AFTER_CONTENT_SCROLL_UP) != 0
        ) {
            updateHeaderScroll(dxUnconsumed, dyUnconsumed, consumed, type)
        }
    }


    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
    }

    override fun measureChildWithMargins(
        child: View,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ) {
        val layoutParams = child.layoutParams as LayoutParams
        if (layoutParams.isContent) {

            super.measureChildWithMargins(
                child,
                parentWidthMeasureSpec,
                widthUsed,
                parentHeightMeasureSpec,
                heightUsed - headerMaxScrollSize
            )
            return
        }
        super.measureChildWithMargins(
            child,
            parentWidthMeasureSpec,
            widthUsed,
            parentHeightMeasureSpec,
            heightUsed
        )
    }


    override fun onStopNestedScroll(child: View) {
        super.onStopNestedScroll(child)
        parentHelper.onStopNestedScroll(child)

    }

    private fun updateHeaderScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        if (dy == 0) {
            return
        }
        val validDy = (headerScrollY + dy).coerceIn(0..headerMaxScrollSize) - headerScrollY

        if (validDy == 0) {
            return
        }
        onScrollChangeListener?.invoke(validDy)

        headerScrollY += validDy
        consumed[1] = validDy

        offsetChildren(headerScrollY)

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        stickMode.invalidateCache()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        childrenSequence.map { it.viewOffsetHelper }.forEach {
            it.onViewLayout()
            it.applyOffsets()
        }
    }

    private fun calculateChildrenOffsets(
        headerScrollY: Int,
        scrollableViews: List<IndexedValue<View>>
    ): IntArray {
        var remainScrollY = headerScrollY//剩余scroll
        val consumeList = IntArray(childCount)
        for ((index, view) in scrollableViews) {

            val myConsumeScrollY =
                remainScrollY.coerceAtMost(view.measuredHeightWithVisible)//本次消费scroll

            remainScrollY -= myConsumeScrollY
            consumeList[index] = myConsumeScrollY
            if (remainScrollY <= 0) {
                break
            }
        }
        return consumeList

    }

    private fun offsetChildren(headerScrollY: Int) {

        val offsets = calculateChildrenOffsets(headerScrollY, stickMode.getScrollableViews())
        var totalOffset = 0

        childrenSequence
            .map { it.viewOffsetHelper }
            .forEachIndexed { index, viewOffsetHelper ->
                totalOffset -= offsets.getOrElse(index) { 0 }
                viewOffsetHelper.setTopAndBottomOffset(totalOffset)
            }
    }

    override fun canScrollVertically(direction: Int): Boolean {
        val headerCanScroll = if (direction < 0) {
            //up
            headerScrollY > 0
        } else {
            headerScrollY < headerMaxScrollSize
        }
        val contentCanScroll =
            contentView?.canScrollVertically(direction) ?: false

        return headerCanScroll || contentCanScroll
    }


    override fun checkLayoutParams(p: ViewGroup.LayoutParams) = p is LayoutParams

    override fun generateDefaultLayoutParams() = LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    override fun generateLayoutParams(attrs: AttributeSet) =
        LayoutParams(context, attrs)

    override fun generateLayoutParams(p: ViewGroup.LayoutParams) = LayoutParams(p)

    class LayoutParams : LinearLayout.LayoutParams {

        var stickyType = STICK_TYPE_HEADER_SCROLL
        val isHeaderStick get() = stickyType == STICK_TYPE_HEADER_STICK
        val isHeaderScroll get() = stickyType == STICK_TYPE_HEADER_SCROLL
        val isContent get() = stickyType == STICK_TYPE_CONTENT

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val typedArray =
                c.obtainStyledAttributes(attrs, R.styleable.StickContainerLayout_Layout)
            stickyType = typedArray.getInt(
                R.styleable.StickContainerLayout_Layout_stick_type,
                STICK_TYPE_HEADER_SCROLL
            )
            typedArray.recycle()
        }

        constructor(width: Int, height: Int) : super(width, height)
        constructor(p: ViewGroup.LayoutParams) : super(p)
        constructor(source: ViewGroup.MarginLayoutParams) : super(source)
        constructor(source: LinearLayout.LayoutParams) : super(source)
    }

    companion object {
        const val STICK_TYPE_HEADER_SCROLL = 0
        const val STICK_TYPE_HEADER_STICK = 1
        const val STICK_TYPE_CONTENT = 2

        //向上滑动content前，先隐藏header
        const val CONSUME_BEFORE_CONTENT_SCROLL_UP = 0x01

        //向下滑动content前，先显示header
        const val CONSUME_BEFORE_CONTENT_SCROLL_DOWN = 0x02

        //向上滑动content后，再隐藏header
        const val CONSUME_AFTER_CONTENT_SCROLL_UP = 0x04

        //向下滑动content后，再显示header
        const val CONSUME_AFTER_CONTENT_SCROLL_DOWN = 0x08


        //常用1:总是优先操作header
        const val CONSUME_BEFORE_CONTENT =
            CONSUME_BEFORE_CONTENT_SCROLL_UP or CONSUME_BEFORE_CONTENT_SCROLL_DOWN

        //常用2:总是优先显示内容，向上时，先隐藏header，向下时，等内容滚动往再显示header
        const val CONSUME_PREFER_CONTENT_SHOW =
            CONSUME_BEFORE_CONTENT_SCROLL_UP or CONSUME_AFTER_CONTENT_SCROLL_DOWN

        const val STICK_MODE_LATEST = 0
        const val STICK_MODE_ALL = 1
    }
}