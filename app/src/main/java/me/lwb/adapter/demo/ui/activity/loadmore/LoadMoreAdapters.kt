package me.lwb.adapter.demo.ui.activity.loadmore

import android.widget.ImageView
import me.lwb.adapter.demo.R
import me.lwb.adapter.demo.databinding.FooterProjectBinding
import me.lwb.adapter.demo.databinding.HeaderProjectBinding
import me.lwb.adapter.demo.databinding.LayoutEmptyBinding
import me.lwb.adapter.SingleViewBindingAdapter
import me.lwb.adapter.ext.plus
import me.lwb.adapter.ext.viewBindingOf
import me.lwb.adapter.loadmore.LoadMoreAdapterModule
import me.lwb.adapter.loadmore.LoadMoreStatus
import me.lwb.adapter.loadmore.ext.createEmptyStatusAdapter
import me.lwb.adapter.loadmore.ext.createLoadMoreStatusAdapter
import me.lwb.utils.android.ext.showChildOnly

/**
 * Created by ve3344@qq.com.
 */
object LoadMoreAdapters {
    fun ImageHeaderAdapter() = SingleViewBindingAdapter(viewBindingOf { ImageView(it) }) {
        itemBinding.contentView.setImageResource(R.drawable.ic_launcher_background)
    }

    fun ProjectHeaderAdapter() = SingleViewBindingAdapter(HeaderProjectBinding::inflate) {
        itemBinding.tips.text = "Header2"
    }

    fun ProjectLoadMoreState(module: LoadMoreAdapterModule<*, *>) =
        module.createLoadMoreStatusAdapter(FooterProjectBinding::inflate) {
            when (val status = module.loadStatus) {
                is LoadMoreStatus.Fail -> {
                    isGone = false

                    //加载失败，显示加载失败、
                    itemBinding.root.showChildOnly { it == itemBinding.loadError }
                    itemBinding.loadError.setOnClickListener {
                        //点击重试
                        module.retry()
                        itemBinding.root.showChildOnly { it == itemBinding.progressBar }
                        //手动显示加载中
                    }
                }
                LoadMoreStatus.Idle -> {
                    //空闲，隐藏所有view
                    isGone = true
                }
                is LoadMoreStatus.Loading -> {
                    isGone = false
                    //加载中，显示加载中
                    itemBinding.root.showChildOnly { it == itemBinding.progressBar }
                }
                is LoadMoreStatus.NoMore -> {
                    if (status.isReload) {
                        isGone = true
                        //第一页就没有数据，我们有空布局了，所以不需要显示[没有更多数据了]。
                        return@createLoadMoreStatusAdapter
                    }
                    isGone = false

                    //没有更多数据了，显示没有更多数据
                    itemBinding.root.showChildOnly { it == itemBinding.reachEnd }
                }
            }
        }

    fun ProjectEmptyAdapter(loadMoreAdapterModule: LoadMoreAdapterModule<*, *>) =
        loadMoreAdapterModule.createEmptyStatusAdapter(LayoutEmptyBinding::inflate){

        }

    fun ProjectFooterAdapter() = SingleViewBindingAdapter(HeaderProjectBinding::inflate) {
        itemBinding.tips.text = "Footer1"
    }

    /**
     * 自由组合各个部分Adapter
     */
    fun CommonAdapter(loadMoreAdapterModule: LoadMoreAdapterModule<*, *>) =
        ImageHeaderAdapter() +
                ProjectHeaderAdapter() +
                loadMoreAdapterModule.adapter +
                ProjectLoadMoreState(loadMoreAdapterModule) +
                ProjectEmptyAdapter(loadMoreAdapterModule) +
                ProjectFooterAdapter()

}
