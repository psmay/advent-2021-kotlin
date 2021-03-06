package com.psmay.exp.advent.y2021.tests

import com.psmay.exp.advent.y2021.Day06
import com.psmay.exp.advent.y2021.tests.helpers.getTextFile
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class Day06Test {
    private val exampleInput = listOf(3, 4, 3, 1, 2)

    private val puzzleInput = getTextFile("y2021/Day06Input")
        .readText()
        .trimEnd()
        .split(",")
        .map { it.toInt() }

    @TestFactory
    fun `part1 produces sample results as expected`() = listOf(
        exampleInput to 5934
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            val result = Day06.part1(input)
            Assertions.assertEquals(expected, result)
        }
    }

    @TestFactory
    fun `part2 produces sample results as expected`() = listOf(
        exampleInput to 26984457539
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            val result = Day06.part2(input)
            Assertions.assertEquals(expected, result)
        }
    }

    @Test
    fun `part1 on puzzle input succeeds`() {
        val result = Day06.part1(puzzleInput)
        println("Result: $result")
    }

    @Test
    fun `part2 on puzzle input succeeds`() {
        val result = Day06.part2(puzzleInput)
        println("Result: $result")
    }
}