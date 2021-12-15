package com.psmay.exp.advent.y2021.util

class MutableListBasedDeque<T> : Deque<T>, Stack<T> {
    private val store = mutableListOf<T>()
    override fun isEmpty() = store.isEmpty()
    override val size get() = store.size
    override fun contains(element: T) = store.contains(element)
    override fun containsAll(elements: Collection<T>) = store.containsAll(elements)
    override fun iterator() = store.iterator()

    override fun addFirst(element: T): Boolean {
        store.add(0, element)
        return true
    }

    override fun addFirstIfRoom(element: T) = addFirst(element)
    override fun addLast(element: T) = store.add(element)
    override fun addLastIfRoom(element: T) = addLast(element)
    override fun first() = store.first()
    override fun firstOrNull() = store.firstOrNull()
    override fun firstOrDefault(defaultValue: T) = firstOrElse { defaultValue }
    override fun firstOrElse(defaultValue: () -> T) = store.elementAtOrElse(0) { _ -> defaultValue() }
    override fun removeFirst() = store.removeFirst()
    override fun removeFirstOrNull() = store.removeFirstOrNull()
    override fun removeFirstOrDefault(defaultValue: T) = removeFirstOrElse { defaultValue }
    override fun removeFirstOrElse(defaultValue: () -> T) =
        if (store.isEmpty()) defaultValue() else store.removeAt(0)

    override fun last() = store.last()
    override fun lastOrNull() = store.lastOrNull()
    override fun lastOrDefault(defaultValue: T) = lastOrElse { defaultValue }
    override fun lastOrElse(defaultValue: () -> T) = store.elementAtOrElse(store.lastIndex) { _ -> defaultValue() }
    override fun removeLast() = store.removeLast()
    override fun removeLastOrNull() = store.removeLastOrNull()
    override fun removeLastOrDefault(defaultValue: T) = removeLastOrElse { defaultValue }
    override fun removeLastOrElse(defaultValue: () -> T) =
        if (store.isEmpty()) defaultValue() else store.removeAt(store.lastIndex)
}