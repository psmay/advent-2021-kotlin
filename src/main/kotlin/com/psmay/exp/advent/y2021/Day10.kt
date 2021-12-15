package com.psmay.exp.advent.y2021

import com.psmay.exp.advent.y2021.util.Stack
import com.psmay.exp.advent.y2021.util.emptyStack

object Day10 {

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
        val stack: Stack<OpenChunk> = emptyStack()

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