package com.psmay.exp.advent.y2021

import com.psmay.exp.advent.helpers.asUseLinesSource
import com.psmay.exp.advent.helpers.getTextLineSource
import org.junit.jupiter.api.Assertions.assertEquals
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
            assertEquals(expected, result)
        }
    }

    private fun part1(input: Sequence<List<Int>>): Int {
        return 0
    }

    private fun part2(input: Sequence<List<Int>>): Int {
        return 0
    }

    @TestFactory
    fun `part1 produces sample results as expected`() = listOf(
        exampleRawInput to 15
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            val result = input.useLines { lines -> part1(lines.map { parseLine(it) }) }
            assertEquals(expected, result)
        }
    }

    @TestFactory
    fun `part2 produces sample results as expected`() = listOf(
        exampleRawInput to -1
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            val result = input.useLines { lines -> part2(lines.map { parseLine(it) }) }
            assertEquals(expected, result)
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


