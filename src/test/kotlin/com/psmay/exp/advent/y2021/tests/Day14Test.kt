package com.psmay.exp.advent.y2021.tests

import com.psmay.exp.advent.y2021.Day14.FormulaElement
import com.psmay.exp.advent.y2021.Day14.FormulaPairInsertionRule
import com.psmay.exp.advent.y2021.Day14.expanded
import com.psmay.exp.advent.y2021.Day14.expandedLongCounts
import com.psmay.exp.advent.y2021.Day14.getElementCounts
import com.psmay.exp.advent.y2021.Day14.toMap
import com.psmay.exp.advent.y2021.Day14.yields
import com.psmay.exp.advent.y2021.tests.helpers.UseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.asUseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.getTextLineSource
import com.psmay.exp.advent.y2021.tests.helpers.splitAtEmptyLines
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class Day14Test {

    data class TestInput(val polymerTemplate: List<FormulaElement>, val rules: List<FormulaPairInsertionRule>)

    private val exampleInput1 = TestInput(
        listOf(FormulaElement("N"), FormulaElement("N"), FormulaElement("C"), FormulaElement("B")),
        listOf(
            ("C" to "H") yields "B",
            ("H" to "H") yields "N",
            ("C" to "B") yields "H",
            ("N" to "H") yields "C",
            ("H" to "B") yields "C",
            ("H" to "C") yields "B",
            ("H" to "N") yields "C",
            ("N" to "N") yields "C",
            ("B" to "H") yields "H",
            ("N" to "C") yields "B",
            ("N" to "B") yields "B",
            ("B" to "N") yields "B",
            ("B" to "B") yields "N",
            ("B" to "C") yields "B",
            ("C" to "C") yields "N",
            ("C" to "N") yields "C",
        )
    )

    private val exampleRawInput1 = sequenceOf(
        "NNCB",
        "",
        "CH -> B",
        "HH -> N",
        "CB -> H",
        "NH -> C",
        "HB -> C",
        "HC -> B",
        "HN -> C",
        "NN -> C",
        "BH -> H",
        "NC -> B",
        "NB -> B",
        "BN -> B",
        "BB -> N",
        "BC -> B",
        "CC -> N",
        "CN -> C"
    ).asUseLinesSource()

    data class TestCase(
        val exampleRawInput: UseLinesSource,
        val exampleInput: TestInput,
        val part1Result: Int,
        val part2Result: Long,
    )

    private val testCases = listOf(
        TestCase(exampleRawInput1, exampleInput1, 1588, 2188189693529)
    )

    private val puzzleRawInput = getTextLineSource("y2021/Day14Input")

    private fun parsePolymerTemplate(line: String): List<FormulaElement> =
        line.toCharArray().map { FormulaElement(it.toString()) }

    private fun parseInsertionRule(line: String): FormulaPairInsertionRule {
        val regex = """^(\p{IsLetter})(\p{IsLetter}) -> (\p{IsLetter})$""".toRegex()
        val (inA, inB, out) = regex.matchEntire(line)?.destructured
            ?: throw IllegalArgumentException("Rule '$line' is in unexpected format.")
        return FormulaPairInsertionRule(FormulaElement(inA) to FormulaElement(inB), FormulaElement(out))
    }

    private fun parseAll(lines: Sequence<String>): TestInput {
        val chunks = lines.splitAtEmptyLines()

        val iterator = chunks.iterator()

        val template = if (iterator.hasNext()) {
            val line = iterator.next().singleOrNull()
                ?: throw IllegalArgumentException("Polymer template must be exactly one line.")
            parsePolymerTemplate(line)
        } else {
            throw IllegalArgumentException("Input is missing template chunk.")
        }

        val rules = if (iterator.hasNext()) {
            iterator.next().map { parseInsertionRule(it) }
        } else {
            throw IllegalArgumentException("Input is missing rules chunk.")
        }

        if (iterator.hasNext()) {
            throw IllegalArgumentException("Input contains unused data.")
        }

        return TestInput(template, rules)
    }

    @TestFactory
    fun `raw input cooks to correct cooked form`() = testCases.map { (actualRaw, expected, _, _) ->
        dynamicTest("$actualRaw to $expected") {
            val result = actualRaw.useLines { parseAll(it) }
            Assertions.assertEquals(expected, result)
        }
    }

    private fun part1(input: TestInput): Int {
        val rulesMap = input.rules.toMap()

        val template = input.polymerTemplate

        val result = template.asSequence().expanded(rulesMap, 10).toList()

        val counts = result.getElementCounts().entries.sortedBy { it.value }

        val (_, lowestCount) = counts.first()
        val (_, highestCount) = counts.last()

        return highestCount - lowestCount
    }

    private fun part2(input: TestInput): Long {
        val rulesMap = input.rules.toMap()

        val template = input.polymerTemplate

        val result = template.asSequence().expandedLongCounts(rulesMap, 40)

        val counts = result.entries.sortedBy { it.value }

        val (_, lowestCount) = counts.first()
        val (_, highestCount) = counts.last()

        return highestCount - lowestCount
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


