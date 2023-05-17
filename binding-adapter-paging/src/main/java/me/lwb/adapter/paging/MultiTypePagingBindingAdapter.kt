package me.lwb.adapter.paging

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import me.lwb.adapter.paging.ext.DiffCallbackUtils
import me.lwb.adapter.BindingViewHolder
import me.lwb.adapter.IBindingAdapter
import me.lwb.adapter.ItemViewMapperStore

/**
 * 多布局分页Adapter
 */
open class MultiTypePagingBindingAdapter<I : Any, V : ViewBinding>(
    private val itemViewMapperStore: ItemViewMapperStore<I, V>,
    diffCallback: DiffUtil.ItemCallback<I> = DiffCallbackUtils.itemCallback(),
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    workerDispatcher: CoroutineDispatcher = Dispatchers.Default
) :
    PagingDataAdapter<I, BindingViewHolder<V>>(
        diffCallback,
        mainDispatcher,
        workerDispatcher
    ) , IBindingAdapter<V> {

    override var onAttachedToRecyclerViewListener: ((RecyclerView) -> Unit)? = null
    override var onDetachedToRecyclerViewListener: ((RecyclerView) -> Unit)? = null
    override var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        onAttachedToRecyclerViewListener?.invoke(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = null
        onDetachedToRecyclerViewListener?.invoke(recyclerView)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BindingViewHolder<V> =
        onCreateViewHolderDelegate(parent, viewType)

    override fun getItemViewType(position: Int) =
        itemViewMapperStore.getItemViewType(position,
            requireNotNull(getItem(position)) { "Invalid position" })

    override fun onBindViewHolder(holder: BindingViewHolder<V>, position: Int) {
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<V>,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        onBindViewHolderDelegate(holder, position, payloads)
    }

    override var onCreateViewHolderDelegate: (parent: ViewGroup, viewType: Int) -> BindingViewHolder<V> =
        { parent, viewType ->
            itemViewMapperStore.createViewHolder(this, parent, viewType)
        }

    override var onBindViewHolderDelegate: (holder: BindingViewHolder<V>, position: Int, p: List<Any>) -> Unit =
        { holder, position, p ->
            itemViewMapperStore.bindViewHolder(holder,
                position,
                requireNotNull(getItem(position)) { "Invalid position" },
                p)
        }
}