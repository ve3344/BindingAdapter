package me.lwb.adapter.ext

import android.util.SparseArray
import androidx.viewbinding.ViewBinding
import me.lwb.adapter.*
import me.lwb.adapter.LayoutConverter
import me.lwb.adapter.LayoutConverter2
import me.lwb.adapter.LayoutCreator
import me.lwb.adapter.LayoutTypeExtractor

/**
 * Created by ve3344@qq.com.
 */


/**
 * 简化创建多布局配置的Builder
 */
class MultiTypeAdapterIndexConfigBuilder<I : Any> internal constructor() {
    private var layoutTypeExtractor: LayoutTypeExtractor<I>? = null
    private val layouts: MutableList<ItemViewMapperStore.ItemViewMapper<out I, out ViewBinding>> =
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
        converter: LayoutConverter<ItemType, V> = { _, _, _ -> }
    ): Int {
        val id = layouts.size
        layouts.add(ItemViewMapperStore.ItemViewMapper(creator, converter))
        return id
    }

    /**
     * 配置1个类型布局
     * @param creator 布局创建器
     * @param converter 布局更新器
     */
    fun <ItemType : I, V : ViewBinding> layout(
        creator: LayoutCreator<V>,
        converter: LayoutConverter2<ItemType, V> = { _, _ -> }
    ): Int {
        val id = layouts.size
        layouts.add(ItemViewMapperStore.ItemViewMapper(creator, converter.asConverter()))
        return id
    }

    /**
     * 完成创建，生成配置信息
     */
    internal fun create(): ItemViewMapperStore<I, ViewBinding> {
        val layoutTypeExtractor = layoutTypeExtractor
        requireNotNull(layoutTypeExtractor) { "You must call extractItemViewType method" }
        return MultiTypeItemViewMapperStore(
            layoutTypeExtractor,
            MultiTypeItemViewMapperStore.ArrayMapperProvider(layouts)
        )
    }
}

/**
 * 创建1个多布局配置
 */
fun <I : Any> createMultiTypeConfigByIndex(config: MultiTypeAdapterIndexConfigBuilder<I>.() -> Unit): ItemViewMapperStore<I, ViewBinding> {
    return MultiTypeAdapterIndexConfigBuilder<I>().run {
        config()
        create()
    }
}

//------------------------------------------



/**
 * 简化创建多布局配置的Builder
 */
//class -> type
//class
class MultiTypeAdapterTypeConfigBuilder<I : Any> internal constructor() {
    private val layouts: MutableList<ItemViewMapperStore.ItemViewMapper<out I, out ViewBinding>> =
        mutableListOf()
    private val layoutsTypeIndexMap: MutableMap<Class<*>, Int> = HashMap()

    /**
     * 配置1个类型布局
     * @param creator 布局创建器
     * @param converter 布局更新器
     */
    inline fun <reified ItemType : I, V : ViewBinding> layout(
        noinline creator: LayoutCreator<V>,
        noinline converter: LayoutConverter<ItemType, V> = { _, _, _ -> }
    ) = layout(ItemType::class.java, creator, converter)

    /**
     * 配置1个类型布局
     * @param creator 布局创建器
     * @param converter 布局更新器
     */
    fun <ItemType : I, V : ViewBinding> layout(
        clazz: Class<ItemType>,
        creator: LayoutCreator<V>,
        converter: LayoutConverter<ItemType, V> = { _, _, _ -> }
    ) {
        layoutsTypeIndexMap[clazz] = layouts.size;
        layouts.add(ItemViewMapperStore.ItemViewMapper(creator, converter))
    }

    /**
     * 配置1个类型布局
     * @param creator 布局创建器
     * @param converter 布局更新器
     */
    inline fun <reified ItemType : I, V : ViewBinding> layout(
        noinline creator: LayoutCreator<V>,
        noinline converter: LayoutConverter2<ItemType, V> = { _, _ -> }
    ) = layout(ItemType::class.java, creator, converter)

    /**
     * 配置1个类型布局
     * @param creator 布局创建器
     * @param converter 布局更新器
     */
    fun <ItemType : I, V : ViewBinding> layout(
        clazz: Class<ItemType>,
        creator: LayoutCreator<V>,
        converter: LayoutConverter2<ItemType, V> = { _, _ -> }
    ) {
        layoutsTypeIndexMap[clazz] = layouts.size;
        layouts.add(ItemViewMapperStore.ItemViewMapper(creator, converter.asConverter()))
    }


    /**
     * 完成创建，生成配置信息
     */
    internal fun create(): ItemViewMapperStore<I, ViewBinding> {

        val layoutTypeExtractor = layoutTypeExtractor()
        return MultiTypeItemViewMapperStore(
            layoutTypeExtractor,
            MultiTypeItemViewMapperStore.ArrayMapperProvider(layouts)
        )
    }

    private fun layoutTypeExtractor(): LayoutTypeExtractor<I> {
        return { position, item ->
            requireNotNull(layoutsTypeIndexMap[item.javaClass]) { "Can not find type at $position with item $item" }
        }
    }
}

/**
 * 创建1个多布局配置
 */
fun <I : Any> createMultiTypeConfigByType(config: MultiTypeAdapterTypeConfigBuilder<I>.() -> Unit): ItemViewMapperStore<I, ViewBinding> {
    return MultiTypeAdapterTypeConfigBuilder<I>().run {
        config()
        create()
    }
}

//--------------------------------------------
/**
 * 简化创建多布局配置的Builder
 */
class MultiTypeAdapterMapConfigBuilder<I : Any> internal constructor() {
    private var layoutTypeExtractor: LayoutTypeExtractor<I>? = null

    private val layouts: SparseArray<ItemViewMapperStore.ItemViewMapper<out I, out ViewBinding>> =
        SparseArray()

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
        viewType: Int,
        creator: LayoutCreator<V>,
        converter: LayoutConverter<ItemType, V> = { _, _, _ -> }
    ) {
        layouts.put(viewType, ItemViewMapperStore.ItemViewMapper(creator, converter))
    }

    /**
     * 配置1个类型布局
     * @param creator 布局创建器
     * @param converter 布局更新器
     */
    fun <ItemType : I, V : ViewBinding> layout(
        viewType: Int,
        creator: LayoutCreator<V>,
        converter: LayoutConverter2<ItemType, V> = { _, _ -> }
    ) {
        layouts.put(
            viewType,
            ItemViewMapperStore.ItemViewMapper(creator, converter.asConverter())
        )
    }

    /**
     * 完成创建，生成配置信息
     */
    internal fun create(): ItemViewMapperStore<I, ViewBinding> {
        val layoutTypeExtractor = layoutTypeExtractor
        requireNotNull(layoutTypeExtractor) { "You must call extractItemViewType method" }
        return MultiTypeItemViewMapperStore(
            layoutTypeExtractor,
            MultiTypeItemViewMapperStore.SparseArrayMapperProvider(layouts)
        )
    }
}

/**
 * 创建1个多布局配置
 */
fun <I : Any> createMultiTypeConfigByMap(config: MultiTypeAdapterMapConfigBuilder<I>.() -> Unit): ItemViewMapperStore<I, ViewBinding> {
    return MultiTypeAdapterMapConfigBuilder<I>().run {
        config()
        create()
    }
}