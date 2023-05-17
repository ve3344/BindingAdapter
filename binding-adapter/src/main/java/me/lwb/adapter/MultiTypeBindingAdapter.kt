package me.lwb.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * 多布局Adapter
 */

open class MultiTypeBindingAdapter<I : Any, V : ViewBinding>(
    var itemViewMapperStore: ItemViewMapperStore<I, V>,
    list: List<I> = ArrayList(),
) : RecyclerView.Adapter<BindingViewHolder<V>>(),
    IVisibleAdapter, IBindingAdapter<V> {

    var data: MutableList<I> = if (list is MutableList) list else ArrayList(list)
        private set
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

    @SuppressLint("NotifyDataSetChanged")
    fun changeDataList(data: MutableList<I>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BindingViewHolder<V> = onCreateViewHolderDelegate(parent, viewType)


    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) =
        itemViewMapperStore.getItemViewType(position, data[position])

    override fun onBindViewHolder(holder: BindingViewHolder<V>, position: Int) {
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<V>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        onBindViewHolderDelegate(holder, position, payloads)
    }

    override var onBindViewHolderDelegate: (holder: BindingViewHolder<V>, position: Int, payloads: List<Any>) -> Unit =
        { holder, position, payloads ->
            itemViewMapperStore.bindViewHolder(holder, position, data[position], payloads)
        }

    override var onCreateViewHolderDelegate: (parent: ViewGroup, viewType: Int) -> BindingViewHolder<V> =
        { parent, viewType ->
            itemViewMapperStore.createViewHolder(this, parent, viewType)
        }

    /**
     * 数据内容的visible控制
     */
    override var isVisible: Boolean = true
        set(visible) {
            updateOnVisibleChange(field, visible, data.size)

            field = visible
        }


    override fun getItemCount() = if (isVisible) data.size else 0

}


