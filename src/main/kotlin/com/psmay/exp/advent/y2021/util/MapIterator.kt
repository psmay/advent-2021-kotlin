package com.psmay.exp.advent.y2021.util

fun <T, R> Sequence<T>.mapIterator(transform: (Iterator<T>) -> Iterator<R>): Sequence<R> {
    val source = this
    return object : Sequence<R> {
        override fun iterator(): Iterator<R> = transform(source.iterator())
    }
}