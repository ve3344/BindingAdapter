package me.lwb.bindingadapter

import androidx.viewbinding.ViewBinding

/**
 * Created by luowenbin on 2021/10/14.
 */
object MultiTypeAdapterUtils {
    /**
     * 简化创建多布局配置的Builder
     */
    class MultiTypeAdapterConfigBuilder<I : Any> internal constructor(){
        private var layoutTypeExtractor: LayoutTypeExtractor<I>? = null
        private val layouts: MutableList<MultiTypeAdapterConfig.LayoutItem<out I, out ViewBinding>> =
            mutableListOf()

        /**
         * 根据item获取布局信息
         */
        fun extractItemViewType(block: LayoutTypeExtractor<I>) {
            layoutTypeExtractor = block
        }

        /**
         * 配置1个类型布局
         * @param creator 布局创建器
         * @param converter 布局更新器
         */
        fun <ItemType : I, V : ViewBinding> layout(
            creator: LayoutCreator<V>,
            converter: LayoutConverter<ItemType, V> ={_,_->}
        ) {
            layouts.add(MultiTypeAdapterConfig.LayoutItem(creator, converter))
        }

        /**
         * 完成创建，生成配置信息
         */
        internal fun create(): MultiTypeAdapterConfig<I> {
            val layoutTypeExtractor = layoutTypeExtractor
            requireNotNull(layoutTypeExtractor) { "You must call extractItemViewType method" }
            return MultiTypeAdapterConfig(layoutTypeExtractor, layouts)
        }
    }

    /**
     * 创建1个多布局配置
     */
    fun <I : Any> createConfig(build: MultiTypeAdapterConfigBuilder<I>.() -> Unit): MultiTypeAdapterConfig<I> {
        return MultiTypeAdapterConfigBuilder<I>().run {
            build()
            create()
        }
    }
}