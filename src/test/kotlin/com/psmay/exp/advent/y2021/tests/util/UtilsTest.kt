package com.psmay.exp.advent.y2021.tests.util

import com.psmay.exp.advent.y2021.util.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals

internal class UtilsTest {

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

    @Test
    fun `takeThroughFirst and dropThroughFirst when there is a first`() {
        val from = sequenceOf("E", "X", "A", "M", "P", "L", "E")
        assertEquals(listOf("E", "X", "A", "M"), from.takeThroughFirst { it == "M" }.toList())
        assertEquals(listOf("P", "L", "E"), from.dropThroughFirst { it == "M" }.toList())
    }

    @Test
    fun `takeThroughFirst and dropThroughFirst when there is not a first`() {
        val from = sequenceOf("E", "X", "A", "M", "P", "L", "E")
        assertEquals(listOf("E", "X", "A", "M", "P", "L", "E"), from.takeThroughFirst { it == "Z" }.toList())
        assertEquals(listOf(), from.dropThroughFirst { it == "Z" }.toList())
    }

    @Test
    fun `takeWhileThenTake works as expected`() {
        val from = sequenceOf("E", "X", "A", "M", "P", "L", "E")
        assertEquals(listOf("E", "X", "A", "M", "P"), from.takeWhileThenTake(3) { it != "A" }.toList())
        assertEquals(listOf("E", "X", "A", "M", "P", "L", "E"), from.takeWhileThenTake(3) { it != "L" }.toList())
        assertEquals(listOf("E", "X", "A", "M", "P", "L", "E"), from.takeWhileThenTake(3) { it != "Z" }.toList())

        assertEquals(listOf("E", "X"), from.takeWhileThenTake(0) { it != "A" }.toList())
        assertEquals(listOf("E", "X", "A", "M", "P"), from.takeWhileThenTake(0) { it != "L" }.toList())
        assertEquals(listOf("E", "X", "A", "M", "P", "L", "E"), from.takeWhileThenTake(0) { it != "Z" }.toList())
    }

    @Test
    fun `dropWhileThenDrop works as expected`() {
        val from = sequenceOf("E", "X", "A", "M", "P", "L", "E")
        //assertEquals(listOf("L", "E"), from.dropWhileThenDrop(3) { it != "A" }.toList())
        //assertEquals(listOf(), from.dropWhileThenDrop(3) { it != "L" }.toList())
        //assertEquals(listOf(), from.dropWhileThenDrop(3) { it != "Z" }.toList())

        assertEquals(listOf("A", "M", "P", "L", "E"), from.dropWhileThenDrop(0) { it != "A" }.toList())
        assertEquals(listOf("L", "E"), from.dropWhileThenDrop(0) { it != "L" }.toList())
        assertEquals(listOf(), from.dropWhileThenDrop(0) { it != "Z" }.toList())
    }
}