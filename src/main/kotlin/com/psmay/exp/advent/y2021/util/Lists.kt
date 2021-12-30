package com.psmay.exp.advent.y2021.util

private fun <T> Collection<T>.ensureContains(indices: IntRange) = indices.ensureInBounds(this.indices)
private fun <T> Collection<T>.ensureContainsEmptyIndex(n: Int) = ensureContains(n until n)

/**
 * Removes the elements in the specified range.
 */
fun <T> MutableList<T>.discard(indices: IntRange) {
    ensureContains(indices)
    removeAndDiscard(indices)
}

/**
 * Removes and returns the elements in the specified range.
 */
fun <T> MutableList<T>.splice(indices: IntRange): List<T> {
    ensureContains(indices)
    return removeAndGet(indices)
}

/**
 * Replaces the elements in the specified range with the specified elements, returning a list of the removed elements.
 */
fun <T> MutableList<T>.splice(indices: IntRange, replacement: Collection<T>): List<T> {
    ensureContains(indices)
    val removed = removeAndGet(indices)
    addAll(indices.first, replacement)
    return removed
}

/**
 * Replaces the elements in the specified range with the specified elements, returning a list of the removed elements.
 */
fun <T> MutableList<T>.splice(indices: IntRange, replacement: Sequence<T>): List<T> {
    ensureContains(indices)
    val removed = removeAndGet(indices)
    addAll(indices.first, replacement)
    return removed
}

/**
 * Replaces the elements in the specified range with the specified elements, returning a list of the removed elements.
 */
fun <T> MutableList<T>.splice(indices: IntRange, replacement: Iterable<T>): List<T> {
    ensureContains(indices)
    val removed = removeAndGet(indices)
    addAll(indices.first, replacement)
    return removed
}

/**
 * Replaces the elements in the specified range with the specified elements, returning a list of the removed elements.
 */
fun <T> MutableList<T>.splice(indices: IntRange, replacement: Iterator<T>): List<T> {
    ensureContains(indices)
    val removed = removeAndGet(indices)
    addAll(indices.first, replacement)
    return removed
}

// Repeatedly calls built-in addAll with collections of elements to add, keeping track of the index to simulate the
// result of concatenating all the collections and adding them at once.
//
// Using the built-in addAll for collection chunks, and thus utilizing whatever optimizations addAll does internally,
// at least ostensibly more time-efficient (fewer reallocations in the mutable list) than adding elements one at a
// time and more space efficient (smaller intermediate buffer for iterator elements) than converting all of the
// elements of an iteration to a List and then inserting that.
private fun <T> MutableList<T>.addAllChunksUnchecked(index: Int, chunks: Iterator<Collection<T>>): Int {
    var addedCount = 0

    for (chunk in chunks) {
        val chunkSize = chunk.size
        if (chunkSize > 0) {
            addAll(index + addedCount, chunk)
            addedCount += chunkSize
        }
    }

    return addedCount
}

/**
 * Inserts all of the elements of each chunk in the specified iterator [chunks] into this list at the specified [index].
 *
 * @return The number of elements added as the result of the operation.
 */
fun <T> MutableList<T>.addAllChunks(index: Int, chunks: Iterator<Collection<T>>): Int {
    ensureContainsEmptyIndex(index)
    return addAllChunksUnchecked(index, chunks)
}

/**
 * Inserts all of the elements of each chunk in the specified iterable [chunks] into this list at the specified [index].
 *
 * @return The number of elements added as the result of the operation.
 */
fun <T> MutableList<T>.addAllChunks(index: Int, chunks: Iterable<Collection<T>>): Int {
    ensureContainsEmptyIndex(index)
    return addAllChunksUnchecked(index, chunks.iterator())
}

/**
 * Inserts all of the elements of each chunk in the specified sequence [chunks] into this list at the specified [index].
 *
 * @return The number of elements added as the result of the operation.
 */
fun <T> MutableList<T>.addAllChunks(index: Int, chunks: Sequence<Collection<T>>): Int {
    ensureContainsEmptyIndex(index)
    return addAllChunksUnchecked(index, chunks.iterator())
}

private const val CHUNK_SIZE = 256

/**
 * Inserts all of the elements of the specified sequence [elements] into this list at the specified [index].
 *
 * @return `true` if the list was changed as the result of the operation.
 */
fun <T> MutableList<T>.addAll(index: Int, elements: Sequence<T>): Boolean {
    ensureContainsEmptyIndex(index)
    val chunks = elements.chunked(CHUNK_SIZE)
    return addAllChunksUnchecked(index, chunks.iterator()) > 0
}

/**
 * Inserts all of the elements of the specified iterable [elements] into this list at the specified [index].
 *
 * @return `true` if the list was changed as the result of the operation.
 */
fun <T> MutableList<T>.addAll(index: Int, elements: Iterable<T>) = addAll(index, elements.asSequence())

/**
 * Inserts all of the elements of the specified iterator [elements] into this list at the specified [index].
 *
 * @return `true` if the list was changed as the result of the operation.
 */
fun <T> MutableList<T>.addAll(index: Int, elements: Iterator<T>) = addAll(index, elements.asSequence())

// Removes and returns the elements in the specified range (without checking the range first).
//
// Ideally there would be an optimized way to remove more than a single element at once, but Kotlin doesn't expose one.
private fun <T> MutableList<T>.removeAndGet(indices: IntRange): List<T> {
    return if (indices.isEmpty()) {
        listOf()
    } else {
        listIterator(indices.first).removeAndGet(indices.count())
    }
}

// Removes and discards the elements in the specified range (without checking the range first).
//
// Ideally there would be an optimized way to remove more than a single element at once, but Kotlin doesn't expose one.
private fun <T> MutableList<T>.removeAndDiscard(indices: IntRange) {
    if (indices.isEmpty()) {
        // do nothing
    } else {
        listIterator(indices.first).removeAndDiscard(indices.count())
    }
}

// Removes and returns the next n elements produced by this iterator.
private fun <T> MutableListIterator<T>.removeAndGet(n: Int): List<T> {
    val removed = mutableListOf<T>()
    repeat(n) {
        val element = next()
        remove()
        removed.add(element)
    }
    return removed.toList()
}

// Removes and discards the next n elements produced by this iterator.
private fun <T> MutableListIterator<T>.removeAndDiscard(n: Int) {
    repeat(n) {
        next()
        remove()
    }
}


