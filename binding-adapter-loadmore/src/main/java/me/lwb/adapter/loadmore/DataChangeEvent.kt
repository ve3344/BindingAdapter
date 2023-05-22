package me.lwb.adapter.loadmore

/**
 * 数据流的单位，就是1次加载的数据集
 */
sealed class DataChangeEvent<T> {
    class Append<T>(val data: Collection<T>) : DataChangeEvent<T>() {
        override fun toString(): String = "Append(size=${data.size})"
    }

    class Replace<T>(val data: Collection<T>) : DataChangeEvent<T>() {
        override fun toString() = "Replace(size=${data.size})"
    }

}