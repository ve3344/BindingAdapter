package me.lwb.adapter.loadmore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * 分页数据
 * @param uiReceiver ui事件的接受器
 * @param dataFlow 数据流由若干个Update 和若干个Append组成，第一个必定是Replace
 */
class LoadMoreDataSource<T>(
    val uiReceiver: UiReceiver,
    val dataFlow: Flow<DataChangeEvent<T>>,
    val statusFlow: StateFlow<LoadMoreStatus>,
) {
    /**
     * 数据流的单位，就是1次加载的数据集
     */
    class DataChangeEvent<T>(val data: Collection<T>, val isAppend: Boolean) {
        override fun toString() =
            if (isAppend) "Append(size=${data.size})" else "Replace(size=${data.size})"
    }
}


sealed interface LoadMoreStatus {
    /**
     * 空闲
     */
    object Idle : LoadMoreStatus
    /**
     * 没有更多数据了
     * @param isReload 当前是否为reload
     */
    class NoMore(val isReload: Boolean) : LoadMoreStatus
    class Fail(val isReload: Boolean,val throwable: Throwable) : LoadMoreStatus
    class Loading(val isReload: Boolean) : LoadMoreStatus
}