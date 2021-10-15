package me.lwb.bindingadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 * Created by ve3344@qq.com on 2021/10/12.
 */

/**
 * 存储所有布局类型的配置信息
 * @param layoutTypeExtractor 根据item获取布局类型的方法
 * @param layouts 局类型的配置信息列表
 */
class MultiTypeAdapterConfig<I : Any>(
    private val layoutTypeExtractor: LayoutTypeExtractor<I>,
    private val layouts: List<LayoutItem<out I, out ViewBinding>>
) {
    /**
     * 1个布局类型的配置信息
     * @param creator 布局创建器
     * @param converter 布局更新器
     */
    class LayoutItem<I : Any, V : ViewBinding>(
        private val creator: LayoutCreator<V>,
        private val converter: LayoutConverter<I, V>
    ) {
        /**
         * 布局创建
         */
        fun createViewHolder(parent: ViewGroup): BindingViewHolder<ViewBinding> {
            val binding = creator(LayoutInflater.from(parent.context), parent, false)
            return BindingViewHolder(binding)
        }

        /**
         * 布局更新
         */
        @Suppress("UNCHECKED_CAST")
        fun bindViewHolder(holder: BindingViewHolder<ViewBinding>, position: Int, item: Any) {
            holder as BindingViewHolder<V>
            item as I
            holder.converter(position, item)
        }
    }

    /**
     * 根据type获取布局
     */
    private fun getLayout(viewType: Int): LayoutItem<out I, out ViewBinding> {
        return requireNotNull(layouts.getOrNull(viewType)) { "View type out of range,viewType=$viewType,layoutSize=${layouts.size}" }
    }

    /**
     * 根据item获取type
     */
    fun getItemViewType(position: Int, item: I) = layoutTypeExtractor(position, item)

    /**
     * 根据type创建布局
     */
    fun createViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ViewBinding> {
        return getLayout(viewType).createViewHolder(parent)
    }

    /**
     * 根据type更新布局
     */
    fun bindViewHolder(holder: BindingViewHolder<ViewBinding>, position: Int, item: I) {
        val viewType = getItemViewType(position, item)
        getLayout(viewType).bindViewHolder(holder, position, item)
    }
}

