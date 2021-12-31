package com.psmay.exp.advent.y2021.tests

import com.psmay.exp.advent.y2021.day18.snailfish.Element
import com.psmay.exp.advent.y2021.day18.snailfish.snailAddTo
import com.psmay.exp.advent.y2021.day18.snailfish.snailTo
import com.psmay.exp.advent.y2021.tests.helpers.UseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.asUseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.getTextLineSource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class Day18Test {
    private val exampleInput1 =
        sequenceOf(
            (((0 snailTo (5 snailTo 8)) snailTo ((1 snailTo 7) snailTo (9 snailTo 6))) snailTo ((4 snailTo (1 snailTo 2)) snailTo ((1 snailTo 4) snailTo 2))),
            (((5 snailTo (2 snailTo 8)) snailTo 4) snailTo (5 snailTo ((9 snailTo 9) snailTo 0))),
            (6 snailTo (((6 snailTo 2) snailTo (5 snailTo 6)) snailTo ((7 snailTo 6) snailTo (4 snailTo 7)))),
            (((6 snailTo (0 snailTo 7)) snailTo (0 snailTo 9)) snailTo (4 snailTo (9 snailTo (9 snailTo 0)))),
            (((7 snailTo (6 snailTo 4)) snailTo (3 snailTo (1 snailTo 3))) snailTo (((5 snailTo 5) snailTo 1) snailTo 9)),
            ((6 snailTo ((7 snailTo 3) snailTo (3 snailTo 2))) snailTo (((3 snailTo 8) snailTo (5 snailTo 7)) snailTo 4)),
            ((((5 snailTo 4) snailTo (7 snailTo 7)) snailTo 8) snailTo ((8 snailTo 3) snailTo 8)),
            ((9 snailTo 3) snailTo ((9 snailTo 9) snailTo (6 snailTo (4 snailTo 9)))),
            ((2 snailTo ((7 snailTo 7) snailTo 7)) snailTo ((5 snailTo 8) snailTo ((9 snailTo 3) snailTo (0 snailTo 2)))),
            ((((5 snailTo 2) snailTo 5) snailTo (8 snailTo (3 snailTo 7))) snailTo ((5 snailTo (7 snailTo 5)) snailTo (4 snailTo 4))),
        )

    private val exampleRawInput1 = sequenceOf(
        "[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]",
        "[[[5,[2,8]],4],[5,[[9,9],0]]]",
        "[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]",
        "[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]",
        "[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]",
        "[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]",
        "[[[[5,4],[7,7]],8],[[8,3],8]]",
        "[[9,3],[[9,9],[6,[4,9]]]]",
        "[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]",
        "[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]"
    ).asUseLinesSource()

    data class TestCase(
        val exampleRawInput: UseLinesSource,
        val exampleInput: Sequence<Element>,
        val part1Result: Long,
        val part2Result: Long,
    )

    private val testCases = listOf(
        TestCase(exampleRawInput1, exampleInput1, 4140, 3993),
    )

    private val puzzleRawInput = getTextLineSource("y2021/Day18Input")

    private fun parseLine(line: String): Element = Element.parse(line)

    private fun part1(input: Sequence<Element>): Long {
        val total = input.reduce { a, b -> a.snailAddTo(b) }
        return total.magnitude
    }

    private fun part2(input: Sequence<Element>): Long {
        val elements = input.toList()

        // Every distinct pair of indices in the given elements, where order is important.
        val indices = elements.indices.asSequence()
        val indexPairs = indices.flatMap { ai -> indices.filter { bi -> ai != bi }.map { bi -> ai to bi } }
        val elementPairs = indexPairs.map { (a, b) -> elements[a] to elements[b] }

        val magnitudes = elementPairs.map { (a, b) -> a.snailAddTo(b).magnitude }

        return magnitudes.maxOrNull()!!
    }

    @TestFactory
    fun `raw input cooks to correct cooked form`() = testCases.map { (actualRaw, expected, _, _) ->
        dynamicTest("$actualRaw to $expected") {
            val result = actualRaw.useLines { lines -> lines.map { parseLine(it) } }
            Assertions.assertEquals(expected.toList(), result.toList())
        }
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


