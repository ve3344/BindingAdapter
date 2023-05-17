package me.lwb.adapter.viewpager

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView

open class AutoScrollViewPagerModule(
    val pager: RecyclerView,
    private val viewPagerModule: ViewPagerModule,
) {
    var scrollEnable = true
    var autoScrollPeriod: Long = 3000L
    var autoScrollDirectionNext = true
    val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder> =
        requireNotNull(pager.adapter) { "Set adapter first" }

    init {
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                val currentPosition = adapter.itemCount / 2
                pager.scrollToPosition(currentPosition)
            }
        })
    }

    private val autoScrollRunnable: Runnable = object : Runnable {
        override fun run() {
            performScroll()

            if (autoScrollPeriod > 0 && scrollEnable) {
                pager.removeCallbacks(this)
                pager.postDelayed(this, autoScrollPeriod)
            }
        }
    }

    private fun performScroll() {
        if (!scrollEnable){
            return
        }
        var currentPosition = viewPagerModule.currentItem
        val position = currentPosition
        if (autoScrollDirectionNext) {
            if (position < (adapter.itemCount) - 1) {
                currentPosition = position + 1
                pager.smoothScrollToPosition(currentPosition)
            }
        } else {
            if (position > 0) {
                currentPosition = position - 1
                pager.smoothScrollToPosition(currentPosition)

            }
        }
    }


    fun stopAutoScroll() {
        pager.removeCallbacks(autoScrollRunnable)
    }

    fun startAutoScroll() {
        if (autoScrollPeriod > 0 && scrollEnable) {
            pager.removeCallbacks(autoScrollRunnable)
            pager.postDelayed(autoScrollRunnable, autoScrollPeriod)
        }
    }

    fun bindLifecycle(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onStart() {
                startAutoScroll()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onStop() {
                stopAutoScroll()
            }
        })
    }
}

fun ViewPagerModule.setupAutoScrollModule() =
    AutoScrollViewPagerModule(recyclerView, this)