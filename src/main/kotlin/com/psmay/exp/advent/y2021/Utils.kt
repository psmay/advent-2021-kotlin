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

@Deprecated("Use builtin windowed() instead.", replaceWith = ReplaceWith("windowed()"))
@Suppress("FunctionName")
fun <T> Sequence<T>.`makeshift windowed`(size: Int): Sequence<List<T>> {
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

@Deprecated("Use builtin windowed() instead.", replaceWith = ReplaceWith("windowed()"))
@Suppress("FunctionName", "DEPRECATION")
fun <T> Iterable<T>.`makeshift windowed`(size: Int) =
    this.asSequence().`makeshift windowed`(size).asIterable()

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

/**
 * Finds the min and max of an iterable, or null if there are no elements.
 */
// Not more efficient than calling separately, but it makes the exception on empty less awkward.
fun <T : Comparable<T>> Iterable<T>.minAndMaxOrNull(): Pair<T, T>? {
    val min = this.minOrNull()
    val max = this.maxOrNull()
    return if (min == null || max == null) null else (min to max)
}

/**
 * Finds the nth number in the triangle progression 0, 0+1, 0+1+2, 0+1+2+3, ...
 */
// Could be done with a tailrec, but I think this gets the point across in fewer symbols.
fun triangle(n: Int): Int {
    require(n >= 0) { "Cannot compute for negative values." }
    return (0..n).fold(0) { a, i -> a + i }
}