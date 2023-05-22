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


}


