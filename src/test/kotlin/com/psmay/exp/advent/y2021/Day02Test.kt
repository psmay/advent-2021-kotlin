package com.psmay.exp.advent.y2021

import com.psmay.exp.advent.helpers.getTextFile
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals

internal class Day02Test {
    private val puzzleInput = getTextFile("y2021/Day02Input")
        .readLines()
        .map { Day02.Instruction.parse(it) }

    val exampleInput = listOf(
        "forward 5",
        "down 5",
        "forward 8",
        "up 3",
        "down 8",
        "forward 2",
    ).map { Day02.Instruction.parse(it) }

    @TestFactory
    fun `part1 produces sample results as expected`() = listOf(
        exampleInput to 150
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            assertEquals(expected, Day02.part1(input))
        }
    }

    @Test
    fun `part1 on puzzle input succeeds`() {
        val result = Day02.part1(puzzleInput)
        println("Result: $result")
    }

    @TestFactory
    fun `part2 produces sample results as expected`() = listOf(
        exampleInput to 900
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            assertEquals(expected, Day02.part2(input))
        }
    }

    @Test
    fun `part2 on puzzle input succeeds`() {
        val result = Day02.part2(puzzleInput)
        println("Result: $result")
    }
}