@file:Suppress("unused")

package com.psmay.exp.advent.y2021.util

interface PeekingIterator<out T> : Iterator<T> {
    fun peek(): T
}

fun <T> Iterator<T>.withPeeking(): PeekingIterator<T> {
    return if (this is PeekingIterator<T>) this
    else OneElementLookaheadIterator(this)
}

fun <T> Iterator<T>.withPeekingAndFilter(predicate: (T) -> Boolean): PeekingIterator<T> =
    OneElementLookaheadWithFilterIterator(this, predicate)

internal open class OneElementLookaheadIterator<T>(private val source: Iterator<T>) : PeekingIterator<T> {
    private val buffer = emptyQueue<T>()

    override fun hasNext(): Boolean = buffer.isNotEmpty() || source.hasNext()

    open fun allowElement(element: T) = true

    private fun preload(): Boolean {
        while (buffer.isEmpty() && source.hasNext()) {
            val element = source.next()
            if (allowElement(element)) {
                buffer.addLast(element)
            }
        }
        return hasNext()
    }

    override fun next(): T {
        if (preload()) {
            return buffer.removeFirst()
        } else {
            throw NoSuchElementException()
        }
    }

    override fun peek(): T {
        if (preload()) {
            return buffer.first()
        } else {
            throw NoSuchElementException()
        }
    }
}

internal class OneElementLookaheadWithFilterIterator<T>(
    source: Iterator<T>,
    private val predicate: (T) -> Boolean,
) :
    OneElementLookaheadIterator<T>(source) {
    override fun allowElement(element: T) = predicate(element)
}


