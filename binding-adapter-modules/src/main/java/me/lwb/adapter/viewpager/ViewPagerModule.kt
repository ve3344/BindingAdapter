package me.lwb.adapter.viewpager

import android.view.View
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.setupViewPagerModule(pagerSnapHelper: PagerSnapHelper = PagerSnapHelper()) =
    ViewPagerModule(this, pagerSnapHelper)

interface RecyclerViewViewPagerListener {
    fun onPageAttach(view: View, position: Int)

    fun onPageDetach(view: View, isNext: Boolean, position: Int)

    fun onPageSelected(view: View, position: Int, isBottom: Boolean)
}

class ViewPagerModule(
    val recyclerView: RecyclerView,
    val pagerSnapHelper: PagerSnapHelper = PagerSnapHelper(),
) :
    RecyclerView.OnChildAttachStateChangeListener, RecyclerView.OnScrollListener() {

    var currentItem = -1
        private set
    var viewPagerListener: RecyclerViewViewPagerListener? = null

    init {
        pagerSnapHelper.attachToRecyclerView(recyclerView)
        recyclerView.addOnChildAttachStateChangeListener(this)
        recyclerView.addOnScrollListener(this)
        initCurrent()
        recyclerView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> initCurrent() }

    }

    private fun initCurrent() {
        onScrollStateChanged(recyclerView,recyclerView.scrollState)
    }

    override fun onChildViewAttachedToWindow(view: View) {
        if (recyclerView.childCount == 1) {
            val layoutManager = recyclerView.layoutManager ?: return
            val position = layoutManager.getPosition(view)
            viewPagerListener?.onPageAttach(view, position)
        }
    }


    override fun onChildViewDetachedFromWindow(view: View) {
        val layoutManager = recyclerView.layoutManager ?: return
        val position = layoutManager.getPosition(view)
        viewPagerListener?.onPageDetach(view, directionNext, position)
    }


    private var directionNext = true
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState != RecyclerView.SCROLL_STATE_IDLE) {
            return
        }
        val layoutManager = recyclerView.layoutManager ?: return
        val view = pagerSnapHelper.findSnapView(layoutManager) ?: return
        val adapter = recyclerView.adapter ?: return
        val position = layoutManager.getPosition(view)
        val isBottom = position == adapter.itemCount - 1
        if (position==currentItem){
            return
        }

        currentItem = position
        viewPagerListener?.onPageSelected(view, position, isBottom)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val d = if (dx != 0) dx else dy
        directionNext = d > 0
    }
}