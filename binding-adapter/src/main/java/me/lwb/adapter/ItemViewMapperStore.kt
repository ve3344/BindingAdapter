package me.lwb.adapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Created by ve3344@qq.com.
 */

/**
 * 存储所有布局类型的配置信息
 */
interface ItemViewMapperStore<I : Any, V : ViewBinding> {
    fun createViewHolder(
        adapter: RecyclerView.Adapter<*>,
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<V>

    fun getItemViewType(position: Int, item: I): Int

    fun bindViewHolder(holder: BindingViewHolder<V>, position: Int, item: I, payloads: List<Any>)


    /**
     * Adapter 中1个布局类型的配置信息
     * @param creator 布局创建器
     * @param converter 布局更新器
     */
    class ItemViewMapper<I : Any, V : ViewBinding>(
        private val creator: LayoutCreator<V>,
        private val converter: LayoutConverter<I, V>
    ) {
        /**
         * 布局创建
         */
        fun createViewHolder(
            adapter: RecyclerView.Adapter<*>,
            parent: ViewGroup
        ): BindingViewHolder<V> {
            val binding = creator(LayoutInflater.from(parent.context), parent, false)
            return BindingViewHolder(adapter, binding)
        }

        /**
         * 布局更新
         */
        fun bindViewHolder(
            holder: BindingViewHolder<V>,
            position: Int,
            item: I,
            payloads: List<Any>
        ) {
            holder.converter(position, item, payloads)
        }

    }
}



typealias ItemViewMapperProvider<I> = (viewType: Int) -> ItemViewMapperStore.ItemViewMapper<out I, out ViewBinding>

/**
 * 存储所有布局类型的配置信息
 * @param itemViewTypeExtractor 根据item获取布局类型的方法
 * @param getItemViewMapper 根据type获取布局LayoutMapper
 */
class MultiTypeItemViewMapperStore<I : Any>(
    private val itemViewTypeExtractor: LayoutTypeExtractor<I>,
    private val getItemViewMapper: ItemViewMapperProvider<I>
) : ItemViewMapperStore<I, ViewBinding> {


    /**
     * 根据item获取type
     */
    override fun getItemViewType(position: Int, item: I) = itemViewTypeExtractor(position, item)

    /**
     * 根据type创建布局
     */
    @Suppress("UNCHECKED_CAST")
    override fun createViewHolder(
        adapter: RecyclerView.Adapter<*>,
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ViewBinding> {
        return getItemViewMapper(viewType).createViewHolder(
            adapter,
            parent
        ) as BindingViewHolder<ViewBinding>
    }

    /**
     * 根据type更新布局
     */
    override fun bindViewHolder(
        holder: BindingViewHolder<ViewBinding>,
        position: Int,
        item: I,
        payloads: List<Any>
    ) {
        val viewType = getItemViewType(position, item)
        getItemViewMapper(viewType).bindViewHolderCast(holder, position, item, payloads)
    }


    @Suppress("UNCHECKED_CAST")
    fun <I : Any, V : ViewBinding> ItemViewMapperStore.ItemViewMapper<I, V>.bindViewHolderCast(
        holder: BindingViewHolder<out ViewBinding>,
        position: Int,
        item: Any,
        payloads: List<Any>
    ) {
        holder as BindingViewHolder<V>
        item as I
        this.bindViewHolder(holder, position, item, payloads)
    }

    class SparseArrayMapperProvider<I : Any>(private val layoutData: SparseArray<ItemViewMapperStore.ItemViewMapper<out I, out ViewBinding>>) :
        ItemViewMapperProvider<I> {
        override fun invoke(viewType: Int) =
            requireNotNull(layoutData.get(viewType)) { "Unknown view type $viewType" }
    }

    class ArrayMapperProvider<I : Any>(private val layoutData: List<ItemViewMapperStore.ItemViewMapper<out I, out ViewBinding>>) :
        ItemViewMapperProvider<I> {
        override fun invoke(viewType: Int) =
            requireNotNull(layoutData.getOrNull(viewType)) { "View type out of range,viewType=$viewType,max view type is ${layoutData.lastIndex}" }
    }

    class SingleMapperProvider<I : Any>(private val itemViewMapper: ItemViewMapperStore.ItemViewMapper<I, out ViewBinding>) :
        ItemViewMapperProvider<I> {
        override fun invoke(viewType: Int) = itemViewMapper
    }

}


/**
 * 单个布局
 */
open class SingleTypeItemViewMapperStore<I : Any, V : ViewBinding>(
    private val itemViewMapper: ItemViewMapperStore.ItemViewMapper<I, V>
) : ItemViewMapperStore<I, V> {
    override fun createViewHolder(
        adapter: RecyclerView.Adapter<*>,
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<V> = itemViewMapper.createViewHolder(adapter, parent)

    override fun getItemViewType(position: Int, item: I) = 0


    override fun bindViewHolder(
        holder: BindingViewHolder<V>,
        position: Int,
        item: I,
        payloads: List<Any>
    ) = itemViewMapper.bindViewHolder(holder, position, item, payloads)
}