package com.psmay.exp.advent.y2021

import com.psmay.exp.advent.helpers.getTextFile
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals

internal class Day01Test {
    @TestFactory
    fun `part1 produces sample results as expected`() = listOf(
        listOf(199, 200, 208, 210, 200, 207, 240, 269, 260, 263) to 7
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            assertEquals(expected, Day01.part1(input))
        }
    }

    @TestFactory
    fun `part2 produces sample results as expected`() = listOf(
        listOf(607, 618, 618, 617, 647, 716, 769, 792) to 5
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            assertEquals(expected, Day01.part2(input))
        }
    }

    private val puzzleInput = getTextFile("y2021/Day01Input")
        .readLines()
        .map { it.toInt() }

    @Test
    fun `part1 on puzzle input succeeds`() {
        val result = Day01.part1(puzzleInput)
        println("Result: $result")
    }

    @Test
    fun `part2 on puzzle input succeeds`() {
        val result = Day01.part2(puzzleInput)
        println("Result: $result")
    }
}