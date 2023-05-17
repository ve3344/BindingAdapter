package me.lwb.adapter.sticklayout

import android.view.View

abstract class StickMode() {
    private var cacheScrollableViews: List<IndexedValue<View>>? = null

    var maxScrollSize: Int = 0
        protected set
    private var parent: StickContainerLayout? = null
    fun attach(parent: StickContainerLayout) {
        this.parent = parent
    }

    open fun invalidateCache() {
        cacheScrollableViews = null
        getScrollableViews()
    }

    open fun getScrollableViews(): List<IndexedValue<View>> {
        val parent=parent?:return emptyList()
        return cacheScrollableViews ?: onCalculateScrollableViews(parent.childrenSequence).also {
            maxScrollSize = onCalculateMaxScrollSize(it)
            cacheScrollableViews = it
        }
    }

    open fun onCalculateMaxScrollSize(it: List<IndexedValue<View>>) =
        it.sumOf { it.value.measuredHeightWithVisible }

    abstract fun onCalculateScrollableViews(children: Sequence<View>): List<IndexedValue<View>>


    /**
     * 最后一个
     */
    class StickingLatest() : StickMode() {
        //0,[1],2,3,[4],[5],6,[7]
        //0->2->3->[1]->[4]->6->[5]
        override fun onCalculateScrollableViews(children: Sequence<View>): List<IndexedValue<View>> {


            return buildList {
                var lastStick: IndexedValue<View>? = null
                for ((index, view) in children.withIndex()) {
                    val params = view.layoutParamsCast
                    when {
                        params.isHeaderStick -> {
                            if (lastStick != null) {
                                add(lastStick)
                            }
                            lastStick = IndexedValue(index, view)
                        }
                        params.isHeaderScroll -> {
                            add(IndexedValue(index, view))
                        }
                        else -> {}
                    }

                }
            }
        }

    }

    /**
     * 全部
     */
    class StickingAll() : StickMode() {
        //0,[1],2,3,[4],[5],6,[7]
        //0->2->3->6
        override fun onCalculateScrollableViews(children: Sequence<View>): List<IndexedValue<View>> {
            return children.withIndex()
                .filter { it.value.layoutParamsCast.isHeaderScroll }
                .toList()
        }

    }

}

