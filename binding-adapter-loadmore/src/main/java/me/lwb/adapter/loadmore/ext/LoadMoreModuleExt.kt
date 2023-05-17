package me.lwb.adapter.loadmore.ext

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import me.lwb.adapter.BindingViewHolder
import me.lwb.adapter.IVisibleAdapter
import me.lwb.adapter.SingleViewBindingAdapter
import me.lwb.adapter.loadmore.LoadMoreAdapterModule
import me.lwb.adapter.loadmore.LoadMoreStatus

/**
 * Created by ve3344@qq.com.
 * 对Adapter进行重新排列的工具
 */
/**
 * 对Paging Adapter进行重新排列
 */
internal typealias LayoutCreator<V> = (inflater: LayoutInflater, root: ViewGroup, attachToRoot: Boolean) -> V
internal typealias LayoutConverterLoadMoreStatus<V> = BindingViewHolder<V>.(item: LoadMoreStatus) -> Unit

@SuppressLint("NotifyDataSetChanged")
internal inline fun <A> A.notifyIfVisible(action: () -> Unit) where A : IVisibleAdapter, A : RecyclerView.Adapter<*> {
    val oldVisible = this.isVisible
    //绑定状态
    action()

    //刷新loadAdapter,可见->可见
    if (this.isVisible && oldVisible) {
        notifyDataSetChanged()
    }

}


/**
 * 创建空布局adapter
 * @param creator 创建布局
 */
fun <V : ViewBinding> LoadMoreAdapterModule<*, *>.createEmptyStatusAdapter(
    creator: LayoutCreator<V>,
    converter: LayoutConverterLoadMoreStatus<V> = { }
): SingleViewBindingAdapter<V> = createLoadMoreStatusAdapter(creator, {
    adapter.itemCount == 0 && it.loadStatus is LoadMoreStatus.NoMore
},converter)

/**
 * 创建加载状态布局adapter
 * @param creator 创建布局
 * @param checkShow 是否显示,检测该布局是否需要显示loadStatus 变化时会先检测是否需要显示
 * @param converter 布局配置,布局更新块，可以使用itemBinding 来更新内容，loadStatus 变化时会调用更新 如下示例显示了各种加载状态，错误点击重试功能。
 */
fun <V : ViewBinding> LoadMoreAdapterModule<*, *>.createLoadMoreStatusAdapter(
    creator: LayoutCreator<V>,
    checkShow: (LoadMoreAdapterModule<*, *>) -> Boolean = { true },
    converter: LayoutConverterLoadMoreStatus<V> = { }
): SingleViewBindingAdapter<V> {
    val adapter =
        SingleViewBindingAdapter(creator) { converter(this@createLoadMoreStatusAdapter.loadStatus) }
    observeLoadMoreStatus {
        adapter.notifyIfVisible {
            adapter.isVisible = checkShow(this)
        }

    }
    return adapter
}

