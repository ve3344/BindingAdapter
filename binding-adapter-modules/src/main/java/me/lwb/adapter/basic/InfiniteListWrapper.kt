package me.lwb.adapter.basic

/**
 * Created by ve3344@qq.com.
 */
open class InfiniteListWrapper<T>(val delegate: MutableList<T>) : MutableList<T> {

    companion object {
        const val INFINITE_NUMBER = 10000_0000
    }

    override val size: Int get() = (if (delegate.isEmpty()) 0 else INFINITE_NUMBER)


    fun getDataPosition(index: Int): Int = index.toDataPosition()

    private fun Int.toDataPosition(): Int {
        val itemCount = delegate.size
        if (itemCount == 0) {
            return 0
        }
        return this % itemCount
    }

    override fun contains(element: T): Boolean = delegate.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean = delegate.containsAll(elements)

    override fun get(index: Int): T = delegate[index.toDataPosition()]

    override fun indexOf(element: T): Int = delegate.indexOf(element)
    override fun lastIndexOf(element: T): Int = delegate.lastIndexOf(element)

    override fun isEmpty(): Boolean = delegate.isEmpty()


    override fun iterator() = delegate.iterator()

    override fun listIterator() = delegate.listIterator()

    override fun listIterator(index: Int) = delegate.listIterator(index.toDataPosition())

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        throw UnsupportedOperationException("Can not get sub list of an infinite list")
    }

    override fun add(element: T): Boolean = delegate.add(element)

    override fun add(index: Int, element: T) = delegate.add(index.toDataPosition(), element)

    override fun addAll(index: Int, elements: Collection<T>): Boolean =
        delegate.addAll(index.toDataPosition(), elements)

    override fun addAll(elements: Collection<T>): Boolean = delegate.addAll(elements)

    override fun clear() = delegate.clear()

    override fun remove(element: T): Boolean = delegate.remove(element)

    override fun removeAll(elements: Collection<T>): Boolean = delegate.removeAll(elements)

    override fun removeAt(index: Int): T = delegate.removeAt(index.toDataPosition())

    override fun retainAll(elements: Collection<T>): Boolean = delegate.retainAll(elements)

    override fun set(index: Int, element: T): T = delegate.set(index.toDataPosition(), element)
}

