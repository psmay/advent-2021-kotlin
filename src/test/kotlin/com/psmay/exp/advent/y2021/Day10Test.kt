package com.psmay.exp.advent.y2021

import com.psmay.exp.advent.helpers.asUseLinesSource
import com.psmay.exp.advent.helpers.getTextLineSource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class Day10Test {
    private val exampleInput = sequenceOf(
        "[({(<(())[]>[[{[]{<()<>>",
        "[(()[<>])]({[<{<<[]>>(",
        "{([(<{}[<>[]}>{[]{[(<()>",
        "(((({<>}<{<{<>}{[]{[]{}",
        "[[<[([]))<([[{}[[()]]]",
        "[{[{({}]{}}([{[{{{}}([]",
        "{<[[]]>}<{[{[{[]{()[[[]",
        "[<(<(<(<{}))><([]([]()",
        "<{([([[(<>()){}]>(<<{{",
        "<{([{{}}[<[[[<>{}]]]>[]]"
    )

    private val exampleRawInput = exampleInput.asUseLinesSource()

    private val puzzleRawInput = getTextLineSource("y2021/Day10Input")

    private fun part1(input: Sequence<String>): Long {
        return input.map { Day10.doSomething(it.asSequence()) }
            .filterIsInstance<Day10.CorruptedResult>()
            .map { it.characterScore }
            .sum()
    }

    private fun part2(input: Sequence<String>): Long {
        val scores = input.map { Day10.doSomething(it.asSequence()) }
            .filterIsInstance<Day10.IncompleteResult>()
            .map { it.completionScore }
            .sortedBy { it }
            .toList()

        if (scores.isEmpty()) throw Exception()

        val middleIndex = scores.size / 2
        return scores[middleIndex]
    }

    @TestFactory
    fun `part1 produces sample results as expected`() = listOf(
        exampleRawInput to 26397L
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            val result = input.useLines { lines -> part1(lines) }
            assertEquals(expected, result)
        }
    }

    @TestFactory
    fun `part2 produces sample results as expected`() = listOf(
        exampleRawInput to 288957L
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            val result = input.useLines { lines -> part2(lines) }
            assertEquals(expected, result)
        }
    }

    @Test
    fun `part1 on puzzle input succeeds`() {
        val result = puzzleRawInput.useLines { lines -> part1(lines) }
        println("Result: $result")
    }

    @Test
    fun `part2 on puzzle input succeeds`() {
        val result = puzzleRawInput.useLines { lines -> part2(lines) }
        println("Result: $result")
    }
}



