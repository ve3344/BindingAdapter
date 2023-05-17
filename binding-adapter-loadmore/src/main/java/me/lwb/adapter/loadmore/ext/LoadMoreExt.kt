package me.lwb.adapter.loadmore.ext

import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import me.lwb.adapter.MultiTypeBindingAdapter
import me.lwb.adapter.loadmore.LoadMoreData
import me.lwb.adapter.loadmore.LoadMoreDataFetcher
import me.lwb.adapter.loadmore.LoadMoreProgress
import me.lwb.adapter.loadmore.setupLoadMoreModule

/**
 * Created by ve3344@qq.com.
 */
fun <I : Any, V : ViewBinding, P : LoadMoreProgress> MultiTypeBindingAdapter<I, V>.loadMoreFrom(
    coroutineScope: CoroutineScope,
    pageProgress: P,
    fetcher: LoadMoreDataFetcher<I, P>
) = setupLoadMoreModule().also {
    it.setDataSource(coroutineScope, LoadMoreData(coroutineScope,pageProgress,fetcher).source)
}