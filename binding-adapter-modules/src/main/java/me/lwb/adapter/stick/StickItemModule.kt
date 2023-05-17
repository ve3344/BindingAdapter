package me.lwb.adapter.stick

import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView


/**
 * Created by ve3344@qq.com.
 * @param recyclerView 列表
 * @param stickAdapter 目标adapter，悬浮的item在哪个adapter,一般和recyclerView.adapter 一样
 * @param stickItemPredicate 判断是否是悬浮的依据，position 是stickAdapter的局部，无需再转换
 */
open class StickItemModule(
    val recyclerView: RecyclerView,
    val stickAdapter: RecyclerView.Adapter<*>,
    val stickItemPredicate: StickItemPredicate
) {

    private val TAG: String = this.javaClass.simpleName

    private val container = recyclerView.parent as ViewGroup

    var stickItemListener: StickItemListener? = null

    //正在显示
    private var currentStickItem: StickingItem? = null

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            updateStickingItem()
        }
    }
    private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            detachStickItem()
            updateStickingItem()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            checkStickingItemValid(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            checkStickingItemValid(positionStart, itemCount)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            checkStickingItemValid(positionStart, itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkStickingItemValid(positionStart, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            checkStickingItemValid(fromPosition, itemCount)
            checkStickingItemValid(toPosition, itemCount)
        }
    }
    private val childViewAttachedListener = object :
        RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewAttachedToWindow(view: View) {
            if (currentStickItem == null) {
                updateStickingItem()
            }
        }

        override fun onChildViewDetachedFromWindow(view: View) {
        }
    }

    init {
        recyclerView.addOnScrollListener(scrollListener)
        stickAdapter.registerAdapterDataObserver(adapterDataObserver)
        recyclerView.addOnChildAttachStateChangeListener(childViewAttachedListener)
    }

    fun detach() {
        recyclerView.removeOnScrollListener(scrollListener)
        stickAdapter.unregisterAdapterDataObserver(adapterDataObserver)
        recyclerView.removeOnChildAttachStateChangeListener(childViewAttachedListener)
    }

    private fun checkStickingItemValid(positionStart: Int, itemCount: Int) {
        if (currentStickItem?.anchorPosition in positionStart until positionStart + itemCount) {
            detachStickItem()
            updateStickingItem()
        }
    }

    /**
     * 刷新stickView位置和数据
     */
    private fun updateStickingItem() {
        val adapter = recyclerView.adapter ?: return

        //从顶部view开始找
        val topView = recyclerView.layoutManager?.getChildAt(0) ?: return
        val topViewHolder = recyclerView.getChildViewHolder(topView)
        val topViewPosition = topViewHolder.layoutPosition

        val topViewPositionRelative = adapter.findRelativeAdapterPositionInCompatSelf(
            stickAdapter,
            topViewHolder,
            topViewPosition
        )
        if (topViewPositionRelative == RecyclerView.NO_POSITION) {
            //当前位置不处于目标stickAdapter 范围，一般处于是ConcatAdapter 中的其它adapter
            return detachStickItem()
        }
        //需要悬浮的位置，stickPosition==null 时，表示当前位置没有可以悬浮的，一般是还没滚到悬浮数据的位置
        val anchorPosition =
            findStickAnchorPosition(topViewPositionRelative, true) ?: return detachStickItem()

        if (anchorPosition >= stickAdapter.itemCount) {
            //一般不会出现
            Log.w(
                TAG,
                "Invalid anchorPosition=$anchorPosition ,itemCount=${stickAdapter.itemCount}"
            )
            return
        }

        //获取当前悬浮的item
        val stickingItem = makeStickingItem(anchorPosition)

        //绑定数据
        bindStickViewData(stickingItem, anchorPosition)
        //设置位置便偏移
        layoutStickView(stickingItem, topViewPositionRelative, anchorPosition)
    }

    private fun bindStickViewData(stickingItem: StickingItem, anchorPosition: Int) {
        if (stickingItem.anchorPosition != anchorPosition) {
            stickAdapter.bindViewHolderCast(stickingItem.viewHolder, anchorPosition)
            val old = stickingItem.anchorPosition
            stickingItem.anchorPosition = anchorPosition
            stickItemListener?.onStickAnchorPositionChange(old, anchorPosition)
        }
    }

    private fun layoutStickView(
        stickingItem: StickingItem,
        topViewPositionRelative: Int,
        anchorPosition: Int
    ) {
        //当前悬浮的view
        val stickView = stickingItem.viewHolder.itemView
        stickView.parent.bringChildToFront(stickView)
        //是否已经滚到下一个悬浮的item了
        //下一个滚上来的时候，需要把正在悬浮的item顶上去，通过translation 实现
        stickView.doOnLayout {
            offsetCurrentStickView(stickView, topViewPositionRelative)
            stickItemListener?.onStickItemUpdate(anchorPosition, stickView)
        }
    }

    private fun getViewBounds(position: Int?): Rect? {
        position ?: return null

        val adapter = recyclerView.adapter ?: return null
        val positionGlobal =
            adapter.findGlobalAdapterPositionInCompatSelf(stickAdapter, position)
        val view =
            recyclerView.findViewHolderForLayoutPosition(positionGlobal)?.itemView
                ?: return null

        return view.getBoundsInContainer()
    }

    private fun offsetCurrentStickView(
        stickView: View,
        topViewPositionRelative: Int,
    ) {

        val stickViewBounds = stickView.getBoundsInContainer()
        val nextAnchorPosition = findStickAnchorPosition(topViewPositionRelative, false)
        val nextAnchorViewBounds = getViewBounds(nextAnchorPosition)

        val orientation = recyclerView.layoutManager?.orientation() ?: return


        if (orientation == RecyclerView.VERTICAL) {
            val offset =
                if (nextAnchorViewBounds?.isOverlappingVertical(stickViewBounds) == true) {
                    (nextAnchorViewBounds.top - stickViewBounds.bottom)
                } else {
                    0
                }
            stickView.translationY = offset.toFloat()

        } else {
            val offset =
                if (nextAnchorViewBounds?.isOverlappingHorizontal(stickViewBounds) == true) {
                    (nextAnchorViewBounds.left - stickViewBounds.right)
                } else {
                    0
                }
            stickView.translationX = offset.toFloat()
        }
    }


    private fun detachStickItem() {
        currentStickItem?.let {
            container.removeView(it.viewHolder.itemView)
            stickItemListener?.onStickItemDetach(it.anchorPosition, it.viewHolder.itemView)
            currentStickItem = null
        }
    }

    /**
     * 创建悬浮的View
     */
    private fun makeStickingItem(position: Int): StickingItem {
        val cache = currentStickItem
        val adapter = stickAdapter
        val viewType = adapter.getItemViewType(position)

        if (cache?.viewType == viewType) {
            return cache
        }
        detachStickItem()
        val viewHolder = adapter.createViewHolder(recyclerView, viewType)

        val layoutParams = viewHolder.itemView.layoutParams as? RecyclerView.LayoutParams
        if (layoutParams != null) {
            container.addView(viewHolder.itemView, FrameLayout.LayoutParams(layoutParams))
        } else {
            container.addView(viewHolder.itemView)
        }
        stickItemListener?.onStickItemAttach(position, viewHolder.itemView)

        return StickingItem(viewHolder).also { currentStickItem = it }
    }


    private fun findStickAnchorPosition(fromPosition: Int, forward: Boolean): Int? {
        val range =
            if (forward) (fromPosition downTo 0) else (fromPosition + 1 until stickAdapter.itemCount)
        for (i in range) {
            if (stickItemPredicate.isStickItem(i)) {
                return i
            }
        }
        return null
    }


    private fun View.getBoundsInContainer(): Rect {
        val bounds = getBounds()
        var p = parent
        while (p != null && p != container) {
            p as ViewGroup
            bounds.offset(p.getBounds())
            p = p.parent
        }
        return bounds
    }

    private data class StickingItem(
        val viewHolder: RecyclerView.ViewHolder
    ) {
        val viewType: Int get() = viewHolder.itemViewType
        var anchorPosition: Int = -1
    }

    fun interface StickItemPredicate {
        fun isStickItem(position: Int): Boolean
    }

    interface StickItemListener {
        fun onStickItemAttach(position: Int, view: View)
        fun onStickItemDetach(position: Int, view: View)
        fun onStickItemUpdate(position: Int, view: View)
        fun onStickAnchorPositionChange(oldPosition: Int, newPosition: Int)

    }
}


/**
 * @param predicate 判断是否是悬浮的依据，position 是stickAdapter的局部，无需再转换
 */
fun RecyclerView.setupStickItemModule(
    predicate: StickItemModule.StickItemPredicate
): StickItemModule {
    val adapter = this.adapter
    require(adapter is RecyclerView.Adapter<*>) { "Set adapter first!" }
    return StickItemModule(this, adapter, predicate)
}

/**
 * @param predicate 判断是否是悬浮的依据，position 是stickAdapter的局部，无需再转换
 */
fun RecyclerView.setupStickItemModule(
    adapter: RecyclerView.Adapter<*>,
    predicate: StickItemModule.StickItemPredicate
): StickItemModule {
    return StickItemModule(this, adapter, predicate)
}