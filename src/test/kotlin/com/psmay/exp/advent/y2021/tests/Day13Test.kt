package com.psmay.exp.advent.y2021.tests

import com.psmay.exp.advent.y2021.Day13.DotFieldInstruction
import com.psmay.exp.advent.y2021.tests.helpers.UseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.asUseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.getTextLineSource
import com.psmay.exp.advent.y2021.tests.helpers.splitAtEmptyLines
import com.psmay.exp.advent.y2021.util.plotToGrid
import com.psmay.exp.advent.y2021.util.plotToStrings
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class Day13Test {

    data class TestInput(val dots: List<Pair<Int, Int>>, val instructions: List<DotFieldInstruction>)

    private val exampleInput1 = TestInput(
        listOf(
            6 to 10,
            0 to 14,
            9 to 10,
            0 to 3,
            10 to 4,
            4 to 11,
            6 to 0,
            6 to 12,
            4 to 1,
            0 to 13,
            10 to 12,
            3 to 4,
            3 to 0,
            8 to 4,
            1 to 10,
            2 to 14,
            8 to 10,
            9 to 0,
        ),
        listOf(
            DotFieldInstruction.FoldThroughRow(7),
            DotFieldInstruction.FoldThroughColumn(5)
        )
    )

    private val exampleRawInput1 = sequenceOf(
        "6,10",
        "0,14",
        "9,10",
        "0,3",
        "10,4",
        "4,11",
        "6,0",
        "6,12",
        "4,1",
        "0,13",
        "10,12",
        "3,4",
        "3,0",
        "8,4",
        "1,10",
        "2,14",
        "8,10",
        "9,0",
        "",
        "fold along y=7",
        "fold along x=5"
    ).asUseLinesSource()

    data class TestCase(
        val exampleRawInput: UseLinesSource,
        val exampleInput: TestInput,
        val part1Result: Int,
        val part2Result: Set<Pair<Int, Int>>,
    )

    private val testCases = listOf(
        TestCase(exampleRawInput1, exampleInput1, 17,
            // These points correspond to the drawn square in the example result.
            setOf(
                (0 to 0),
                (0 to 1),
                (0 to 2),
                (0 to 3),
                (0 to 4),
                (1 to 0),
                (1 to 4),
                (2 to 0),
                (2 to 4),
                (3 to 0),
                (3 to 4),
                (4 to 0),
                (4 to 1),
                (4 to 2),
                (4 to 3),
                (4 to 4))),
    )

    private val puzzleRawInput = getTextLineSource("y2021/Day13Input")



    private fun parseFold(line: String): DotFieldInstruction {
        val regex = """^fold along (\w)=(-?\d+)$""".toRegex()
        val (variable, value) = regex.matchEntire(line)?.destructured
            ?: throw IllegalArgumentException("Fold '$line' is in unexpected format.")

        return when (variable) {
            "x" -> DotFieldInstruction.FoldThroughColumn(value.toInt())
            "y" -> DotFieldInstruction.FoldThroughRow(value.toInt())
            else -> throw IllegalArgumentException("Unknown axis '$variable'.")
        }
    }

    private fun parsePoint(line: String): Pair<Int, Int> {
        val regex = """^(-?[0-9]+),(-?[0-9]+)$""".toRegex()
        val (x, y) = regex.matchEntire(line)?.destructured
            ?: throw IllegalArgumentException("Point '$line' is in unexpected format.")
        return x.toInt() to y.toInt()
    }

    private fun parseAll(lines: Sequence<String>): TestInput {
        val chunks = lines.splitAtEmptyLines()

        val iterator = chunks.iterator()

        val points = if (iterator.hasNext()) {
            iterator.next().map { parsePoint(it) }
        } else {
            throw IllegalArgumentException("Input is missing points chunk.")
        }

        val folds = if (iterator.hasNext()) {
            iterator.next().map { parseFold(it) }
        } else {
            throw IllegalArgumentException("Input is missing folds chunk.")
        }

        if (iterator.hasNext()) {
            throw IllegalArgumentException("Input contains unused data.")
        }

        return TestInput(points, folds)
    }

    @TestFactory
    fun `raw input cooks to correct cooked form`() = testCases.map { (actualRaw, expected, _, _) ->
        dynamicTest("$actualRaw to $expected") {
            val result = actualRaw.useLines { parseAll(it) }
            Assertions.assertEquals(expected, result)
        }
    }

    private fun part1(input: TestInput): Int {
        val dots = input.dots.toSet()
        val instructions = input.instructions.take(1) // Part 1 calls for using only first instruction

        val final = instructions.fold(dots) { acc, it -> it.apply(acc) }
        return final.size
    }

    private fun part2(input: TestInput): Set<Pair<Int, Int>> {
        val dots = input.dots.toSet()
        val instructions = input.instructions

        return instructions.fold(dots) { acc, it -> it.apply(acc) }
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
        println("Result visualized:")
        for (stringRow in result.plotToGrid().plotToStrings()) {
            println(stringRow)
        }
        println("--")
    }
}

