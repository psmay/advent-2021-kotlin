package com.psmay.exp.advent.y2021.tests

import com.psmay.exp.advent.y2021.Day11
import com.psmay.exp.advent.y2021.tests.helpers.asUseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.getTextLineSource
import com.psmay.exp.advent.y2021.util.toGrid
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class Day11Test {
    private val exampleInput = sequenceOf(
        listOf(5, 4, 8, 3, 1, 4, 3, 2, 2, 3),
        listOf(2, 7, 4, 5, 8, 5, 4, 7, 1, 1),
        listOf(5, 2, 6, 4, 5, 5, 6, 1, 7, 3),
        listOf(6, 1, 4, 1, 3, 3, 6, 1, 4, 6),
        listOf(6, 3, 5, 7, 3, 8, 5, 4, 7, 8),
        listOf(4, 1, 6, 7, 5, 2, 4, 6, 4, 5),
        listOf(2, 1, 7, 6, 8, 4, 1, 7, 2, 1),
        listOf(6, 8, 8, 2, 8, 8, 1, 1, 3, 4),
        listOf(4, 8, 4, 6, 8, 4, 8, 5, 5, 4),
        listOf(5, 2, 8, 3, 7, 5, 1, 5, 2, 6),
    )

    private val exampleRawInput = sequenceOf(
        "5483143223",
        "2745854711",
        "5264556173",
        "6141336146",
        "6357385478",
        "4167524645",
        "2176841721",
        "6882881134",
        "4846848554",
        "5283751526"
    ).asUseLinesSource()

    private val puzzleRawInput = getTextLineSource("y2021/Day11Input")

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
        val inputGrid = input.asIterable().toGrid()
        val steps = Day11.run(inputGrid, 100)
        return steps.map { it.flashCount }.sum()
    }

    private fun part2(input: Sequence<List<Int>>): Int {
        return 0
    }

    @TestFactory
    fun `part1 produces sample results as expected`() = listOf(
        exampleRawInput to 1656
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            val result = input.useLines { lines -> part1(lines.map { parseLine(it) }) }
            Assertions.assertEquals(expected, result)
        }
    }

    @TestFactory
    fun `part2 produces sample results as expected`() = listOf(
        exampleRawInput to -1
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