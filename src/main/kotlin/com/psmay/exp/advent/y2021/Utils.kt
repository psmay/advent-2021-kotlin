package com.psmay.exp.advent.y2021

import java.util.*

/**
 * Performs a fold operation one element at a time.
 */
fun <T, R> Sequence<T>.foldIncrementally(initial: R, operation: (R, T) -> R): Sequence<R> {
    val source = this
    return sequence {
        var accumulator = initial
        for (element in source) {
            accumulator = operation(accumulator, element)
            yield(accumulator)
        }
    }
}

/**
 * Performs a fold operation one element at a time.
 */
fun <T, R> Iterable<T>.foldIncrementally(initial: R, operation: (R, T) -> R): Iterable<R> =
    this.asSequence().foldIncrementally(initial, operation).asIterable()

fun <T> Sequence<T>.pairwise(initial: T): Sequence<Pair<T, T>> {
    val source = this
    return sequence {
        var previous = initial
        for (element in source) {
            this.yield(previous to element)
            previous = element
        }
    }
}

fun <T> Iterable<T>.pairwise(initial: T) =
    this.asSequence().pairwise(initial).asIterable()

// This actually does the same thing as zipWithNext, which I didn't find out about
// until later.
@Deprecated("Use zipWithNext() instead.", replaceWith = ReplaceWith("zipWithNext()"))
fun <T> Sequence<T>.pairwise(): Sequence<Pair<T, T>> {
    // This is used so that, if T is nullable, it is still possible to assign separate meanings to null and unset.
    data class Holder(var item: T)

    val source = this
    return sequence {
        var previous: Holder? = null
        for (element in source) {
            if (previous == null) {
                previous = Holder(element)
            } else {
                this.yield(previous.item to element)
                previous.item = element
            }
        }
    }
}

@Deprecated("Use zipWithNext() instead.", replaceWith = ReplaceWith("zipWithNext()"))
fun <T> Iterable<T>.pairwise() =
    @Suppress("DEPRECATION")
    this.asSequence().pairwise().asIterable()

/**
 * Produces a sequence of full windows of the specified size from this sequence.
 * If this sequence is shorter than the specified size, the result is an empty sequence.
 */
fun <T> Sequence<T>.windowed(size: Int): Sequence<List<T>> {
    if (size < 1) throw IllegalArgumentException("Window size cannot be less than 1.")

    val buffer: Queue<T> = LinkedList()

    val source = this
    return sequence {
        var space = size

        for (element in source) {
            if (space > 0) {
                --space
            } else {
                buffer.remove()
            }

            buffer.add(element)

            if (space == 0) {
                yield(buffer.toList())
            }
        }
    }
}

/**
 * Produces an iterable of full windows of the specified size from this iterable.
 * If this iterable is shorter than the specified size, the result is an empty iterable.
 */
fun <T> Iterable<T>.windowed(size: Int) =
    this.asSequence().windowed(size).asIterable()

/**
 * Produces a sequence of windows of the specified size from this sequence.
 * The first and last (size - 1) elements, which may overlap, have their unspecified
 * elements filled with the given placeholder value. If the source is empty, the result
 * is empty.
 */
fun <T> Sequence<T>.roughWindowed(size: Int, placeholder: T): Sequence<List<T>> {
    if (size < 1) throw IllegalArgumentException("Window size cannot be less than 1.")
    val buffer: Queue<T> = LinkedList(List(size) { placeholder })

    val source = this
    return sequence {
        var wasEmpty = true
        for (element in source) {
            wasEmpty = false
            buffer.remove()
            buffer.add(element)
            yield(buffer.toList())
        }

        if (!wasEmpty) {
            var leftToFlush = size - 1
            while (leftToFlush > 0) {
                --leftToFlush
                buffer.remove()
                buffer.add(placeholder)
                yield(buffer.toList())
            }
        }
    }
}

/**
 * Produces an iterable of windows of the specified size from this iterable.
 * The first and last (size - 1) elements, which may overlap, have their unspecified
 * elements filled with the given placeholder value. If the source is empty, the result
 * is empty.
 */
fun <T> Iterable<T>.roughWindowed(size: Int, placeholder: T) =
    this.asSequence().roughWindowed(size, placeholder).asIterable()

/**
 * Produces the transpose of the specified list of sequences as a sequence of lists.
 *
 * If the sources are not the same length, the transpose is truncated to the shortest source.
 */
fun <T> List<Sequence<T>>.transpose(): Sequence<List<T>> {
    val iterators = this.map { it.iterator() }

    return sequence {
        while (iterators.all { it.hasNext() }) {
            val next = iterators.map { it.next() }
            this.yield(next)
        }
    }
}

/**
 * Produces the transpose of the specified list of iterables as an iterable of lists.
 *
 * If the sources are not the same length, the transpose is truncated to the shortest source.
 */
fun <T> List<Iterable<T>>.transpose(): Iterable<List<T>> =
    this.map { it.asSequence() }.transpose().asIterable()

/**
 * Produces the transpose of the specified list of lists as a list of lists.
 *
 * If the sources are not the same length, the transpose is truncated to the shortest source.
 */
fun <T> List<List<T>>.transpose(): List<List<T>> =
    this.map { it.asIterable() }.transpose().toList()
