package com.psmay.exp.advent.y2021

import com.psmay.exp.advent.helpers.getTextFile
import com.psmay.exp.advent.y2021.Day07.findCostInitialFormula
import com.psmay.exp.advent.y2021.Day07.findCostNewFormula
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class Day07Test {
    private val exampleInput = listOf(16, 1, 2, 0, 4, 2, 7, 1, 2, 14)

    private val puzzleInput = getTextFile("y2021/Day07Input")
        .readText()
        .trimEnd()
        .split(",")
        .map { it.toInt() }

    // Starting with this day, I'm going to try implementing part1 and part2 in the test file so that the interface
    // exposed by the main code is more realistic.

    private fun part1(input: List<Int>): Int {
        return Day07
            .findBestItineraries(input, ::findCostInitialFormula)
            .map { it.cost }
            .first()
    }

    private fun part2(input: List<Int>): Int {
        return Day07
            .findBestItineraries(input, ::findCostNewFormula)
            .map { it.cost }
            .first()
    }

    @TestFactory
    fun `part1 produces sample results as expected`() = listOf(
        exampleInput to 37
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            val result = part1(input)
            assertEquals(expected, result)
        }
    }

    @TestFactory
    fun `part2 produces sample results as expected`() = listOf(
        exampleInput to 168
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            val result = part2(input)
            assertEquals(expected, result)
        }
    }

    @Test
    fun `part1 on puzzle input succeeds`() {
        val result = part1(puzzleInput)
        println("Result: $result")
    }

    @Test
    fun `part2 on puzzle input succeeds`() {
        val result = part2(puzzleInput)
        println("Result: $result")
    }
}

