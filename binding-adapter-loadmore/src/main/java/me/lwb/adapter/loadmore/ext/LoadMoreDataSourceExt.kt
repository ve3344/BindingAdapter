package me.lwb.adapter.loadmore.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.lwb.adapter.loadmore.LoadMoreDataSource

/**
 * Created by ve3344@qq.com.
 */
@PublishedApi
internal inline fun <T : Any, R : Any> LoadMoreDataSource<T>.transformDataFlow(transform: (Flow<LoadMoreDataSource.DataChangeEvent<T>>) -> Flow<LoadMoreDataSource.DataChangeEvent<R>>): LoadMoreDataSource<R> {
    return LoadMoreDataSource<R>(uiReceiver, transform(dataFlow), statusFlow)
}


inline fun <T : Any, R : Any> LoadMoreDataSource<T>.transform(crossinline transform: (Collection<T>) -> Collection<R>) =
    transformDataFlow {
        it.map {
            LoadMoreDataSource.DataChangeEvent(transform(it.data), it.isAppend)
        }
    }


fun <T : Any, R : Any> LoadMoreDataSource<T>.map(map: (T) -> R) = transform {
    it.map(map)
}


fun <T : Any> LoadMoreDataSource<T>.filter(map: (T) -> Boolean) = transform {
    it.filter(map)
}

