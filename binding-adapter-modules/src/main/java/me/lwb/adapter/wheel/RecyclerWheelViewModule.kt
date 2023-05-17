package me.lwb.adapter.wheel

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import me.lwb.adapter.stick.doOnNextLayout


@SuppressLint("NotifyDataSetChanged")
open class RecyclerWheelViewModule(val recyclerView: RecyclerView) {

    /**
     * 偏移，上下的空白数量
     */
    var offset = 1
        set(value) {
            field = value
            startOffsetAdapter?.notifyDataSetChanged()
            endOffsetAdapter?.notifyDataSetChanged()
        }


    private var currentSelectedPosition: Int = 0
    private var currentDeterminePosition: Int = currentSelectedPosition

    /**
     * 当前选择
     */
    var selectedPosition: Int
        get() = currentSelectedPosition
        set(value) {
            if (currentSelectedPosition == value) {
                return
            }
            if (value !in 0 until (dataAdapter?.itemCount?:0)){
                return
            }
            recyclerView.stopScroll()
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(value, 0)
            updateSelectPosition(value)
            recyclerView.doOnNextLayout {
                wheelLinearSnapHelper.snapToTargetExistingView(recyclerView)
                notifySelectDetermine(value)

            }

        }

    /**
     * fling滚动速度比例
     */
    var flingVelocityFactor: Float = 0.33f

    /**
     * 单个item高度
     */
    internal var itemSize = 0
    private var dataAdapter: RecyclerView.Adapter<*>? = null
    private var startOffsetAdapter: OffsetAdapter? = null
    private var endOffsetAdapter: OffsetAdapter? = null

    private val wheelLinearSnapHelper = WheelLinearSnapHelper()

    var orientation: Int
        get() {
            val layoutManager = recyclerView.layoutManager
            return if (layoutManager is LinearLayoutManager) {
                layoutManager.orientation
            } else {
                VERTICAL
            }
        }
        set(value) {
            recyclerView.layoutManager = LinearLayoutManager(recyclerView.context, value, false)
        }

    init {
        if (recyclerView.layoutManager !is LinearLayoutManager){
            recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        }
        wheelLinearSnapHelper.attachToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    notifySelectDetermine(currentSelectedPosition)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (itemSize == 0) {
                    return
                }
                val layoutManager = recyclerView.layoutManager ?: return
                val globalAdapter = recyclerView.adapter ?: return
                val dataAdapter = dataAdapter ?: return
                val snapView = wheelLinearSnapHelper.findSnapView(layoutManager) ?: return
                val snapViewHolder = recyclerView.getChildViewHolder(snapView)
                val snapViewPositionGlobal = layoutManager.getPosition(snapView)

                val snapViewPosition = globalAdapter.findRelativeAdapterPositionIn(
                    dataAdapter,
                    snapViewHolder,
                    snapViewPositionGlobal
                )
                if (snapViewPosition == RecyclerView.NO_POSITION) {
                    return
                }

                updateSelectPosition(snapViewPosition)
            }
        })

        recyclerView.adapter?.let { setAdapter(it) }

    }

    /**
     * 选中的item确定了
     */
    private fun notifySelectDetermine(value: Int) {
        if (currentDeterminePosition==value){
            return
        }
        currentDeterminePosition = value
        onSelectChangeListener?.invoke(value)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateSelectPosition(
        selectedPositionNew: Int,
    ) {
        if (currentSelectedPosition != selectedPositionNew) {
            currentSelectedPosition = selectedPositionNew
            onScrollingSelectListener?.invoke(selectedPositionNew)
            recyclerView.post { dataAdapter?.notifyDataSetChanged() }
        }
    }

    var onScrollingSelectListener: ((Int) -> Unit)? = null

    var onSelectChangeListener: ((Int) -> Unit)? = null
    fun observeSelectChange(onSelectListener: (Int) -> Unit) {
        this.onSelectChangeListener = onSelectListener
        onSelectListener.invoke(currentSelectedPosition)
    }

    /**
     * 设置数据adapter
     */
    @Suppress("UNCHECKED_CAST")
    fun setAdapter(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) {
        itemSize = 0
        currentSelectedPosition = 0
        currentDeterminePosition = 0
        if (adapter == null) {
            dataAdapter = null
            startOffsetAdapter = null
            endOffsetAdapter = null
            recyclerView.adapter = null
            return
        }
        if (adapter == dataAdapter) {
            return
        }

        dataAdapter = adapter
        bindStateToAdapter(adapter)
        startOffsetAdapter = OffsetAdapter(adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
        endOffsetAdapter = OffsetAdapter(adapter)

        recyclerView.adapter = ConcatAdapter(
            startOffsetAdapter,
            adapter,
            endOffsetAdapter
        )
    }

    /**
     * 测量item大小（高度）
     */
    private fun measureItemSize(itemView: View) {
        if (itemSize == 0) {
            val rect = itemView.measureSize()
            if (orientation == VERTICAL) {
                val lp = itemView.layoutParams
                itemSize = if (lp is ViewGroup.MarginLayoutParams) {
                    rect.height() + lp.leftMargin + lp.rightMargin
                } else {
                    rect.height()
                }
                recyclerView.layoutParams = recyclerView.layoutParams.apply {
                    height = (offset + offset + 1) * itemSize
                }
            } else {
                val lp = itemView.layoutParams
                itemSize = if (lp is ViewGroup.MarginLayoutParams) {
                    rect.width() + lp.topMargin + lp.bottomMargin
                } else {
                    rect.width()
                }

                recyclerView.layoutParams = recyclerView.layoutParams.apply {
                    width = (offset + offset + 1) * itemSize
                }
            }

        }
    }

    /**
     * 上下偏移
     */
    internal inner class OffsetAdapter(private val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            adapter.createViewHolder(parent, viewType)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (adapter.itemCount > 0) {
                adapter.onBindViewHolder(holder, 0)
            }
            holder.itemView.visibility = View.INVISIBLE
            measureItemSize(holder.itemView)
        }

        override fun getItemCount(): Int = offset
    }

    /**
     * 对齐
     */
    private inner class WheelLinearSnapHelper : LinearSnapHelper() {
        override fun onFling(velocityX: Int, velocityY: Int): Boolean =
            super.onFling(
                (velocityX * flingVelocityFactor).toInt(),
                (velocityY * flingVelocityFactor).toInt()
            )
    }


    /**
     * 横线
     */
    private var wheelDecoration: WheelDecoration? = null

    /**
     * 设置滚轮中间线条
     */
    fun setWheelDecoration(decoration: WheelDecoration?) {
        if (wheelDecoration==decoration){
            return
        }
        wheelDecoration?.let {
            it.module = null
            recyclerView.removeItemDecoration(it)
        }
        decoration?.let {
            it.module = this
            recyclerView.addItemDecoration(it)
            wheelDecoration = it
        }
    }

    companion object {
        val HORIZONTAL = RecyclerView.HORIZONTAL
        val VERTICAL = RecyclerView.VERTICAL
    }

    private fun LinearSnapHelper.snapToTargetExistingView(mRecyclerView: RecyclerView) {
        val layoutManager = mRecyclerView.layoutManager ?: return
        val snapView = findSnapView(layoutManager) ?: return
        val snapDistance = calculateDistanceToFinalSnap(layoutManager, snapView)?:return
        if (snapDistance[0] != 0 || snapDistance[1] != 0) {
            mRecyclerView.smoothScrollBy(snapDistance[0], snapDistance[1])
        }
    }
}

fun RecyclerView.setupWheelModule(): RecyclerWheelViewModule {
    return RecyclerWheelViewModule(this)
}

