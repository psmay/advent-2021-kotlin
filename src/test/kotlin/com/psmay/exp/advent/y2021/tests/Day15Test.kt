package com.psmay.exp.advent.y2021.tests

import com.psmay.exp.advent.y2021.day15.findPath
import com.psmay.exp.advent.y2021.tests.helpers.UseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.asUseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.getTextLineSource
import com.psmay.exp.advent.y2021.util.Grid
import com.psmay.exp.advent.y2021.util.concatenateHorizontally
import com.psmay.exp.advent.y2021.util.concatenateVertically
import com.psmay.exp.advent.y2021.util.toGrid
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals

internal class Day15Test {
    private val exampleInput1 = sequenceOf(
        listOf(1, 1, 6, 3, 7, 5, 1, 7, 4, 2),
        listOf(1, 3, 8, 1, 3, 7, 3, 6, 7, 2),
        listOf(2, 1, 3, 6, 5, 1, 1, 3, 2, 8),
        listOf(3, 6, 9, 4, 9, 3, 1, 5, 6, 9),
        listOf(7, 4, 6, 3, 4, 1, 7, 1, 1, 1),
        listOf(1, 3, 1, 9, 1, 2, 8, 1, 3, 7),
        listOf(1, 3, 5, 9, 9, 1, 2, 4, 2, 1),
        listOf(3, 1, 2, 5, 4, 2, 1, 6, 3, 9),
        listOf(1, 2, 9, 3, 1, 3, 8, 5, 2, 1),
        listOf(2, 3, 1, 1, 9, 4, 4, 5, 8, 1),
    ).asIterable().toGrid()

    private val exampleRawInput1 = sequenceOf(
        "1163751742",
        "1381373672",
        "2136511328",
        "3694931569",
        "7463417111",
        "1319128137",
        "1359912421",
        "3125421639",
        "1293138521",
        "2311944581"
    ).asUseLinesSource()

    data class TestCase(
        val exampleRawInput: UseLinesSource,
        val exampleInput: Grid<Int>,
        val part1Result: Int,
        val part2Result: Int,
    )

    private val testCases = listOf(
        TestCase(exampleRawInput1, exampleInput1, 40, 315)
    )

    private val puzzleRawInput = getTextLineSource("y2021/Day15Input")

    private fun parseLine(line: String): List<Int> = line.toCharArray().map { it.toString().toInt() }

    private fun parseAll(lines: Sequence<String>) = lines.map { parseLine(it) }.asIterable().toGrid()

    @TestFactory
    fun `raw input cooks to correct cooked form`() = testCases.map { (actualRaw, expected, _, _) ->
        dynamicTest("$actualRaw to $expected") {
            val result = actualRaw.useLines { parseAll(it) }
            Assertions.assertEquals(expected, result)
        }
    }

    private fun part1(input: Grid<Int>): Int {
        @Suppress("UnnecessaryVariable")
        val grid = input

        for(row in grid.rows) {
            println("ROW: $row")
        }

        val path = grid.findPath().drop(1).toList()
        for (x in path) {
            println("$x ${grid[x]}")
        }
        val costs = path.map { grid[it] }
        return costs.sum()
    }

    private fun part2(input: Grid<Int>): Int {
        val grid = expandGrid(input)

        val path = grid.findPath().drop(1).toList()
        for (x in path) {
            println("$x ${grid[x]}")
        }
        val costs = path.map { grid[it] }
        return costs.sum()
    }

    private fun expandGrid(input: Grid<Int>): Grid<Int> {
        // Reranges a value to the range 1..9.
        // Example: 15 -> 6
        fun clamp(n: Int) = (((n - 1) + 9) % 9) + 1

        // This assumes that grid already has properly ranged numbers.
        // If not, remove the numberToAdd == 0 case.
        fun gridAddAndClamp(grid: Grid<Int>, numberToAdd: Int) =
            if (numberToAdd == 0) {
                grid
            } else {
                grid.map { clamp(it + numberToAdd) }
            }

        val h = (0..4)
            .map { gridAddAndClamp(input, it) }
            .concatenateHorizontally()

        return (0..4)
            .map { gridAddAndClamp(h, it) }
            .concatenateVertically()
    }

    @TestFactory
    fun `part1 produces sample results as expected`() = testCases.map { (input, _, expected, _) ->
        dynamicTest("$input to $expected") {
            val result = input.useLines { lines -> part1(parseAll(lines)) }
            Assertions.assertEquals(expected, result)
        }
    }

    @TestFactory
    fun `part2 produces sample results as expected`() = testCases.map { (input, _, _, expected) ->
        dynamicTest("$input to $expected") {
            val result = input.useLines { lines -> part2(parseAll(lines)) }
            Assertions.assertEquals(expected, result)
        }
    }

    @Test
    fun `part1 on puzzle input succeeds`() {
        val result = puzzleRawInput.useLines { lines -> part1(parseAll(lines)) }
        assertEquals(386, result)
        println("Result: $result")
    }

    @Test
    fun `part2 on puzzle input succeeds`() {
        val result = puzzleRawInput.useLines { lines -> part2(parseAll(lines)) }
        println("Result: $result")
    }
}
