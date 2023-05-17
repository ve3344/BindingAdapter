package me.lwb.adapter.loadmore

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * 分页加载模块的数据存储类，类似于Pager的作用
 *
 */
class LoadMoreData<T, P : LoadMoreProgress> constructor(
    private val coroutineScope: CoroutineScope,
    val progress: P,
    fetcher: LoadMoreDataFetcher<T, P>? = null,
) {
    private var currentLoadJob: Job? = null
    private var cacheData: MutableList<T> = ArrayList()
    private val uiReceiver: UiReceiver = UiReceiverImpl()
    private val dataEventFlow = MutableSharedFlow<LoadMoreDataSource.DataChangeEvent<T>>()
    private val statusFlow = MutableStateFlow<LoadMoreStatus>(LoadMoreStatus.Idle)

    private val dataFlow = flow {
        emit(LoadMoreDataSource.DataChangeEvent(cacheData, false))
        emitAll(dataEventFlow)
    }
    val source = LoadMoreDataSource(uiReceiver, dataFlow, statusFlow)

    var preloadCount = 5
    private var fetchMore: LoadMoreDataFetcher<T, P>? = fetcher

    /**
     * 重置fetchMore并重新加载，在大多数情况，尤其是在带参数加载是非常有用，
     * 该方法有个副作用，就是无法在onSaveInstance时保存，如果有onSaveInstance保存请求参数需求时就不要使用
     */
    fun suspendReload(fetchMore: LoadMoreDataFetcher<T, P>) {
        this.fetchMore = fetchMore
        reload()
    }


    fun reload(): Boolean {
        cancelLoad()
        progress.resetProgress()
        return loadMoreOrReload(true)
    }

    private fun retry() {
        if (statusFlow.value is LoadMoreStatus.Fail) {
            statusFlow.value = LoadMoreStatus.Idle
            loadMore()
        }
    }

    private fun loadMore() {
        if (statusFlow.value is LoadMoreStatus.Idle) {
            loadMoreOrReload(false)
        }
    }


    private fun cancelLoad() {
        currentLoadJob?.cancel()
        currentLoadJob = null
        statusFlow.value = LoadMoreStatus.Idle
    }


    /**
     * 加载更多/重新加载
     * @param isReload 重新加载
     */
    private fun loadMoreOrReload(isReload: Boolean): Boolean {
        val fetcher = fetchMore ?: return false
        statusFlow.value = LoadMoreStatus.Loading(isReload)

        currentLoadJob = coroutineScope.launch {
            try {
                val data = fetcher.fetch(progress) ?: emptyList()
                if (data.isNotEmpty()) {
                    progress.nextProgress()
                    if (isReload) {
                        cacheData.clear()
                        cacheData.addAll(data)
                        dataEventFlow.emit(LoadMoreDataSource.DataChangeEvent(data, false))
                    } else {
                        cacheData.addAll(data)
                        dataEventFlow.emit(LoadMoreDataSource.DataChangeEvent(data, true))
                    }
                    statusFlow.value = LoadMoreStatus.Idle
                } else {
                    statusFlow.value = LoadMoreStatus.NoMore(isReload)
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                statusFlow.value = LoadMoreStatus.Fail(isReload, e)
            }
        }
        return true
    }

    private inner class UiReceiverImpl : UiReceiver {
        override fun reload() {
            this@LoadMoreData.reload()
        }

        override fun cancel() {
            this@LoadMoreData.cancelLoad()
        }

        override fun retry() {
            this@LoadMoreData.retry()
        }

        override fun onItemAccess(index: Int) {
            if (cacheData.size - index < preloadCount) {
                this@LoadMoreData.loadMore()
            }
        }

    }
}


