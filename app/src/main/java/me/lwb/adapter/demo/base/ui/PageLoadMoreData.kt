package me.lwb.adapter.demo.base.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import me.lwb.adapter.loadmore.LoadMoreData
import me.lwb.adapter.loadmore.LoadMoreDataFetcher
import me.lwb.adapter.loadmore.PageProgress

/**
 * Created by ve3344@qq.com.
 */
fun <T> ViewModel.PageLoadMoreData(fetcher: LoadMoreDataFetcher<T, PageProgress>) =
    LoadMoreData(viewModelScope, PageProgress(), fetcher)