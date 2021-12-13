package com.psmay.exp.advent.y2021.tests

import com.psmay.exp.advent.y2021.Day12.CaveNode
import com.psmay.exp.advent.y2021.Day12.CaveSystem
import com.psmay.exp.advent.y2021.Day12.toCaveNode
import com.psmay.exp.advent.y2021.tests.helpers.UseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.asUseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.getTextLineSource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class Day12Test {
    private val exampleInput1 = sequenceOf(
        "start" to "A",
        "start" to "b",
        "A" to "c",
        "A" to "b",
        "b" to "d",
        "A" to "end",
        "b" to "end",
    ).map { (a, b) -> a.toCaveNode() to b.toCaveNode() }

    private val exampleInput2 = sequenceOf(
        "dc" to "end",
        "HN" to "start",
        "start" to "kj",
        "dc" to "start",
        "dc" to "HN",
        "LN" to "dc",
        "HN" to "end",
        "kj" to "sa",
        "kj" to "HN",
        "kj" to "dc",
    ).map { (a, b) -> a.toCaveNode() to b.toCaveNode() }

    private val exampleInput3 = sequenceOf(
        "fs" to "end",
        "he" to "DX",
        "fs" to "he",
        "start" to "DX",
        "pj" to "DX",
        "end" to "zg",
        "zg" to "sl",
        "zg" to "pj",
        "pj" to "he",
        "RW" to "he",
        "fs" to "DX",
        "pj" to "RW",
        "zg" to "RW",
        "start" to "pj",
        "he" to "WI",
        "zg" to "he",
        "pj" to "fs",
        "start" to "RW",
    ).map { (a, b) -> a.toCaveNode() to b.toCaveNode() }

    private val exampleRawInput1 = sequenceOf(
        "start-A",
        "start-b",
        "A-c",
        "A-b",
        "b-d",
        "A-end",
        "b-end",
    ).asUseLinesSource()

    private val exampleRawInput2 = sequenceOf(
        "dc-end",
        "HN-start",
        "start-kj",
        "dc-start",
        "dc-HN",
        "LN-dc",
        "HN-end",
        "kj-sa",
        "kj-HN",
        "kj-dc",
    ).asUseLinesSource()

    private val exampleRawInput3 = sequenceOf(
        "fs-end",
        "he-DX",
        "fs-he",
        "start-DX",
        "pj-DX",
        "end-zg",
        "zg-sl",
        "zg-pj",
        "pj-he",
        "RW-he",
        "fs-DX",
        "pj-RW",
        "zg-RW",
        "start-pj",
        "he-WI",
        "zg-he",
        "pj-fs",
        "start-RW",
    ).asUseLinesSource()

    data class TestCase(
        val exampleRawInput: UseLinesSource,
        val exampleInput: Sequence<Pair<CaveNode, CaveNode>>,
        val part1Result: Int,
        val part2Result: Int,
    )

    private val testCases = listOf(
        TestCase(exampleRawInput1, exampleInput1, 10, 36),
        TestCase(exampleRawInput2, exampleInput2, 19, 103),
        TestCase(exampleRawInput3, exampleInput3, 226, 3509),
    )

    private val puzzleRawInput = getTextLineSource("y2021/Day12Input")

    private fun parseLine(line: String): Pair<CaveNode, CaveNode> {
        val parts = line.split("-", limit = 3)
        if (parts.size != 2) throw IllegalArgumentException("Edge description must have 2 parts separated by '-'.")
        val (a, b) = parts
        return a.toCaveNode() to b.toCaveNode()
    }

    @TestFactory
    fun `raw input cooks to correct cooked form`() = testCases.map { (input, expectedSequence, _, _) ->
        val expected = expectedSequence.toList()
        dynamicTest("$input to $expected") {
            val result = input.useLines { lines -> lines.map { parseLine(it) }.toList() }
            Assertions.assertEquals(expected, result)
        }
    }

    private fun part1(input: Sequence<Pair<CaveNode, CaveNode>>): Int {
        val caveSystem = CaveSystem(input)
        val pathsToEnd = caveSystem.traverseStartToEndUsingInitialLogic()
        return pathsToEnd.size
    }

    private fun part2(input: Sequence<Pair<CaveNode, CaveNode>>): Int {
        val caveSystem = CaveSystem(input)
        val pathsToEnd = caveSystem.traverseStartToEndUsingNewLogic()
        return pathsToEnd.size
    }

    @TestFactory
    fun `part1 produces sample results as expected`() = testCases.map { (input, _, expected, _) ->
        dynamicTest("$input to $expected") {
            val result = input.useLines { lines -> part1(lines.map { parseLine(it) }) }
            Assertions.assertEquals(expected, result)
        }
    }

    @TestFactory
    fun `part2 produces sample results as expected`() = testCases.map { (input, _, _, expected) ->
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
