package com.psmay.exp.advent.y2021.util

@JvmInline
value class Opt<out T> private constructor(private val contentOrMarker: Any?) {
    private inline val isFull: Boolean get() = contentOrMarker !== EmptyMarker

    @Suppress("UNCHECKED_CAST")
    private inline val valueIfFull
        get() = contentOrMarker as T

    /**
     * Returns true if the option is empty.
     */
    fun isEmpty() = !isFull

    /**
     * Returns true if the option is not empty.
     */
    fun isNotEmpty() = isFull

    /**
     * Returns the value contained in this option.
     *
     * @throws NoSuchElementException if this option is empty.
     */
    fun get() = if (isFull) valueIfFull else throw NoSuchElementException()

    /**
     * Returns the value contained in this option, or `null` if there is no such value.
     */
    fun getOrNull() = if (isFull) valueIfFull else null

    /**
     * Produces a result value by running one callback if this option contains a value or another callback if this
     * option is empty.
     */
    fun <R> fold(onFull: (value: T) -> R, onEmpty: () -> R): R = if (isFull) onFull(valueIfFull) else onEmpty()

    /**
     * Returns a new option whose contents are the results of applying a transform to the contents of this option.
     */
    fun <R> map(transform: (T) -> R) = if (isFull) optOf(transform(valueIfFull)) else emptyOpt()

    /**
     * Returns a new option whose contents are the contents of this option that satisfy the specified predicate.
     */
    fun filter(predicate: (T) -> Boolean): Opt<T> = if (!isFull || predicate(valueIfFull)) this else emptyOpt()

    /**
     * Calls an action on the value contained in this option, if any.
     */
    fun forEach(action: (T) -> Unit) {
        if (isFull) action(valueIfFull)
    }

    /**
     * Returns a sequence reflecting the contents of this option.
     */
    fun asSequence(): OptSequence<T> = OptSequence(this)

    /**
     * Returns an iterable reflecting the contents of this option.
     */
    fun asIterable(): OptIterable<T> = OptIterable(this)

    /**
     * Returns a list reflecting the contents of this option.
     */
    fun asList(): OptList<T> = OptList(this)

    private fun contains(element: Any?) = if (isFull) element == valueIfFull else false

    private fun containsAll(elements: Collection<Any?>): Boolean {
        return if (isFull) {
            val value = valueIfFull
            elements.all { element -> element == value }
        } else {
            elements.isEmpty()
        }
    }

    @JvmInline
    value class OptSequence<out T> internal constructor(private val opt: Opt<T>) : Sequence<T> {
        /**
         * Returns the option that this sequence is based on.
         */
        fun asOpt() = opt
        override fun iterator(): Iterator<T> = getIterator(opt)
    }

    @JvmInline
    value class OptIterable<out T> internal constructor(private val opt: Opt<T>) : Iterable<T> {
        /**
         * Returns the option that this sequence is based on.
         */
        fun asOpt() = opt
        override fun iterator(): Iterator<T> = getIterator(opt)
    }

    @JvmInline
    value class OptList<out T> internal constructor(private val opt: Opt<T>) : List<T> {
        /**
         * Returns the option that this list is based on.
         */
        fun asOpt() = opt

        private fun onlyIndexOf(element: Any?) = if (contains(element)) 0 else -1

        override val size: Int get() = if (opt.isFull) 1 else 0

        override fun contains(element: @UnsafeVariance T): Boolean = opt.contains(element)

        override fun containsAll(elements: Collection<@UnsafeVariance T>): Boolean = opt.containsAll(elements)

        override fun get(index: Int): T =
            if (index == 0 && opt.isFull) opt.valueIfFull else throw IndexOutOfBoundsException()

        override fun indexOf(element: @UnsafeVariance T): Int = onlyIndexOf(element)

        override fun isEmpty(): Boolean = !opt.isFull

        override fun iterator(): Iterator<T> = getIterator(opt)

        override fun lastIndexOf(element: @UnsafeVariance T) = onlyIndexOf(element)

        override fun listIterator(): ListIterator<T> = getIterator(opt)

        override fun listIterator(index: Int): ListIterator<T> = getIterator(opt, index)

        override fun subList(fromIndex: Int, toIndex: Int): List<T> {
            val size = this.size

            return if (fromIndex < 0 || toIndex > size) {
                throw IndexOutOfBoundsException()
            } else if (fromIndex == 0 && toIndex == size) {
                this
            } else {
                OptList(emptyOpt())
            }
        }
    }

    companion object {
        private object EmptyMarker

        internal fun <T> getEmpty() = Opt<T>(EmptyMarker)
        internal fun <T> getFull(element: T) = Opt<T>(element)

        internal fun <T> getIterator(opt: Opt<T>) =
            if (opt.isFull) getFullIterator(opt.valueIfFull) else getEmptyIterator()

        internal fun <T> getIterator(opt: Opt<T>, index: Int) =
            if (opt.isFull) getFullIterator(opt.valueIfFull, index) else getEmptyIterator(index)

        private fun <T> getEmptyIterator(): ListIterator<T> = EmptyListIterator
        private fun <T> getEmptyIterator(index: Int): ListIterator<T> =
            if (index == 0) getEmptyIterator() else throw IndexOutOfBoundsException()

        private fun <T> getFullIterator(element: T): ListIterator<T> = SingleListIterator(element, true)
        private fun <T> getFullIterator(element: T, index: Int): ListIterator<T> {
            val atStart = when (index) {
                0 -> true
                1 -> false
                else -> throw IndexOutOfBoundsException()
            }
            return SingleListIterator(element, atStart)
        }

        private object EmptyListIterator : ListIterator<Nothing> {
            override fun hasNext() = false
            override fun hasPrevious() = false
            override fun nextIndex() = 0
            override fun previousIndex() = -1
            override fun next(): Nothing = throw NoSuchElementException()
            override fun previous(): Nothing = throw NoSuchElementException()
        }

        private class SingleListIterator<T>(val element: T, var atStart: Boolean) : ListIterator<T> {
            override fun hasNext(): Boolean = atStart
            override fun hasPrevious(): Boolean = !atStart

            override fun nextIndex() = if (atStart) 0 else 1
            override fun previousIndex() = if (!atStart) 0 else -1

            override fun next(): T {
                if (atStart) {
                    atStart = false
                    return element
                } else {
                    throw NoSuchElementException()
                }
            }

            override fun previous(): T {
                if (!atStart) {
                    atStart = true
                    return element
                } else {
                    throw NoSuchElementException()
                }
            }
        }
    }
}

/**
 * Returns an empty option.
 */
fun <T> emptyOpt(): Opt<T> = Opt.getEmpty()

/**
 * Returns an option containing the specified value.
 */
fun <T> optOf(element: T): Opt<T> = Opt.getFull(element)

/**
 * Returns an empty option.
 */
fun <T> optOf() = emptyOpt<T>()

/**
 * Returns the value contained in this option, or the result of the specified callback if there is no such value.
 */
fun <T> Opt<T>.getOrElse(defaultValue: () -> T) = fold({ it }, defaultValue)

/**
 * Gets an empty option with the same type as this option.
 */
@Suppress("unused")
fun <T> Opt<T>.cleared(): Opt<T> = emptyOpt()

/**
 * Gets a full option with the same type as this option, whose contents are set to the specified value.
 */
@Suppress("unused")
fun <T> Opt<T>.changed(value: T): Opt<T> = optOf(value)

/**
 * Gets this option, if it is a full option; otherwise, gets a new option with the specified contents.
 */
fun <T> Opt<T>.defaulted(defaultValue: () -> T) = if (this.isEmpty()) optOf(defaultValue()) else this

