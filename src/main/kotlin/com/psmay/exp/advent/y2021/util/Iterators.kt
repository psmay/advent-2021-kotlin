@file:Suppress("unused")

package com.psmay.exp.advent.y2021.util

fun <T> Iterator<T>.listOfNext() = if (hasNext()) listOf(next()) else emptyList()
fun <T> Iterator<T>.nextOrNull() = if (hasNext()) next() else null
fun <T> Iterator<T>.nextOrElse(defaultValue: () -> T) = if (hasNext()) next() else defaultValue()

fun <T> PeekingIterator<T>.listOfPeek() = if (hasNext()) listOf(peek()) else emptyList()
fun <T> PeekingIterator<T>.peekOrNull() = if (hasNext()) peek() else null
fun <T> PeekingIterator<T>.peekOrElse(defaultValue: () -> T) = if (hasNext()) peek() else defaultValue()

fun <T, R> Iterator<T>.map(transform: (T) -> R): Iterator<R> {
    val source = this
    return object : Iterator<R> {
        override fun hasNext() = source.hasNext()
        override fun next() = transform(source.next())
    }
}

fun <T> Iterator<T>.filter(predicate: (T) -> Boolean): Iterator<T> = this.withPeekingAndFilter(predicate)

fun <T> Iterator<T>.takeThroughFirst(predicate: (T) -> Boolean): Iterator<T> {
    val source = this
        .map { it to predicate(it) }

    var ended = false

    return object : Iterator<T> {
        override fun hasNext(): Boolean {
            return (!ended) && source.hasNext()
        }

        override fun next(): T {
            if (hasNext()) {
                val (element, result) = source.next()
                if (result) ended = true
                return element
            } else {
                throw NoSuchElementException()
            }
        }
    }
}

fun <T> Iterator<T>.dropThroughFirst(predicate: (T) -> Boolean): Iterator<T> {
    val source = this

    var initialSkipComplete = false

    return object : Iterator<T> {
        fun initialSkip() {
            if (!initialSkipComplete) {
                var found = false
                while (!found && source.hasNext()) {
                    found = predicate(source.next())
                }
                initialSkipComplete = true
            }
        }

        override fun hasNext(): Boolean {
            initialSkip()
            return source.hasNext()
        }

        override fun next(): T {
            initialSkip()
            return source.next()
        }
    }
}