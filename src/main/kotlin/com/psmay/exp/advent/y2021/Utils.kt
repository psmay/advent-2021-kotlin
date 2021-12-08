@file:Suppress("unused")

package com.psmay.exp.advent.y2021

import java.util.*

// Something option-like and sequence-like that supports *OrNull() implementations and regular ones at the same time
private data class Found<T>(val value: T)

private fun <T> Found<T>?.single(): T = (this ?: throw NoSuchElementException()).value
private fun <T> Found<T>?.single(exceptionMessage: String): T =
    (this ?: throw NoSuchElementException(exceptionMessage)).value

private fun <T> Found<T>?.listSingle() = single("List is empty.")
private fun <T> Found<T>?.iterableSingle() = single("Collection is empty.")
private fun <T> Found<T>?.sequenceSingle() = single("Sequence is empty.")
private fun <T> Found<T>?.singleOrNull(): T? = this?.value
private fun <T> Found<T>?.toList(): List<T> = if (this == null) emptyList() else listOf(value)
private fun <T> Found<T>?.asIterable() = toList().asIterable()
private fun <T> Found<T>?.asSequence() = toList().asSequence()

/**
 * Performs a fold operation one element at a time.
 */
@Deprecated("Use runningFold(...).drop(1) instead.",
    replaceWith = ReplaceWith("runningFold(initial, operation).drop(1)"))
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

fun <T> Sequence<T>.pairwise(initial: T): Sequence<Pair<T, T>> {
    val source = this
    return sequence {
        var previous = initial
        for (element in source) {
            yield(previous to element)
            previous = element
        }
    }
}

fun <T> Iterable<T>.pairwise(initial: T) = asSequence().pairwise(initial).toList()

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
                yield(previous.item to element)
                previous.item = element
            }
        }
    }
}

@Deprecated("Use zipWithNext() instead.", replaceWith = ReplaceWith("zipWithNext()"))
fun <T> Iterable<T>.pairwise() =
    @Suppress("DEPRECATION")
    asSequence().pairwise().toList()

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
    asSequence().`makeshift windowed`(size).toList()

/**
 * Produces the transpose of the specified list of sequences as a sequence of lists.
 *
 * If the sources are not the same length, the transpose is truncated to the shortest source.
 */
fun <T> List<Sequence<T>>.transpose(): Sequence<List<T>> {
    val iterators = map { it.iterator() }

    return sequence {
        while (iterators.all { it.hasNext() }) {
            val next = iterators.map { it.next() }
            yield(next)
        }
    }
}

/**
 * Produces the transpose of the specified iterable of iterables as a list of lists.
 *
 * If the sources are not the same length, the transpose is truncated to the shortest source.
 */
fun <T> Iterable<Iterable<T>>.transpose(): List<List<T>> = map { it.asSequence() }.transpose().toList()

// This implementation computes both extremes in a single pass.
private fun <T : Comparable<T>> Sequence<T>.minAndMaxAsFound() =
    mapFirstThenFoldAsFound({ it to it }) { (min, max), x ->
        (if (x < min) x else min) to (if (x > max) x else max)
    }

/**
 * Finds the min and max of a sequence, or null if the sequence contains no elements.
 */
fun <T : Comparable<T>> Sequence<T>.minAndMaxOrNull() = minAndMaxAsFound().singleOrNull()

/**
 * Finds the min and max of a sequence.
 */
fun <T : Comparable<T>> Sequence<T>.minAndMax() = minAndMaxAsFound().sequenceSingle()

/**
 * Finds the min and max of a collection, or null if the sequence contains no elements.
 */
fun <T : Comparable<T>> Iterable<T>.minAndMaxOrNull() = asSequence().minAndMaxAsFound().singleOrNull()

/**
 * Finds the min and max of a collection.
 */
fun <T : Comparable<T>> Iterable<T>.minAndMax() = asSequence().minAndMaxAsFound().sequenceSingle()

/**
 * Finds the nth number in the triangle progression 0, 0+1, 0+1+2, 0+1+2+3, ...
 */
// Could be done with a tailrec, but I think this gets the point across in fewer symbols.
fun triangle(n: Int): Int {
    require(n >= 0) { "Cannot compute for negative values." }
    return (0..n).fold(0) { a, i -> a + i }
}

// Here's one I thought of that didn't have an equivalent in the library already.
fun <T, R> Sequence<T>.mapFirstThenRunningFold(
    initialOperation: (T) -> R,
    operation: (acc: R, T) -> R,
): Sequence<R> {
    val iterator = iterator()
    return sequence {
        if (iterator.hasNext()) {
            var accumulator = initialOperation(iterator.next())
            yield(accumulator)
            while (iterator.hasNext()) {
                accumulator = operation(accumulator, iterator.next())
                yield(accumulator)
            }
        }
    }
}

private fun <T, R> Sequence<T>.mapFirstThenFoldAsFound(
    initialOperation: (T) -> R,
    operation: (acc: R, T) -> R,
): Found<R>? {
    val iterator = iterator()
    if (iterator.hasNext()) {
        var accumulator = initialOperation(iterator.next())
        while (iterator.hasNext()) {
            accumulator = operation(accumulator, iterator.next())
        }
        return Found(accumulator)
    }
    return null
}

/**
 * Applies a function to convert the first element of a sequence to an accumulator, then performs a fold on the
 * remaining elements.
 */
fun <T, R> Sequence<T>.mapFirstThenFold(initialOperation: (T) -> R, operation: (acc: R, T) -> R) =
    mapFirstThenFoldAsFound(initialOperation, operation).sequenceSingle()

/**
 * Applies a function to convert the first element of a sequence to an accumulator, then performs a fold on the
 * remaining elements; returns null if the sequence is empty.
 */
fun <T, R> Sequence<T>.mapFirstThenFoldOrNull(initialOperation: (T) -> R, operation: (acc: R, T) -> R) =
    mapFirstThenFoldAsFound(initialOperation, operation).singleOrNull()

/**
 * Applies a function to convert the first element of a collection to an accumulator, then performs a fold on the
 * remaining elements.
 */
fun <T, R> Iterable<T>.mapFirstThenFold(initialOperation: (T) -> R, operation: (acc: R, T) -> R) =
    asSequence().mapFirstThenFoldAsFound(initialOperation, operation).iterableSingle()

/**
 * Applies a function to convert the first element of a collection to an accumulator, then performs a fold on the
 * remaining elements; returns null if the collection is empty.
 */
fun <T, R> Iterable<T>.mapFirstThenFoldOrNull(initialOperation: (T) -> R, operation: (acc: R, T) -> R) =
    asSequence().mapFirstThenFoldAsFound(initialOperation, operation).singleOrNull()
