@file:Suppress("unused")

package com.psmay.exp.advent.y2021.util

interface PeekingSequence<out T> : Sequence<T> {
    fun peekingIterator(): PeekingIterator<T>
}

fun <T> Sequence<T>.withPeeking(): PeekingSequence<T> {
    return if (this is PeekingSequence<T>) this
    else OneElementLookaheadSequence(this)
}

private class OneElementLookaheadSequence<T>(private val source: Sequence<T>) : PeekingSequence<T> {
    override fun iterator(): Iterator<T> = source.iterator()

    override fun peekingIterator(): PeekingIterator<T> = OneElementLookaheadIterator(source.iterator())
}