package me.lwb.adapter_demo.base.ui

import androidx.paging.PagingSource
import androidx.paging.PagingState

/**
 * 通用pa
 */
open class SimplePagingSource<I : Any>(
    private val load: PageLoader<I>,
    private val initialKey: Int = 1
) :
    PagingSource<Int, I>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, I> {
        return try {
            val nextPage = params.key ?: initialKey
            var hasMore=true
            val response = load(nextPage, params.loadSize){
                hasMore=false
            }
            LoadResult.Page(
                data = response,
                prevKey = if (nextPage == initialKey) null else nextPage - 1,
                nextKey = if (hasMore) nextPage + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, I>): Int? = null
}