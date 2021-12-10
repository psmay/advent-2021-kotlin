package com.psmay.exp.advent.y2021

object Day10 {

    interface Queue<T> : Collection<T> {
        fun addLast(element: T): Boolean
        fun addLastIfRoom(element: T): Boolean

        fun first(): T
        fun firstOrNull(): T?
        fun firstOrDefault(defaultValue: T): T
        fun firstOrElse(defaultValue: () -> T): T

        fun removeFirst(): T
        fun removeFirstOrNull(): T?
        fun removeFirstOrDefault(defaultValue: T): T
        fun removeFirstOrElse(defaultValue: () -> T): T
    }

    interface Deque<T> : Queue<T> {
        fun addFirst(element: T): Boolean
        fun addFirstIfRoom(element: T): Boolean

        fun last(): T
        fun lastOrNull(): T?
        fun lastOrDefault(defaultValue: T): T
        fun lastOrElse(defaultValue: () -> T): T

        fun removeLast(): T
        fun removeLastOrNull(): T?
        fun removeLastOrDefault(defaultValue: T): T
        fun removeLastOrElse(defaultValue: () -> T): T
    }

    interface Stack<T> : Collection<T> {
        fun addLast(element: T): Boolean
        fun addLastIfRoom(element: T): Boolean

        fun last(): T
        fun lastOrNull(): T?
        fun lastOrDefault(defaultValue: T): T
        fun lastOrElse(defaultValue: () -> T): T

        fun removeLast(): T
        fun removeLastOrNull(): T?
        fun removeLastOrDefault(defaultValue: T): T
        fun removeLastOrElse(defaultValue: () -> T): T
    }

    class MutableListBasedDeque<T> : Deque<T>, Stack<T> {
        private val store = mutableListOf<T>()
        override fun isEmpty() = store.isEmpty()
        override val size get() = store.size
        override fun contains(element: T) = store.contains(element)
        override fun containsAll(elements: Collection<T>) = store.containsAll(elements)
        override fun iterator() = store.iterator()

        override fun addFirst(element: T): Boolean {
            store.add(0, element)
            return true
        }

        override fun addFirstIfRoom(element: T) = addFirst(element)
        override fun addLast(element: T) = store.add(element)
        override fun addLastIfRoom(element: T) = addLast(element)
        override fun first() = store.first()
        override fun firstOrNull() = store.firstOrNull()
        override fun firstOrDefault(defaultValue: T) = firstOrElse { defaultValue }
        override fun firstOrElse(defaultValue: () -> T) = store.elementAtOrElse(0) { _ -> defaultValue() }
        override fun removeFirst() = store.removeFirst()
        override fun removeFirstOrNull() = store.removeFirstOrNull()
        override fun removeFirstOrDefault(defaultValue: T) = removeFirstOrElse { defaultValue }
        override fun removeFirstOrElse(defaultValue: () -> T) =
            if (store.isEmpty()) defaultValue() else store.removeAt(0)

        override fun last() = store.last()
        override fun lastOrNull() = store.lastOrNull()
        override fun lastOrDefault(defaultValue: T) = lastOrElse { defaultValue }
        override fun lastOrElse(defaultValue: () -> T) = store.elementAtOrElse(store.lastIndex) { _ -> defaultValue() }
        override fun removeLast() = store.removeLast()
        override fun removeLastOrNull() = store.removeLastOrNull()
        override fun removeLastOrDefault(defaultValue: T) = removeLastOrElse { defaultValue }
        override fun removeLastOrElse(defaultValue: () -> T) =
            if (store.isEmpty()) defaultValue() else store.removeAt(store.lastIndex)
    }

    data class OpenChunk(val start: Char, val startIndex: Int) {
        init {
            require(groupCharsMap.containsKey(start))
        }

        val endCharacter: Char get() = groupCharsMap[start]!!
    }

    sealed class ScanningResult {
        abstract val index: Int
    }

    data class SuccessResult(override val index: Int) : ScanningResult()

    data class IncompleteResult(override val index: Int, val expectedEndChars: List<Char>) :
        ScanningResult() {

        val completionScore
            get() = expectedEndChars.fold(0L) { acc, c ->
                val charScore = when (c) {
                    ')' -> 1
                    ']' -> 2
                    '}' -> 3
                    '>' -> 4
                    else -> 0
                }
                (acc * 5) + charScore
            }
    }

    data class CorruptedResult(override val index: Int, val char: Char, val description: String) : ScanningResult() {
        val characterScore: Long
            get() = when (char) {
                ')' -> 3
                ']' -> 57
                '}' -> 1197
                '>' -> 25137
                else -> 0
            }
    }

    val groupCharsMap = mapOf(
        '(' to ')',
        '[' to ']',
        '{' to '}',
        '<' to '>',
    )

    private val groupStartChars = groupCharsMap.map { (open, _) -> open }.toHashSet()
    private val groupEndChars = groupCharsMap.map { (_, close) -> close }.toHashSet()

    fun doSomething(input: Sequence<Char>): ScanningResult {
        val stack: Stack<OpenChunk> = MutableListBasedDeque()

        var lastIndex = -1

        for ((index, char) in input.withIndex()) {
            lastIndex = index

            if (groupStartChars.contains(char)) {
                // Start characters are always allowed.
                stack.addLast(OpenChunk(char, index))
            } else if (groupEndChars.contains(char)) {
                val top = stack.lastOrNull()
                val end = top?.endCharacter

                when {
                    end == null -> {
                        // No groups were open.
                        return CorruptedResult(index, char, "Input ascended without descending first.")
                    }
                    char == end -> {
                        // Group ended correctly.
                        stack.removeLast()
                    }
                    else -> {
                        // Group ended with wrong closer.
                        return CorruptedResult(index,
                            char,
                            "Input ascended from '${top.start}' using incorrect delimiter.")
                    }
                }
            } else {
                return CorruptedResult(index, char, "Input contained an invalid character.")
            }
        }

        return if (stack.isEmpty())
            SuccessResult(lastIndex)
        else
            IncompleteResult(lastIndex + 1, stack.reversed().map { it.endCharacter })
    }
}