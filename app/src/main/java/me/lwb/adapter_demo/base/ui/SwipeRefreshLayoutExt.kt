package me.lwb.adapter_demo.base.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * Created by ve3344@qq.com on 2021/9/23.
 */
/**
 * 绑定SwipeRefreshLayout和PagingDataAdapter 的刷新状态
 */
fun SwipeRefreshLayout.bindAdapter(adapter: PagingDataAdapter<*, *>): SwipeRefreshLayout {
    this.setOnRefreshListener {
        adapter.refresh()
    }
    adapter.addLoadStateListener { this.isRefreshing = it.refresh is LoadState.Loading }
    return this
}

/**
 * 为View包裹ViewGroup
 */
fun <T : ViewGroup> View.wrapViewGroup(block: (Context) -> T): T {
    val viewGroup = (parent as? ViewGroup)
    val index = viewGroup?.indexOfChild(this) ?: -1

    viewGroup?.removeView(this)

    val child = this
    val childLayoutParams = layoutParams

    return block(context).apply {
        viewGroup?.addView(this, index, childLayoutParams)
        addView(
            child,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }
}

/**
 * 为View包裹SwipeRefreshLayout
 */
fun View.wrapSwipeRefreshLayout() = wrapViewGroup { SwipeRefreshLayout(context) }

/**
 * 为RecyclerView 包裹SwipeRefreshLayout 并绑定刷新
 */
fun RecyclerView.wrapSwipeRefresh(adapter: PagingDataAdapter<*, *>) {
    this.wrapSwipeRefreshLayout().bindAdapter(adapter)
}