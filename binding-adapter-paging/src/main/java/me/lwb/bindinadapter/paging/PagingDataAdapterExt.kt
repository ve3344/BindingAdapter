package me.lwb.bindinadapter.paging

import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import me.lwb.bindingadapter.*

/**
 * Created by ve3344@qq.com on 2021/10/9.
 * 对Adapter进行重新排列的工具
 */
/**
 * 对Paging Adapter进行重新排列
 */
fun PagingDataAdapter<*, *>.rearrange(
    config: ConcatAdapter.Config = ConcatAdapter.Config.DEFAULT,
    build: PagingConcatAdapterBuilder.() -> Unit
): ConcatAdapter {
    val concatAdapter = ConcatAdapter(config)
    PagingConcatAdapterBuilder(this, concatAdapter).build()
    return concatAdapter
}

class PagingConcatAdapterBuilder(
    private val pagingDataAdapter: PagingDataAdapter<*, *>,
    private val concatAdapter: ConcatAdapter
) {
    /**
     * 分页数据内容
     */
    fun pagingContent(): PagingDataAdapter<*, *> {
        return adapter(pagingDataAdapter)
    }

    /**
     * 加载状态相关的adapter
     */
    fun <V : ViewBinding> loadStateContent(
        creator: LayoutCreator<V>,
        visibleWhenEmpty: Boolean = false,
        converter: LayoutConverterLoadState<V> = {}
    ): SingleViewBindingAdapter<V> {
        return adapter(
            pagingDataAdapter.createLoadStateAdapter(
                creator,
                visibleWhenEmpty,
                converter
            )
        )
    }

    /**
     * 数据空状态相关的adapter
     */
    fun <V : ViewBinding> emptyStateContent(
        creator: LayoutCreator<V>,
    ): BindingAdapter<Unit, V> {
        return adapter(pagingDataAdapter.createEmptyStateAdapter(creator))
    }

    /**
     * header,footer 等
     */
    fun <V : ViewBinding> layoutContent(
        creator: LayoutCreator<V>,
        converter: LayoutConverter1<V> = {}
    ): SingleViewBindingAdapter<V> {
        return adapter(SingleViewBindingAdapter(creator, converter))
    }

    /**
     * 其它adapter
     */
    fun <A : RecyclerView.Adapter<*>> adapter(adapter: A): A {
        concatAdapter.addAdapter(adapter)
        return adapter
    }

}


/**
 * 创建空布局adapter
 */
fun <V : ViewBinding> PagingDataAdapter<*, *>.createEmptyStateAdapter(
    creator: LayoutCreator<V>
): BindingAdapter<Unit, V> {
    val emptyLayoutAdapter = SingleViewBindingAdapter(creator) {
    }
    addLoadStateListener {
        emptyLayoutAdapter.isVisible = itemCount == 0
    }
    return emptyLayoutAdapter
}
open class LoadAdapter<V : ViewBinding>(
    creator: LayoutCreator<V>,
    converter: LayoutConverter1<V> = {}
):SingleViewBindingAdapter<V>(creator,converter)
/**
 * 创建加载状态布局adapter
 * @param visibleWhenEmpty 数据为空时是否显示“没有更多数据”，如果有empty view时可设置为false
 * @param converter 布局配置
 */
fun <V : ViewBinding> PagingDataAdapter<*, *>.createLoadStateAdapter(
    creator: LayoutCreator<V>,
    visibleWhenEmpty: Boolean = false,
    converter: LayoutConverterLoadState<V> = { }
): SingleViewBindingAdapter<V> {
    val loadStateHolder = object {
        var loadState: LoadState = LoadState.NotLoading(endOfPaginationReached = false)
    }
    val adapter = LoadAdapter(creator) { converter(loadStateHolder.loadState) }
    addLoadStateListener {
        loadStateHolder.loadState = it.append
        if ((itemCount == 0 && !visibleWhenEmpty)) {
            adapter.isVisible = false
        } else {
            adapter.isVisible = displayLoadStateAsItem(it.append)
        }
    }
    return adapter
}

/**
 * 需要显示adapter的加载状态
 */
private fun displayLoadStateAsItem(loadState: LoadState): Boolean {
    return loadState is LoadState.Loading || loadState is LoadState.Error || (loadState is LoadState.NotLoading && loadState.endOfPaginationReached)
}