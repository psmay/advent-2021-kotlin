package com.psmay.exp.advent.y2021.tests

import com.psmay.exp.advent.y2021.Day08
import com.psmay.exp.advent.y2021.tests.helpers.asUseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.getTextLineSource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class Day08Test {

    data class InputUnit(val signalPatterns: List<Set<Char>>, val outputValue: List<Set<Char>>)

    private val exampleInput = listOf(
        InputUnit(listOf(
            "be".toSet(),
            "cfbegad".toSet(),
            "cbdgef".toSet(),
            "fgaecd".toSet(),
            "cgeb".toSet(),
            "fdcge".toSet(),
            "agebfd".toSet(),
            "fecdb".toSet(),
            "fabcd".toSet(),
            "edb".toSet(),
        ), listOf("fdgacbe".toSet(), "cefdb".toSet(), "cefbgd".toSet(), "gcbe".toSet())),
        InputUnit(listOf(
            "edbfga".toSet(),
            "begcd".toSet(),
            "cbg".toSet(),
            "gc".toSet(),
            "gcadebf".toSet(),
            "fbgde".toSet(),
            "acbgfd".toSet(),
            "abcde".toSet(),
            "gfcbed".toSet(),
            "gfec".toSet(),
        ), listOf("fcgedb".toSet(), "cgb".toSet(), "dgebacf".toSet(), "gc".toSet())),
        InputUnit(listOf(
            "fgaebd".toSet(),
            "cg".toSet(),
            "bdaec".toSet(),
            "gdafb".toSet(),
            "agbcfd".toSet(),
            "gdcbef".toSet(),
            "bgcad".toSet(),
            "gfac".toSet(),
            "gcb".toSet(),
            "cdgabef".toSet(),
        ), listOf("cg".toSet(), "cg".toSet(), "fdcagb".toSet(), "cbg".toSet())),
        InputUnit(listOf(
            "fbegcd".toSet(),
            "cbd".toSet(),
            "adcefb".toSet(),
            "dageb".toSet(),
            "afcb".toSet(),
            "bc".toSet(),
            "aefdc".toSet(),
            "ecdab".toSet(),
            "fgdeca".toSet(),
            "fcdbega".toSet(),
        ), listOf("efabcd".toSet(), "cedba".toSet(), "gadfec".toSet(), "cb".toSet())),
        InputUnit(listOf(
            "aecbfdg".toSet(),
            "fbg".toSet(),
            "gf".toSet(),
            "bafeg".toSet(),
            "dbefa".toSet(),
            "fcge".toSet(),
            "gcbea".toSet(),
            "fcaegb".toSet(),
            "dgceab".toSet(),
            "fcbdga".toSet(),
        ), listOf("gecf".toSet(), "egdcabf".toSet(), "bgf".toSet(), "bfgea".toSet())),
        InputUnit(listOf(
            "fgeab".toSet(),
            "ca".toSet(),
            "afcebg".toSet(),
            "bdacfeg".toSet(),
            "cfaedg".toSet(),
            "gcfdb".toSet(),
            "baec".toSet(),
            "bfadeg".toSet(),
            "bafgc".toSet(),
            "acf".toSet(),
        ), listOf("gebdcfa".toSet(), "ecba".toSet(), "ca".toSet(), "fadegcb".toSet())),
        InputUnit(listOf(
            "dbcfg".toSet(),
            "fgd".toSet(),
            "bdegcaf".toSet(),
            "fgec".toSet(),
            "aegbdf".toSet(),
            "ecdfab".toSet(),
            "fbedc".toSet(),
            "dacgb".toSet(),
            "gdcebf".toSet(),
            "gf".toSet(),
        ), listOf("cefg".toSet(), "dcbef".toSet(), "fcge".toSet(), "gbcadfe".toSet())),
        InputUnit(listOf(
            "bdfegc".toSet(),
            "cbegaf".toSet(),
            "gecbf".toSet(),
            "dfcage".toSet(),
            "bdacg".toSet(),
            "ed".toSet(),
            "bedf".toSet(),
            "ced".toSet(),
            "adcbefg".toSet(),
            "gebcd".toSet(),
        ), listOf("ed".toSet(), "bcgafe".toSet(), "cdgba".toSet(), "cbgef".toSet())),
        InputUnit(listOf(
            "egadfb".toSet(),
            "cdbfeg".toSet(),
            "cegd".toSet(),
            "fecab".toSet(),
            "cgb".toSet(),
            "gbdefca".toSet(),
            "cg".toSet(),
            "fgcdab".toSet(),
            "egfdb".toSet(),
            "bfceg".toSet(),
        ), listOf("gbdfcae".toSet(), "bgc".toSet(), "cg".toSet(), "cgb".toSet())),
        InputUnit(listOf(
            "gcafb".toSet(),
            "gcf".toSet(),
            "dcaebfg".toSet(),
            "ecagb".toSet(),
            "gf".toSet(),
            "abcdeg".toSet(),
            "gaef".toSet(),
            "cafbge".toSet(),
            "fdbac".toSet(),
            "fegbdc".toSet(),
        ), listOf("fgae".toSet(), "cfgab".toSet(), "fg".toSet(), "bagce".toSet())),

        )

    private val exampleRawInput = listOf(
        "be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe",
        "edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc",
        "fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg",
        "fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb",
        "aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea",
        "fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb",
        "dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe",
        "bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef",
        "egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb",
        "gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce"
    ).asUseLinesSource()

    private val exampleLineDecodeResults = listOf(8394, 9781, 1197, 9361, 4873, 8418, 4548, 1625, 8717, 4315)

    private val puzzleRawInput = getTextLineSource("y2021/Day08Input")

    private fun parseLine(line: String): InputUnit {
        val wordRegex = """^[a-g]+$""".toRegex()

        val sections = line.split(" | ")
        if (sections.size != 2) {
            throw IllegalArgumentException("Signal/output divider not found.")
        }
        val convertedSections = sections.map {
            val parts = it.split(" ")
            val sets = parts.map { part ->
                if (!wordRegex.matches(part)) {
                    throw IllegalArgumentException("Input word '$part' is in unexpected format.")
                }
                val set = part.toSet()
                if (set.size != part.length) {
                    throw IllegalArgumentException("Input word '$part' contains duplicate segments.")
                }
                set
            }
            sets
        }
        return InputUnit(convertedSections[0], convertedSections[1])
    }

    @TestFactory
    fun `raw input cooks to correct cooked form`() = listOf(
        exampleRawInput to exampleInput
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            val result = input.useLines { lines -> lines.map { parseLine(it) }.toList() }
            Assertions.assertEquals(expected, result)
        }
    }

    @Test
    fun `decoder results are correct for example input`() {
        val actual = exampleInput.map { inputUnit ->
            val decoded = Day08.decodeMessage(inputUnit.signalPatterns, inputUnit.outputValue)
            decoded.toInt()
        }
        Assertions.assertEquals(exampleLineDecodeResults, actual)
    }

    private fun part1(input: Sequence<InputUnit>): Int =
        input.map { inputUnit ->
            inputUnit.outputValue
                .map { digit -> Day08.isSimpleDigit(digit) }
                .count { it }
        }.sum()

    private fun part2(input: Sequence<InputUnit>): Int = input.map { inputUnit ->
        Day08.decodeMessage(inputUnit.signalPatterns, inputUnit.outputValue).toInt()
    }.sum()

    @TestFactory
    fun `part1 produces sample results as expected`() = listOf(
        exampleRawInput to 26
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            val result = input.useLines { lines -> part1(lines.map { parseLine(it) }) }
            Assertions.assertEquals(expected, result)
        }
    }

    @TestFactory
    fun `part2 produces sample results as expected`() = listOf(
        exampleRawInput to 61229
    ).map { (input, expected) ->
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