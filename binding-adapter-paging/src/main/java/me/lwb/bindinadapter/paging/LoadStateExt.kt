package me.lwb.bindinadapter.paging
import androidx.paging.LoadState

/**
 * Created by ve3344@qq.com on 2021/10/9.
 * 对LoadState的快捷判断
 */

/**
 * 没有更多数据
 */
inline fun LoadState.onEnd(onEnd:()->Unit): LoadState {
    if (this is LoadState.NotLoading && this.endOfPaginationReached) {
        onEnd()
    }
    return this
}
/**
 * 加载中
 */
inline fun LoadState.onLoading(onLoading:()->Unit): LoadState {
    if (this is LoadState.Loading) {
        onLoading()
    }
    return this
}
/**
 * 加载错误
 */
inline fun LoadState.onError(onError:(e:Throwable)->Unit): LoadState {
    if (this is LoadState.Error) {
        onError(this.error)
    }
    return this
}
/**
 * 空闲
 */
inline fun LoadState.onIdle(onIdle:()->Unit): LoadState {
    if (this is LoadState.NotLoading&& !this.endOfPaginationReached) {
        onIdle()
    }
    return this
}