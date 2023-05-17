package me.lwb.adapter.loadmore

/**
 * Created by ve3344@qq.com.
 * 接受ui事件
 */
interface UiReceiver {
    /**
     * 重新加载
     */
    fun reload()

    fun cancel()
    /**
     * 错误重试
     */
    fun retry()

    /**
     * 加载更多
     */
    fun onItemAccess(index:Int)
}