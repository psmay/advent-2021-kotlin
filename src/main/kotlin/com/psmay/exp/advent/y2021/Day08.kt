@file:Suppress("MemberVisibilityCanBePrivate") // Does this nag go away with the library project type?

package com.psmay.exp.advent.y2021

object Day08 {
    fun isSimpleDigit(set: Set<Char>): Boolean {
        set.ensureSegmentSetIsValid()
        return when (set.size) {
            2, 3, 4, 7 -> true
            else -> false
        }
    }

    fun mapDigitSignalPatterns(input: List<Set<Char>>): Map<Set<Char>, Int> {
        val bySize = convertInputToSizeMapping(input)

        // These four digits can be determined by size alone.
        val setFor1 = bySize[2]?.single() ?: throw IllegalArgumentException("2-segment set is missing.")
        val setFor7 = bySize[3]?.single() ?: throw IllegalArgumentException("3-segment set is missing.")
        val setFor4 = bySize[4]?.single() ?: throw IllegalArgumentException("4-segment set is missing.")
        val setFor8 = bySize[7]?.single() ?: throw IllegalArgumentException("7-segment set is missing.")

        // Others need some help.
        val setsFor5Segments =
            bySize[5]?.toMutableSet() ?: throw IllegalArgumentException("5-segment sets are missing.")
        if (setsFor5Segments.size != 3) throw IllegalArgumentException("Number of 5-segment sets is incorrect.")
        val setsFor6Segments =
            bySize[6]?.toMutableSet() ?: throw IllegalArgumentException("6-segment digits are missing.")
        if (setsFor6Segments.size != 3) throw IllegalArgumentException("Number of 6-segment sets is incorrect.")

        // Of 2, 3, and 5, only 3 includes both segments of 1.
        val setFor3 = setsFor5Segments.removeSingleOrNull { it.containsAll(setFor1) }
            ?: throw IllegalArgumentException("Provided 5-segment sets are inconsistent.")

        // 9 is the union of 3 and 4.
        val union3Or4 = setFor3.union(setFor4)
        val setFor9 = setsFor6Segments.removeSingleOrNull { it == union3Or4 }
            ?: throw IllegalArgumentException("Provided 6-segment sets are inconsistent.")

        // The bottom-left segment is the inverse of 9.
        val bottomLeft = setFor8 - setFor9

        // Of 2 and 3, only 2 has the bottom-left segment.
        val setFor2 = setsFor5Segments.removeSingleOrNull { it.containsAll(bottomLeft) }
            ?: throw IllegalArgumentException("Provided 5-segment sets are inconsistent.")

        // Only 5 remains in the 2 3 5 sets.
        val setFor5 = setsFor5Segments.removeSingleOrNull()
            ?: throw IllegalArgumentException("Provided 5-segment sets are inconsistent.")

        // 6 is the union of 5 with the bottom-left segment.
        val union5OrBottomLeft = setFor5.union(bottomLeft)
        val setFor6 = setsFor6Segments.removeSingleOrNull { it == union5OrBottomLeft }
            ?: throw IllegalArgumentException("Provided 6-segment sets are inconsistent.")

        // Only 0 remains in the 0 6 9 sets.
        val setFor0 = setsFor6Segments.removeSingleOrNull()
            ?: throw IllegalArgumentException("Provided 6-segment sets are inconsistent.")

        return listOf(
            setFor0 to 0,
            setFor1 to 1,
            setFor2 to 2,
            setFor3 to 3,
            setFor4 to 4,
            setFor5 to 5,
            setFor6 to 6,
            setFor7 to 7,
            setFor8 to 8,
            setFor9 to 9,
        ).associate { it }
    }

    fun decodeMessage(
        digitSignalPatterns: List<Set<Char>>,
        message: List<Set<Char>>,
    ): String {
        val mapping = mapDigitSignalPatterns(digitSignalPatterns)
        return decodeMessage(mapping, message)
    }

    fun decodeMessage(
        digitMapping: Map<Set<Char>, Int>,
        message: List<Set<Char>>,
    ): String {
        return message
            .map { digitMapping[it] ?: throw IllegalArgumentException("Output digit not found in lookup table") }
            .joinToString("")
    }

    private fun <T> MutableSet<T>.removeSingleOrNull(): T? {
        val result = singleOrNull()
        if (result != null) {
            remove(result)
        }
        return result
    }

    private fun <T> MutableSet<T>.removeSingleOrNull(predicate: (T) -> Boolean): T? {
        val result = singleOrNull(predicate)
        if (result != null) {
            remove(result)
        }
        return result
    }

    private fun Set<Char>.ensureSegmentSetIsValid() {
        for (segment in this) {
            require(('a'..'g').contains(segment)) { "Input contains invalid segment letter." }
        }
    }

    private fun convertInputToSizeMapping(input: List<Set<Char>>): Map<Int, List<Set<Char>>> {
        require(input.size == 10) { "Input must be 10 sets." }
        require(input.distinct().size == input.size) { "Input sets must be distinct." }

        for (set in input) {
            set.ensureSegmentSetIsValid()
        }

        return input.groupBy { it.size }
    }
}