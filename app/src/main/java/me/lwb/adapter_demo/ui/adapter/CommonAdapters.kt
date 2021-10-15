package me.lwb.adapter_demo.ui.adapter

import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import me.lwb.adapter_demo.R
import me.lwb.adapter_demo.base.ui.VirtualViewBindingInflater
import me.lwb.adapter_demo.databinding.FooterProjectBinding
import me.lwb.adapter_demo.databinding.HeaderProjectBinding
import me.lwb.adapter_demo.databinding.LayoutEmptyBinding
import me.lwb.bindinadapter.paging.*
import me.lwb.utils.android.ext.showChildOnly

/**
 * Created by ve3344@qq.com on 2021/10/12.
 */



fun PagingDataAdapter<*, *>.configPagingWithHeader(): ConcatAdapter {
    //重新对Adapter进行依次拼接
    return rearrange() {
        //添加 header
        layoutContent(VirtualViewBindingInflater {parent,_,_-> ImageView(parent.context) }::inflate) {
            binding.contentView.setImageResource(R.drawable.ic_launcher_background)
        }
        //添加 header
        layoutContent(HeaderProjectBinding::inflate) {
            binding.tips.text = "Header2"
        }
        //添加分页内容
        pagingContent()
        //添加加载状态
        loadStateContent(FooterProjectBinding::inflate) { loadState ->
            loadState.onIdle {
                //空闲，隐藏所有view
                binding.root.showChildOnly { false }
            }.onLoading {
                //加载中，显示加载中
                binding.root.showChildOnly { it == binding.progressBar }
            }.onEnd {
                //没有更多数据了，显示没有更多数据
                binding.root.showChildOnly { it == binding.reachEnd }
            }.onError {
                //加载失败，显示加载失败、
                binding.root.showChildOnly { it == binding.loadError }
                binding.loadError.setOnClickListener {
                    //点击重试
                    retry()
                    binding.root.showChildOnly { it == binding.progressBar }
                    //手动显示加载中
                }
            }
        }
        //添加数据空布局（数据为空时显示）
        emptyStateContent(LayoutEmptyBinding::inflate)
        //添加footer
        layoutContent(HeaderProjectBinding::inflate) {
            binding.tips.text = "Footer1"
        }
    }
}