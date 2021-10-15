package me.lwb.bindinadapter.paging

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import me.lwb.bindingadapter.BindingViewHolder
import me.lwb.bindingadapter.MultiTypeAdapterConfig

/**
 * 多布局分页Adapter
 */
open class MultiTypePagingBindingAdapter<I : Any>(
    private val multiTypeAdapterConfig: MultiTypeAdapterConfig<I>,
    diffCallback: DiffUtil.ItemCallback<I> = DiffCallbackUtils.create(),
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    workerDispatcher: CoroutineDispatcher = Dispatchers.Default
) :
    PagingDataAdapter<I, BindingViewHolder<ViewBinding>>(
        diffCallback,
        mainDispatcher,
        workerDispatcher
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ViewBinding> {
        return multiTypeAdapterConfig.createViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        val item = requireNotNull(getItem(position)) { "Invalid position" }
        return multiTypeAdapterConfig.getItemViewType(position, item)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ViewBinding>, position: Int) {
        val item = getItem(position) ?: return
        multiTypeAdapterConfig.bindViewHolder(holder, position, item)
    }

}