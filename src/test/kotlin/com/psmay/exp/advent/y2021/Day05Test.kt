package com.psmay.exp.advent.y2021

import com.psmay.exp.advent.y2021.Day05.part1
import com.psmay.exp.advent.y2021.Day05.part2
import com.psmay.exp.advent.y2021.tests.helpers.UseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.asUseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.getTextLineSource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class Day05Test {
    private val exampleInput = sequenceOf(
        (0 to 9) to (5 to 9),
        (8 to 0) to (0 to 8),
        (9 to 4) to (3 to 4),
        (2 to 2) to (2 to 1),
        (7 to 0) to (7 to 4),
        (6 to 4) to (2 to 0),
        (0 to 9) to (2 to 9),
        (3 to 4) to (1 to 4),
        (0 to 0) to (8 to 8),
        (5 to 5) to (8 to 2),
    )

    private val exampleLineInput = sequenceOf(
        "0,9 -> 5,9",
        "8,0 -> 0,8",
        "9,4 -> 3,4",
        "2,2 -> 2,1",
        "7,0 -> 7,4",
        "6,4 -> 2,0",
        "0,9 -> 2,9",
        "3,4 -> 1,4",
        "0,0 -> 8,8",
        "5,5 -> 8,2",
    ).asUseLinesSource()

    // The line format is simple and general enough that there's no reason to put it in the main logic.
    private fun exampleParseLine(input: String): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val regex = """(\d+),(\d+) -> (\d+),(\d+)""".toRegex()
        val (x1, y1, x2, y2) = regex.matchEntire(input)?.destructured
            ?: throw IllegalArgumentException("Line is in unexpected format.")

        // Fun fact: Kotlin's toInt() without a radix parameter always uses base-10; it's not like in JavaScript where
        // the input itself can sometimes set the base.
        val p1 = x1.toInt() to y1.toInt()
        val p2 = x2.toInt() to y2.toInt()
        return p1 to p2
    }

    @Test
    fun `parser result on example input is as expected`() {
        val expected = exampleInput.toList()
        val actual = exampleLineInput.useLines { lines -> lines.map { exampleParseLine(it) }.toList() }

        assertEquals(expected, actual)
    }

    @TestFactory
    fun `part1 produces sample results from parse as expected`() = listOf(
        exampleLineInput to 5
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            val result = runPart1OnLines(input)
            assertEquals(expected, result)
        }
    }

    @TestFactory
    fun `part2 produces sample results from parse as expected`() = listOf(
        exampleLineInput to 12
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            val result = runPart2OnLines(input)
            assertEquals(expected, result)
        }
    }

    private val puzzleInput = getTextLineSource("y2021/Day05Input")

    @Test
    fun `part1 on puzzle input succeeds`() {
        val result = runPart1OnLines(puzzleInput)
        println("Result: $result")
    }

    @Test
    fun `part2 on puzzle input succeeds`() {
        val result = runPart2OnLines(puzzleInput)
        println("Result: $result")
    }

    private fun runPart1OnLines(input: UseLinesSource) =
        input.useLines { lines -> part1(lines.map { exampleParseLine(it) }) }

    private fun runPart2OnLines(input: UseLinesSource) =
        input.useLines { lines -> part2(lines.map { exampleParseLine(it) }) }
}