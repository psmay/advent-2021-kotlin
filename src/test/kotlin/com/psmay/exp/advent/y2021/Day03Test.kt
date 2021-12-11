package com.psmay.exp.advent.y2021

import com.psmay.exp.advent.y2021.tests.helpers.getTextFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class Day03Test {
    private val puzzleInput = getTextFile("y2021/Day03Input")
        .readLines()
        .map {
            it
                .asIterable()
                .map { c ->
                    when (c) {
                        '0' -> false
                        '1' -> true
                        else -> throw IllegalArgumentException("The character '$c' is not an allowed value")
                    }
                }
        }

    private val exampleInput = listOf(
        listOf(false, false, true, false, false),
        listOf(true, true, true, true, false),
        listOf(true, false, true, true, false),
        listOf(true, false, true, true, true),
        listOf(true, false, true, false, true),
        listOf(false, true, true, true, true),
        listOf(false, false, true, true, true),
        listOf(true, true, true, false, false),
        listOf(true, false, false, false, false),
        listOf(true, true, false, false, true),
        listOf(false, false, false, true, false),
        listOf(false, true, false, true, false),
    )

    @TestFactory
    fun `part1 produces sample results as expected`() = listOf(
        exampleInput to 198
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            assertEquals(expected, Day03.part1(input))
        }
    }

    @Test
    fun `part1 on puzzle input succeeds`() {
        val result = Day03.part1(puzzleInput)
        println("Result: $result")
    }

    @TestFactory
    fun `part2 produces sample results as expected`() = listOf(
        exampleInput to 230
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            assertEquals(expected, Day03.part2(input))
        }
    }

    @Test
    fun `part2 on puzzle input succeeds`() {
        val result = Day03.part2(puzzleInput)
        println("Result: $result")
    }
}