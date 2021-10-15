package me.lwb.adapter_demo.base.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn

/**
 * Created by ve3344@qq.com on 2021/9/6.
 */
/**
 * 通用加载器
 */
typealias PageLoader<I> = suspend (pageIndex: Int, loadSize: Int, reachedEnd: () -> Unit) -> List<I>
/**
 * 通用分页加载
 */
fun <T : Any> ViewModel.pageData(
    pagingConfig: PagingConfig = PagingConfig(pageSize = 4),
    loader: PageLoader<T>
) =
    Pager(pagingConfig) { me.lwb.adapter_demo.base.ui.SimplePagingSource(loader) }
        .flow
        .asLiveData()
/**
 * 通用带缓存分页加载
 */
fun <T : Any> ViewModel.pageDataWithCache(
    pagingConfig: PagingConfig = PagingConfig(pageSize = 4),
    loader: PageLoader<T>
) =
    Pager(pagingConfig) { me.lwb.adapter_demo.base.ui.SimplePagingSource(loader) }
        .flow
        .cachedIn(viewModelScope)
        .asLiveData()
