package me.lwb.adapter.loadmore

import java.io.Serializable

/**
 * 加载进度，类似于LoadParam
 *
 */
interface LoadMoreProgress {
    /**
     * 是否第一页，不强制实现
     */
    val isFirstProgress: Boolean
    /**
     * 恢复第一页
     */
    fun resetProgress()
    /**
     * 下一页
     */
    fun nextProgress()
}
/**
 * 一般的分页进度
 *
 */
data class PageProgress(
    private val firstPageIndex: Int = DEFAULT_PAGE_INDEX,
    val pageSize: Int = DEFAULT_PAGE_SIZE,
    var pageIndex: Int = firstPageIndex
) : LoadMoreProgress, Serializable {

    /**
     * 是否为第一页
     */
    override val isFirstProgress: Boolean get() = firstPageIndex == pageIndex

    /**
     * 恢复第一页
     */
    override fun resetProgress() {
        pageIndex = firstPageIndex
    }

    /**
     * 下一页
     */
    override fun nextProgress() {
        ++pageIndex
    }

    companion object {
        var DEFAULT_PAGE_INDEX = 1
        var DEFAULT_PAGE_SIZE = 10
    }
}