package com.psmay.exp.advent.y2021.tests

import com.psmay.exp.advent.y2021.tests.helpers.UseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.asUseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.getTextLineSource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class Day16Test {
    private val exampleInputs = listOf(
        sequenceOf(0x3, 0x8, 0x0, 0x0, 0x6, 0xF, 0x4, 0x5, 0x2, 0x9, 0x1, 0x2, 0x0, 0x0),
        sequenceOf(0xE, 0xE, 0x0, 0x0, 0xD, 0x4, 0x0, 0xC, 0x8, 0x2, 0x3, 0x0, 0x6, 0x0),
        sequenceOf(0x8, 0xA, 0x0, 0x0, 0x4, 0xA, 0x8, 0x0, 0x1, 0xA, 0x8, 0x0, 0x0, 0x2, 0xF, 0x4, 0x7, 0x8),
        sequenceOf(0x6,
            0x2,
            0x0,
            0x0,
            0x8,
            0x0,
            0x0,
            0x0,
            0x1,
            0x6,
            0x1,
            0x1,
            0x5,
            0x6,
            0x2,
            0xC,
            0x8,
            0x8,
            0x0,
            0x2,
            0x1,
            0x1,
            0x8,
            0xE,
            0x3,
            0x4),
        sequenceOf(0xC,
            0x0,
            0x0,
            0x1,
            0x5,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1,
            0x6,
            0x1,
            0x1,
            0x5,
            0xA,
            0x2,
            0xE,
            0x0,
            0x8,
            0x0,
            0x2,
            0xF,
            0x1,
            0x8,
            0x2,
            0x3,
            0x4,
            0x0),
        sequenceOf(0xA,
            0x0,
            0x0,
            0x1,
            0x6,
            0xC,
            0x8,
            0x8,
            0x0,
            0x1,
            0x6,
            0x2,
            0x0,
            0x1,
            0x7,
            0xC,
            0x3,
            0x6,
            0x8,
            0x6,
            0xB,
            0x1,
            0x8,
            0xA,
            0x3,
            0xD,
            0x4,
            0x7,
            0x8,
            0x0),
    )

    private val exampleRawInputs = listOf(
        sequenceOf("38006F45291200").asUseLinesSource(),
        sequenceOf("EE00D40C823060").asUseLinesSource(),
        sequenceOf("8A004A801A8002F478").asUseLinesSource(),
        sequenceOf("620080001611562C8802118E34").asUseLinesSource(),
        sequenceOf("C0015000016115A2E0802F182340").asUseLinesSource(),
        sequenceOf("A0016C880162017C3686B18A3D4780").asUseLinesSource(),
    )

    private val examplePart1Results = listOf(1, 7, 16, 12, 23, 31)

    private val examplePart2Results = listOf(-1, -1, -1, -1, -1, -1)

    data class TestCase(
        val exampleRawInput: UseLinesSource,
        val exampleInput: Sequence<Int>,
        val part1Result: Int,
        val part2Result: Int,
    )

    private val testCases = exampleRawInputs.indices.map {
        TestCase(exampleRawInputs[it],
            exampleInputs[it],
            examplePart1Results[it],
            examplePart2Results[it])
    }

    private val puzzleRawInput = getTextLineSource("y2021/Day16Input")

    private fun parseLine(line: String) = line.asSequence().map { it.toString().toInt(16) }

    private fun parseAll(lines: Sequence<String>) = lines.map { parseLine(it) }.flatten()

    @TestFactory
    fun `raw input cooks to correct cooked form`() = testCases.map { (actualRaw, expected, _, _) ->
        dynamicTest("$actualRaw to $expected") {
            val result = actualRaw.useLines { parseAll(it) }
            Assertions.assertEquals(expected.toList(), result.toList())
        }
    }

    private fun part1(input: Sequence<Int>): Int {
        return 0
    }

    private fun part2(input: Sequence<Int>): Int {
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

