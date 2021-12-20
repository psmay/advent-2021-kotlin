package com.psmay.exp.advent.y2021.tests

import com.psmay.exp.advent.y2021.day17.TargetArea
import com.psmay.exp.advent.y2021.day17.parseTargetArea
import com.psmay.exp.advent.y2021.day17.scanForHighestHit
import com.psmay.exp.advent.y2021.tests.helpers.UseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.asUseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.getTextLineSource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class Day17Test {
    private val exampleInput1 = listOf(
        TargetArea(20..30, -10..-5)
    )

    private val exampleRawInput1 = sequenceOf(
        "target area: x=20..30, y=-10..-5"
    ).asUseLinesSource()

    data class TestCase(
        val exampleRawInput: UseLinesSource,
        val exampleInput: List<TargetArea>,
        val part1Result: Int,
        val part2Result: Int,
    )

    private val testCases = listOf(
        TestCase(exampleRawInput1, exampleInput1, 45, -1)
    )

    private val puzzleRawInput = getTextLineSource("y2021/Day17Input")

    private fun parseLine(line: String): TargetArea = parseTargetArea(line)

    private fun parseAll(lines: Sequence<String>) = lines.map { parseLine(it) }.toList()

    @TestFactory
    fun `raw input cooks to correct cooked form`() = testCases.map { (actualRaw, expected, _, _) ->
        dynamicTest("$actualRaw to $expected") {
            val result = actualRaw.useLines { parseAll(it) }
            Assertions.assertEquals(expected, result)
        }
    }

    private fun part1(input: List<TargetArea>): Int {
        val area = input.single()
        val hit = scanForHighestHit(area)
        println("Highest: $hit")

        val (_, highPoint) = hit
        val (_, y) = highPoint
        return y
    }

    private fun part2(input: List<TargetArea>): Int {
        return 0
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
        println("Result: $result")
    }

    @Test
    fun `part2 on puzzle input succeeds`() {
        val result = puzzleRawInput.useLines { lines -> part2(parseAll(lines)) }
        println("Result: $result")
    }
}





