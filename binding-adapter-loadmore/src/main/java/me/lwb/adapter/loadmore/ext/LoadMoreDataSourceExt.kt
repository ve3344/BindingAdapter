package me.lwb.adapter.loadmore.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.lwb.adapter.loadmore.DataChangeEvent
import me.lwb.adapter.loadmore.LoadMoreDataSource

/**
 * Created by ve3344@qq.com.
 */
@PublishedApi
internal inline fun <T : Any, R : Any> LoadMoreDataSource<T>.transformDataFlow(transform: (Flow<DataChangeEvent<T>>) -> Flow<DataChangeEvent<R>>): LoadMoreDataSource<R> {
    return LoadMoreDataSource<R>(uiReceiver, transform(dataFlow), statusFlow)
}


inline fun <T : Any, R : Any> LoadMoreDataSource<T>.transform(crossinline transform: (Collection<T>) -> Collection<R>) =
    transformDataFlow { flow ->
        flow.map {
            when (it) {
                is DataChangeEvent.Replace ->
                    DataChangeEvent.Replace(transform(it.data))
                is DataChangeEvent.Append ->
                    DataChangeEvent.Append(transform(it.data))

            }
        }
    }


fun <T : Any, R : Any> LoadMoreDataSource<T>.map(map: (T) -> R) = transform {
    it.map(map)
}


fun <T : Any> LoadMoreDataSource<T>.filter(map: (T) -> Boolean) = transform {
    it.filter(map)
}


