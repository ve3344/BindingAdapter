package me.lwb.adapter.demo.ui.activity.paging

import android.widget.ImageView
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import me.lwb.adapter.demo.R
import me.lwb.adapter.demo.databinding.FooterProjectBinding
import me.lwb.adapter.demo.databinding.HeaderProjectBinding
import me.lwb.adapter.demo.databinding.LayoutEmptyBinding
import me.lwb.adapter.paging.createEmptyStateAdapter
import me.lwb.adapter.paging.createLoadStateAdapter
import me.lwb.adapter.SingleViewBindingAdapter
import me.lwb.adapter.ext.plus
import me.lwb.adapter.ext.viewBindingOf
import me.lwb.utils.android.ext.showChildOnly

/**
 * Created by ve3344@qq.com.
 */
object PagingAdapters {

    fun ImageHeaderAdapter() = SingleViewBindingAdapter(viewBindingOf { ImageView(it) }) {
        itemBinding.contentView.setImageResource(R.drawable.ic_launcher_background)
    }

    fun ProjectHeaderAdapter() = SingleViewBindingAdapter(HeaderProjectBinding::inflate) {
        itemBinding.tips.text = "Header2"
    }

    fun ProjectLoadMoreState(module: PagingDataAdapter<*, *>) =
        module.createLoadStateAdapter(FooterProjectBinding::inflate) {
            when  {
                it is LoadState.Error -> {
                    //加载失败，显示加载失败、
                    itemBinding.root.showChildOnly { it == itemBinding.loadError }
                    itemBinding.loadError.setOnClickListener {
                        //点击重试
                        module.retry()
                        itemBinding.root.showChildOnly { it == itemBinding.progressBar }
                        //手动显示加载中
                    }
                }
                it is LoadState.NotLoading && !it.endOfPaginationReached -> {
                    //空闲，隐藏所有view
                    itemBinding.root.showChildOnly { false }
                }
                it is LoadState.Loading -> {
                    //加载中，显示加载中
                    itemBinding.root.showChildOnly { it == itemBinding.progressBar }
                }
                it is LoadState.NotLoading && it.endOfPaginationReached  -> {
                    //没有更多数据了，显示没有更多数据
                    itemBinding.root.showChildOnly { it == itemBinding.reachEnd }
                }
            }
        }

    fun ProjectEmptyAdapter(loadMoreAdapterModule: PagingDataAdapter<*, *>) =
        loadMoreAdapterModule.createEmptyStateAdapter(LayoutEmptyBinding::inflate)

    fun ProjectFooterAdapter() = SingleViewBindingAdapter(HeaderProjectBinding::inflate) {
        itemBinding.tips.text = "Footer1"
    }

    /**
     * 自由组合各个部分Adapter
     */
    fun CommonAdapter(adapter: PagingDataAdapter<*, *>) =
        ImageHeaderAdapter() +
                ProjectHeaderAdapter() +
                adapter +
                ProjectLoadMoreState(adapter) +
                ProjectEmptyAdapter(adapter) +
                ProjectFooterAdapter()


}


