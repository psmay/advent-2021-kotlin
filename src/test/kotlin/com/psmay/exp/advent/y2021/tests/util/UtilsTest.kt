package com.psmay.exp.advent.y2021.tests.util

import com.psmay.exp.advent.y2021.util.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class UtilsTest {
    data class FoldIncrementallyParams<T, R>(val source: Sequence<T>, val initial: R, val operation: (R, T) -> R)

    private fun <T, R> FoldIncrementallyParams<T, R>.run() =
        @Suppress("DEPRECATION") this.source.foldIncrementally(this.initial, this.operation)

    @TestFactory
    fun `foldIncrementally works with typical inputs`() = listOf(
        FoldIncrementallyParams(
            sequenceOf(1, 1, 1, 1, 1), 0) { a, x -> a + x } to listOf(1, 2, 3, 4, 5),
        FoldIncrementallyParams(
            sequenceOf(1, -1, -1, 1, 1, 1), 0) { a, x -> a + x } to listOf(1, 0, -1, 0, 1, 2),
        FoldIncrementallyParams(
            sequenceOf("A", "B", "C"), "") { a, x -> a + x } to listOf("A", "AB", "ABC")
    ).map { (input, expected) ->
        dynamicTest("From " + input.source.toList() + " to " + expected) {
            Assertions.assertEquals(expected, input.run().toList())
        }
    }

    @Test
    fun `foldIncrementally result can be traversed multiple times`() {
        val source = sequenceOf(1, 1, 1, 1, 1)
        val result = @Suppress("DEPRECATION") source.foldIncrementally(0) { a, x -> a + x }
        val listA = result.toList()
        val listB = result.toList()
        val xC = result.map { it + 10 }.toList()

        Assertions.assertEquals(listOf(1, 2, 3, 4, 5), listA)
        Assertions.assertEquals(listOf(1, 2, 3, 4, 5), listB)
        Assertions.assertEquals(listOf(11, 12, 13, 14, 15), xC)
    }

    @TestFactory
    fun `pairwise without initial produces specified pairs`() = listOf(
        listOf("A", "B", "C", "D") to listOf("A" to "B", "B" to "C", "C" to "D"),
        listOf(1, 2, 3, 4) to listOf(1 to 2, 2 to 3, 3 to 4),
        listOf(1) to emptyList(),
        emptyList<Int>() to emptyList(),
        listOf<Int?>(1, null, 2, null, 3) to listOf(1 to null, null to 2, 2 to null, null to 3),
    ).map { (input, expected) ->
        dynamicTest("From $input to $expected") {
            @Suppress("DEPRECATION")
            (Assertions.assertEquals(expected, input.pairwise()))
        }
    }

    @TestFactory
    fun `pairwise with initial produces specified pairs`() = listOf(
        ("A" to listOf("B", "C", "D")) to listOf("A" to "B", "B" to "C", "C" to "D"),
        (1 to listOf(2, 3, 4)) to listOf(1 to 2, 2 to 3, 3 to 4),
        (1 to emptyList<Int>()) to emptyList(),
        (1 to listOf(null, 2, null, 3)) to listOf(1 to null, null to 2, 2 to null, null to 3),
    ).map { (input, expected) ->
        dynamicTest("From $input to $expected") {
            val source = input.second
            val initial = input.first
            Assertions.assertEquals(expected, source.pairwise(initial))
        }
    }

    @TestFactory
    fun `windowed produces specified lists`() = listOf(
        (3 to listOf(10, 20, 30, 40, 50)) to listOf(listOf(10, 20, 30), listOf(20, 30, 40), listOf(30, 40, 50)),
        (3 to listOf(10, 20, 30)) to listOf(listOf(10, 20, 30)),
        (3 to listOf(10, 20)) to emptyList()
    ).map { (input, expected) ->
        dynamicTest("From $input to $expected") {
            val source = input.second
            val size = input.first
            @Suppress("DEPRECATION")
            (Assertions.assertEquals(expected, source.`makeshift windowed`(size)))
        }
    }

    private val transposeInput1 = listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9))
    private val transposeResult1 = listOf(listOf(1, 4, 7), listOf(2, 5, 8), listOf(3, 6, 9))

    private val transposeInput2 =
        listOf(listOf("A", "B", "C", "D"), listOf("E", "F", "G", "H"), listOf("I", "J", "K", "L"))
    private val transposeResult2 =
        listOf(listOf("A", "E", "I"), listOf("B", "F", "J"), listOf("C", "G", "K"), listOf("D", "H", "L"))

    private val transposeInput3 =
        listOf(
            listOf("A", "B", "C", "D"),
            listOf("E", "F", "G", "H", "dummy", "dummy"),
            listOf("I", "J", "K", "L", "dummy"))
    private val transposeResult3 =
        listOf(listOf("A", "E", "I"), listOf("B", "F", "J"), listOf("C", "G", "K"), listOf("D", "H", "L"))

    @TestFactory
    fun `transpose works with typical inputs`() = listOf(
        transposeInput1 to transposeResult1,
        transposeInput2 to transposeResult2,
        transposeInput3 to transposeResult3,
    ).map { (input, expected) ->
        dynamicTest("From $input to $expected") {
            Assertions.assertEquals(expected, input.transpose())
        }
    }

    @Test
    fun `triangle is correct for the first few`() {
        Assertions.assertEquals(listOf(0, 0 + 1, 0 + 1 + 2, 0 + 1 + 2 + 3, 0 + 1 + 2 + 3 + 4),
            (0..4).map { triangle(it) })
    }

    private fun <T : Comparable<T>> Iterable<T>.`old minAndMaxOrNull`(): Pair<T, T>? {
        val min = this.minOrNull()
        val max = this.maxOrNull()
        return if (min == null || max == null) null else (min to max)
    }

    @TestFactory
    fun `minAndMaxOrNull acts like old version (Int)`() = listOf(
        emptyList(),
        listOf(1, 6, 9, 2, 7, -5, 4),
    ).flatMap { input ->
        listOf(
            dynamicTest("Iterable from $input") {
                Assertions.assertEquals(input.`old minAndMaxOrNull`(), input.asIterable().minAndMaxOrNull())
            },
            dynamicTest("Sequence from $input") {
                Assertions.assertEquals(input.`old minAndMaxOrNull`(), input.asSequence().minAndMaxOrNull())
            }
        )
    }

    @TestFactory
    fun `minAndMaxOrNull acts like old version (String)`() = listOf(
        emptyList(),
        listOf("E", "X", "A", "M", "P", "L", "E"),
    ).flatMap { input ->
        listOf(
            dynamicTest("Iterable from $input") {
                Assertions.assertEquals(input.`old minAndMaxOrNull`(), input.asIterable().minAndMaxOrNull())
            },
            dynamicTest("Sequence from $input") {
                Assertions.assertEquals(input.`old minAndMaxOrNull`(), input.asSequence().minAndMaxOrNull())
            }
        )
    }
}