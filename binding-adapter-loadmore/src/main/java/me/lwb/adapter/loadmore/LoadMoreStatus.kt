package me.lwb.adapter.loadmore

sealed interface LoadMoreStatus {
    /**
     * 空闲
     */
    object Idle : LoadMoreStatus
    /**
     * 没有更多数据了
     * @param isReload 当前是否为reload，reload状态的NoMore，相当于列表是空的
     */
    class NoMore(val isReload: Boolean) : LoadMoreStatus
    /**
     * 加载失败
     * @param isReload 当前是否为reload，reload状态的Fail，相当于列表加载失败了
     * @param throwable 失败原因
     */
    class Fail(val isReload: Boolean,val throwable: Throwable) : LoadMoreStatus
    /**
     * 加载失败
     * @param isReload 当前是否为reload
     */
    class Loading(val isReload: Boolean) : LoadMoreStatus
}