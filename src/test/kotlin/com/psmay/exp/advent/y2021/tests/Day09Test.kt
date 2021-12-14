package com.psmay.exp.advent.y2021.tests

import com.psmay.exp.advent.y2021.Day09
import com.psmay.exp.advent.y2021.Day09.mapSurroundingBasin
import com.psmay.exp.advent.y2021.tests.helpers.asUseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.getTextLineSource
import com.psmay.exp.advent.y2021.util.Grid
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class Day09Test {
    private val exampleInput = sequenceOf(
        listOf(2, 1, 9, 9, 9, 4, 3, 2, 1, 0),
        listOf(3, 9, 8, 7, 8, 9, 4, 9, 2, 1),
        listOf(9, 8, 5, 6, 7, 8, 9, 8, 9, 2),
        listOf(8, 7, 6, 7, 8, 9, 6, 7, 8, 9),
        listOf(9, 8, 9, 9, 9, 6, 5, 6, 7, 8),
    )

    private val exampleRawInput = sequenceOf(
        "2199943210",
        "3987894921",
        "9856789892",
        "8767896789",
        "9899965678"
    ).asUseLinesSource()

    private val puzzleRawInput = getTextLineSource("y2021/Day09Input")

    private fun parseLine(line: String): List<Int> = line.toCharArray().map { it.toString().toInt() }

    @TestFactory
    fun `raw input cooks to correct cooked form`() = listOf(
        exampleRawInput to exampleInput.toList()
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            val result = input.useLines { lines -> lines.map { parseLine(it) }.toList() }
            Assertions.assertEquals(expected, result)
        }
    }

    private fun part1(input: Sequence<List<Int>>): Int {
        val lowPoints = Day09.getCellWindows(input)
            .filter { it.isLowPoint() }

        val riskLevels = lowPoints.map { it.height + 1 }

        return riskLevels.sum()
    }

    private fun part2(input: Sequence<List<Int>>): Int {
        val heightMap = Grid(input.toList())

        val seen = mutableSetOf<Pair<Int, Int>>()
        val basins = mutableSetOf<Set<Pair<Int, Int>>>()

        for (position in heightMap.allPositions) {
            if (!seen.contains(position)) {
                val basin = heightMap.mapSurroundingBasin(position)

                if (basin.isNotEmpty()) {
                    basins.add(basin)
                    seen.addAll(basin)
                }
            }
        }

        val largestBasinSizes = basins.map { it.size }.sortedByDescending { it }.take(3)

        if (largestBasinSizes.size != 3) {
            throw Exception("Number of basins found was ${largestBasinSizes.size}, not 3.")
        }

        return largestBasinSizes.reduce { a, b -> a * b }
    }

    @TestFactory
    fun `part1 produces sample results as expected`() = listOf(
        exampleRawInput to 15
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            val result = input.useLines { lines -> part1(lines.map { parseLine(it) }) }
            Assertions.assertEquals(expected, result)
        }
    }

    @TestFactory
    fun `part2 produces sample results as expected`() = listOf(
        exampleRawInput to 1134
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            val result = input.useLines { lines -> part2(lines.map { parseLine(it) }) }
            Assertions.assertEquals(expected, result)
        }
    }

    @Test
    fun `part1 on puzzle input succeeds`() {
        val result = puzzleRawInput.useLines { lines -> part1(lines.map { parseLine(it) }) }
        println("Result: $result")
    }

    @Test
    fun `part2 on puzzle input succeeds`() {
        val result = puzzleRawInput.useLines { lines -> part2(lines.map { parseLine(it) }) }
        println("Result: $result")
    }
}