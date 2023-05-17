package me.lwb.adapter.basic

internal class LazyListWrapper<T>(val listProvider: () -> MutableList<T>) : MutableList<T> {
    private val delegate get() = listProvider()
    override val size: Int get() = delegate.size

    override fun contains(element: T) = delegate.contains(element)

    override fun containsAll(elements: Collection<T>) = delegate.containsAll(elements)

    override fun get(index: Int): T = delegate.get(index)

    override fun indexOf(element: T) = delegate.indexOf(element)

    override fun isEmpty() = delegate.isEmpty()

    override fun iterator() = delegate.iterator()

    override fun lastIndexOf(element: T) = delegate.lastIndexOf(element)

    override fun add(element: T) = delegate.add(element)

    override fun add(index: Int, element: T) = delegate.add(index, element)

    override fun addAll(index: Int, elements: Collection<T>) = delegate.addAll(index, elements)

    override fun addAll(elements: Collection<T>) = delegate.addAll(elements)

    override fun clear() = delegate.clear()

    override fun listIterator() = delegate.listIterator()

    override fun listIterator(index: Int) = delegate.listIterator(index)

    override fun remove(element: T) = delegate.remove(element)

    override fun removeAll(elements: Collection<T>) = delegate.removeAll(elements)

    override fun removeAt(index: Int) = delegate.removeAt(index)

    override fun retainAll(elements: Collection<T>) = delegate.retainAll(elements)

    override fun set(index: Int, element: T) = delegate.set(index, element)

    override fun subList(fromIndex: Int, toIndex: Int) = delegate.subList(fromIndex, toIndex)
}
