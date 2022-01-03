package com.psmay.exp.advent.y2021.util

//
// For Iterator
//

/**
 * Returns the next element from this iterator as an option, or an empty option if this iterator has already reached
 * the end.
 */
fun <T> Iterator<T>.nextOpt() = if (hasNext()) optOf(next()) else emptyOpt()

/**
 * Returns the next element from this iterator that satisfies the specified predicate as an option, or an empty
 * option if this iterator produces no element that satisfies the predicate.
 *
 * Any element that is skipped because it does not satisfy the predicate is discarded.
 */
fun <T> Iterator<T>.nextOpt(predicate: (T) -> Boolean): Opt<T> {
    while (hasNext()) {
        val element = next()
        if (predicate(element)) {
            return optOf(element)
        }
    }
    return emptyOpt()
}

/**
 * Returns the next element from this iterator as an option and a flag reflecting whether this element is followed by
 * others, or an empty option and false if this iterator has already reached the end.
 */
fun <T> Iterator<T>.nextOptHasNext(): Pair<Opt<T>, Boolean> {
    return if (hasNext()) {
        val opt = optOf(next())
        opt to hasNext()
    } else {
        emptyOpt<T>() to false
    }
}

/**
 * Return the element from this iterator at the specified index as an option, or an empty option if there are fewer
 * than [index] elements remaining in this iterator. Any intervening elements are discarded from this iterator.
 */
fun <T> Iterator<T>.elementAtOpt(index: Int): Opt<T> {
    if (index < 0) {
        return emptyOpt()
    }
    repeat(index) {
        if (hasNext()) {
            next()
        } else {
            return emptyOpt()
        }
    }
    return nextOpt()
}

/**
 * Returns the last element of this iterator as an option, or an empty option if this iterator had already reached
 * the end. This iterator is exhausted in the process.
 */
fun <T> Iterator<T>.lastOpt(): Opt<T> {
    return if (!hasNext()) {
        emptyOpt()
    } else {
        var element = next()
        while (hasNext()) {
            element = next()
        }
        optOf(element)
    }
}

/**
 * Returns the last element of this iterator that satisfies the specified predicate as an option, or an empty option
 * if no matching element is found. This iterator is exhausted in the process.
 */
fun <T> Iterator<T>.lastOpt(predicate: (T) -> Boolean): Opt<T> {
    var knownOpt = nextOpt(predicate)
    var foundOpt = knownOpt

    while (foundOpt.isNotEmpty()) {
        knownOpt = foundOpt
        foundOpt = nextOpt(predicate)
    }

    return knownOpt
}

//
// For PeekingIterator
//

/**
 * Returns the peeked element from this iterator as an option, or an empty option if this iterator has already reached
 * the end.
 */
fun <T> PeekingIterator<T>.peekOpt() = if (hasNext()) optOf(peek()) else emptyOpt()

//
// For List
//

/**
 * Returns the first element from this list as an option, or an empty option if this list is empty.
 */
fun <T> List<T>.firstOpt() = if (this.isEmpty()) emptyOpt() else optOf(this[0])

/**
 * Returns the first element from this list as an option and a flag reflecting whether this element is followed by
 * others, or an empty option and false if this list is empty.
 */
fun <T> List<T>.firstOptHasNext(): Pair<Opt<T>, Boolean> {
    val opt = if (isEmpty()) emptyOpt() else optOf(this[0])
    val hasNext = size > 1
    return opt to hasNext
}

/**
 * Returns the one and only element from this list as an option, or an empty option if this list is empty or contains
 * multiple elements.
 */
fun <T> List<T>.singleOpt() = if (this.size == 1) optOf(this[0]) else emptyOpt()

/**
 * Returns the one and only element from this list as an option, or an empty option if this list is empty, or `null`
 * if this list contains multiple elements.
 */
fun <T> List<T>.singleOptOrNull() = when (this.size) {
    0 -> emptyOpt()
    1 -> optOf(this[0])
    else -> null
}

/**
 * Returns the last element from this list as an option, or an empty option if this list is empty.
 */
fun <T> List<T>.lastOpt() = if (this.isEmpty()) emptyOpt() else optOf(this[this.lastIndex])

/**
 * Returns the element at the specified index of this list as an option, or an empty option if the index is out of
 * range.
 */
fun <T> List<T>.elementAtOpt(index: Int): Opt<T> {
    @Suppress("ConvertTwoComparisonsToRangeCheck")
    return if (index >= 0 && index <= lastIndex) optOf(this[index]) else emptyOpt()
}

//
// For Sequence
//

/**
 * Returns the first element in this sequence as an option, or an empty option if this sequence is empty.
 */
fun <T> Sequence<T>.firstOpt() = this.iterator().nextOpt()

/**
 * Returns the first element in this sequence that satisfies the specified predicate as an option, or an empty
 * option if no matching element is found.
 */
fun <T> Sequence<T>.firstOpt(predicate: (T) -> Boolean) = this.filter(predicate).firstOpt()

/**
 * Returns the first element from this list as an option and a flag reflecting whether this element is followed by
 * others, or an empty option and false if this list is empty.
 */
fun <T> Sequence<T>.firstOptHasNext() = this.iterator().nextOptHasNext()

/**
 * Returns the first element of this sequence as an option and a flag reflecting whether this element is followed by
 * others, or an empty option and false if this iterator has already reached the end.
 */
fun <T> Sequence<T>.firstOptHasNext(predicate: (T) -> Boolean) = this.filter(predicate).firstOptHasNext()

/**
 * Returns the one and only element of this sequence as an option, or an empty option if this sequence is empty or
 * contains more than one element.
 */
fun <T> Sequence<T>.singleOpt(): Opt<T> {
    val (opt, hasNext) = this.iterator().nextOptHasNext()
    return if (hasNext) emptyOpt() else opt
}

/**
 * Returns the one and only element of this sequence that satisfies the specified predicate as an option, or an empty
 * option if this sequence contains no matching elements or more than one matching element.
 */
fun <T> Sequence<T>.singleOpt(predicate: (T) -> Boolean) = this.filter(predicate).singleOpt()

/**
 * Returns the one and only element of this sequence as an option, or an empty option if this sequence is empty, or
 * `null` if this sequence contains more than one element.
 */
fun <T> Sequence<T>.singleOptOrNull(): Opt<T>? {
    val (opt, hasNext) = this.iterator().nextOptHasNext()
    return if (hasNext) null else opt
}

/**
 * Returns the one and only element of this sequence that satisfies the specified predicate as an option, or an empty
 * option if this sequence contains no matching elements, or `null` if this sequence contains more than one matching
 * element.
 */
fun <T> Sequence<T>.singleOptOrNull(predicate: (T) -> Boolean) = this.filter(predicate).singleOptOrNull()

/**
 * Returns the last element in this sequence as an option, or an empty option if this sequence is empty.
 */
fun <T> Sequence<T>.lastOpt() = this.iterator().lastOpt()

/**
 * Returns the last element in this sequence that satisfies the specified predicate as an option, or an empty
 * option if no matching element is found.
 */
fun <T> Sequence<T>.lastOpt(predicate: (T) -> Boolean) = this.filter(predicate).lastOpt()

/**
 * Returns the element at the specified index of this sequence as an option, or an empty option if the index is out of
 * range.
 */
fun <T> Sequence<T>.elementAtOpt(index: Int) = this.iterator().elementAtOpt(index)

//
// For Iterable
//

/**
 * Returns the first element in this sequence as an option, or an empty option if this sequence is empty.
 */
fun <T> Iterable<T>.firstOpt() = this.asSequence().firstOpt()

/**
 * Returns the first element in this sequence that satisfies the specified predicate as an option, or an empty
 * option if no matching element is found.
 */
fun <T> Iterable<T>.firstOpt(predicate: (T) -> Boolean) = this.asSequence().firstOpt(predicate)

/**
 * Returns the first element of this sequence as an option, or an empty option if no matching element is found.
 */
fun <T> Iterable<T>.firstOptHasNext() = this.asSequence().firstOptHasNext()

/**
 * Returns the first element of this sequence as an option and a flag reflecting whether this element is followed by
 * others, or an empty option and false if this iterator has already reached the end.
 */
fun <T> Iterable<T>.firstOptHasNext(predicate: (T) -> Boolean) = this.asSequence().firstOptHasNext(predicate)

/**
 * Returns the one and only element of this iterable as an option, or an empty option if this iterable is empty or
 * contains more than one element.
 */
fun <T> Iterable<T>.singleOpt(): Opt<T> = this.asSequence().singleOpt()

/**
 * Returns the one and only element of this iterable that satisfies the specified predicate as an option, or an empty
 * option if this iterable contains no matching elements or more than one matching element.
 */
fun <T> Iterable<T>.singleOpt(predicate: (T) -> Boolean) = this.asSequence().singleOpt(predicate)

/**
 * Returns the one and only element of this iterable as an option, or an empty option if this iterable is empty, or
 * `null` if this iterable contains more than one element.
 */
fun <T> Iterable<T>.singleOptOrNull(): Opt<T>? = this.asSequence().singleOptOrNull()

/**
 * Returns the one and only element of this iterable that satisfies the specified predicate as an option, or an empty
 * option if this iterable contains no matching elements, or `null` if this iterable contains more than one matching
 * element.
 */
fun <T> Iterable<T>.singleOptOrNull(predicate: (T) -> Boolean) = this.asSequence().singleOptOrNull(predicate)

/**
 * Returns the last element in this sequence as an option, or an empty option if this sequence is empty.
 */
fun <T> Iterable<T>.lastOpt() = this.asSequence().lastOpt()

/**
 * Returns the last element in this sequence that satisfies the specified predicate as an option, or an empty
 * option if no matching element is found.
 */
fun <T> Iterable<T>.lastOpt(predicate: (T) -> Boolean) = this.asSequence().lastOpt(predicate)

/**
 * Returns the element at the specified index of this sequence as an option, or an empty option if the index is out of
 * range.
 */
fun <T> Iterable<T>.elementAtOpt(index: Int) = this.asSequence().elementAtOpt(index)

//
// For MutableList
//

/**
 * Removes the first element from this mutable list and returns that element as an option, or returns an empty option
 * if this list is empty.
 */
fun <T> MutableList<T>.removeFirstOpt(): Opt<T> = if (isEmpty()) emptyOpt() else optOf(removeFirst())

/**
 * Removes the last element from this mutable list and returns that element as an option, or returns an empty option
 * if this list is empty.
 */
fun <T> MutableList<T>.removeLastOpt(): Opt<T> = if (isEmpty()) emptyOpt() else optOf(removeLast())