package me.lwb.adapter.wheel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.CancellationSignal
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.LinearLayout
import me.lwb.adapter.BindingAdapter
import me.lwb.adapter.ext.postAvoidComputingLayout
import me.lwb.adapter.modules.R
import me.lwb.adapter.wheel.decoration.DefaultWheelDecoration
import me.lwb.adapter.wheel.decoration.DrawableWheelDecoration

/**
 * Created by ve3344@qq.com.
 */
open class LinkageWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

    private val children: MutableList<WheelWrapper> = ArrayList()
    private val currentData: MutableList<DataProvider> = ArrayList()
    val wheels: List<WheelWrapper> get() = children
    var currentPositions: List<Int>
        get() = children.map { it.selectedPosition }
        set(value) {
            for (i in 0 until value.size.coerceAtMost(children.size)) {
                children[i].selectedPosition = value[i]
            }
        }
    val currentItems: List<CharSequence> get() = children.map { it.selectedItem }


    var wheelOffset = 2
    var wheelDecoration: WheelDecoration? = DefaultWheelDecoration(
        lineWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            1f,
            resources.displayMetrics
        ).toInt(),
        lineColor = Color.parseColor("#dddddd")
    )

    private var adapterFactory: AdapterFactory? = null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.LinkageWheelView).let {
            wheelOffset =
                it.getInteger(R.styleable.LinkageWheelView_wheel_offset, 2)
            val dividerSizeDef =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
                    .toInt()
            val divider = it.getDrawable(R.styleable.LinkageWheelView_wheel_divider)
            val dividerSize =
                it.getDimensionPixelSize(
                    R.styleable.LinkageWheelView_wheel_divider_size,
                    dividerSizeDef
                )

            divider?.let {
                wheelDecoration = DrawableWheelDecoration(divider, dividerSize)
            }
            it.recycle()
        }
    }


    /**
     * 设置数据
     */
    fun setData(data: List<DataProvider>) {
        currentData.clear()
        currentData.addAll(data)
        invalidateWheels()
    }

    /**
     * 设置Adapter
     */
    fun setAdapterFactory(factory: AdapterFactory) {
        adapterFactory = factory
        invalidateWheels()
    }

    /**
     * 配置滚轮
     */

    fun invalidateWheels() {
        children.forEach { it.release() }
        children.clear()
        removeAllViews()

        val factory = adapterFactory ?: return
        if (currentData.isEmpty()) {
            return
        }

        var parent: WheelWrapper? = null
        for ((index, dataProvider) in currentData.withIndex()) {
            val wheelItem = instantWheelWrapper(index, factory, parent, dataProvider)

            parent?.observeDetermineChange {
                wheelItem.loadData()
            }

            children.add(wheelItem)
            parent = wheelItem
        }

        parent?.observeDetermineChange {
            post {
                onSelectListener?.invoke()
            }
        }
        children.firstOrNull()?.loadData()
    }


    var onSelectListener: (() -> Unit)? = null

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        children.forEach { it.release() }
        children.clear()
    }

    private fun instantWheelWrapper(
        index: Int,
        factory: AdapterFactory,
        parent: WheelWrapper?,
        dataProvider: DataProvider,
    ): WheelWrapper {
        val wheelView = RecyclerWheelView(context)
        val adapter = factory(index)
        wheelView.wheelModule.setAdapter(adapter)
        wheelView.wheelModule.setWheelDecoration(wheelDecoration)
        wheelView.wheelModule.offset = wheelOffset

        addView(
            wheelView,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f)
        )
        return WheelWrapper(parent, dataProvider, wheelView.wheelModule, adapter)
    }


    fun interface DataProvider {
        fun onLoad(
            cancellationSignal: CancellationSignal,
            parents: Array<WheelWrapper>,
            loadCallback: (Collection<CharSequence>) -> Unit
        )
    }

    class WheelWrapper internal constructor(
        parent: WheelWrapper?,
        private val dataProvider: DataProvider,
        private val wheelModule: RecyclerWheelViewModule,
        private val adapter: BindingAdapter<CharSequence, *>,
    ) {
        var selectedPosition: Int by wheelModule::selectedPosition


        val selectedItem: CharSequence get() = adapter.data[selectedPosition]
        val items: List<CharSequence> get() = adapter.data

        private val parents: Array<WheelWrapper> = getPath(parent)

        private var cacheCancellationSignal: CancellationSignal? = null
        private fun getPath(parent: WheelWrapper?): Array<WheelWrapper> =
            if (parent == null) emptyArray() else parent.parents + parent

        @SuppressLint("NotifyDataSetChanged")
        internal fun loadData() {
            try {
                release()
                val cancellationSignal = CancellationSignal().also { cacheCancellationSignal = it }
                dataProvider.onLoad(cancellationSignal, parents) { data ->
                    if (cancellationSignal.isCanceled) {
                        cacheCancellationSignal = null
                        return@onLoad
                    }
                    adapter.recyclerView?.postAvoidComputingLayout {
                        adapter.data.clear()
                        adapter.data.addAll(data)
                        adapter.notifyDataSetChanged()
                        cacheCancellationSignal = null
                        notifyChange()
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }

        }
        internal val isLoading get() = cacheCancellationSignal?.isCanceled==false

        private var selectChangeListener: (() -> Unit)? = null

        internal fun observeDetermineChange(listener: () -> Unit) {
            selectChangeListener = listener
            wheelModule.onSelectChangeListener = { notifyChange() }
        }

        private fun notifyChange() {
            selectChangeListener?.invoke()
        }

        internal fun release() {
            cacheCancellationSignal?.cancel()
            cacheCancellationSignal = null
        }
    }
}