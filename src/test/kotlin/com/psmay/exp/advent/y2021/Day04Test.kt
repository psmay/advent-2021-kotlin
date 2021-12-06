package com.psmay.exp.advent.y2021

import com.psmay.exp.advent.helpers.getTextFile
import com.psmay.exp.advent.y2021.Day04.BingoBoardGrid
import com.psmay.exp.advent.y2021.Day04.BingoBoardRow
import com.psmay.exp.advent.y2021.Day04.BingoNumberOrder
import com.psmay.exp.advent.y2021.Day04.parseBingoBoardRow
import com.psmay.exp.advent.y2021.Day04.parseBingoNumberOrder
import com.psmay.exp.advent.y2021.Day04.part1
import com.psmay.exp.advent.y2021.Day04.part2
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class Day04Test {

    data class GameInput(val order: BingoNumberOrder, val boardGrids: List<BingoBoardGrid>)

    private val puzzleInput = exampleLineParsing(getTextFile("y2021/Day04Input").readLines())

    // For testing the parser
    private val exampleLineInput = listOf(
        "7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1",
        "",
        "22 13 17 11  0",
        " 8  2 23  4 24",
        "21  9 14 16  7",
        " 6 10  3 18  5",
        " 1 12 20 15 19",
        "",
        " 3 15  0  2 22",
        " 9 18 13 17  5",
        "19  8  7 25 23",
        "20 11 10 24  4",
        "14 21 16 12  6",
        "",
        "14 21 17 24  4",
        "10 16 15  9 19",
        "18  8 23 26 20",
        "22 11 13  6  5",
        " 2  0 12  3  7"
    )

    // For testing the application code
    private val exampleInput = GameInput(
        BingoNumberOrder(listOf(7,
            4,
            9,
            5,
            11,
            17,
            23,
            2,
            0,
            14,
            21,
            24,
            10,
            16,
            13,
            6,
            15,
            25,
            12,
            22,
            18,
            20,
            8,
            19,
            3,
            26,
            1)),

        listOf(
            BingoBoardGrid(listOf(
                BingoBoardRow(listOf(22, 13, 17, 11, 0)),
                BingoBoardRow(listOf(8, 2, 23, 4, 24)),
                BingoBoardRow(listOf(21, 9, 14, 16, 7)),
                BingoBoardRow(listOf(6, 10, 3, 18, 5)),
                BingoBoardRow(listOf(1, 12, 20, 15, 19)),
            )),
            BingoBoardGrid(listOf(
                BingoBoardRow(listOf(3, 15, 0, 2, 22)),
                BingoBoardRow(listOf(9, 18, 13, 17, 5)),
                BingoBoardRow(listOf(19, 8, 7, 25, 23)),
                BingoBoardRow(listOf(20, 11, 10, 24, 4)),
                BingoBoardRow(listOf(14, 21, 16, 12, 6)),
            )),
            BingoBoardGrid(listOf(
                BingoBoardRow(listOf(14, 21, 17, 24, 4)),
                BingoBoardRow(listOf(10, 16, 15, 9, 19)),
                BingoBoardRow(listOf(18, 8, 23, 26, 20)),
                BingoBoardRow(listOf(22, 11, 13, 6, 5)),
                BingoBoardRow(listOf(2, 0, 12, 3, 7)),
            )))
    )

    fun exampleLineParsing(lines: Iterable<String>): GameInput {
        val lineGroups = sequence {
            val buffer = mutableListOf<String>()
            for (line in lines) {
                if (line.isEmpty()) {
                    if (buffer.any()) {
                        val content = buffer.toList()
                        buffer.clear()
                        yield(content)
                    }
                } else {
                    buffer.add(line)
                }
            }
            if (buffer.any()) {
                yield(buffer.toList())
            }
        }

        val groupList = lineGroups.toList()

        val orderGroup = groupList.first()
        val boardGridGroups = groupList.drop(1)

        if (orderGroup.size != 1) {
            throw IllegalArgumentException("Order line group expected 1 line, got ${orderGroup.size}.")
        }

        val order = parseBingoNumberOrder(orderGroup.first())

        val boardGrids = boardGridGroups
            .map { group -> group.map { parseBingoBoardRow(it) } }
            .map { rows -> BingoBoardGrid(rows) }

        return GameInput(order, boardGrids)
    }

    @Test
    fun `parser result on example input is as expected`() {
        val expected = exampleInput
        val actual = exampleLineParsing(exampleLineInput)

        assertEquals(expected.order, actual.order)

        val expectedCells = expected.boardGrids.map { grid -> grid.rows.map { row -> row.cells } }
        val actualCells = actual.boardGrids.map { grid -> grid.rows.map { row -> row.cells } }
        assertEquals(expectedCells, actualCells)
    }

    @TestFactory
    fun `part1 produces sample results as expected`() = listOf(
        exampleInput to 4512,
        exampleLineParsing(exampleLineInput) to 4512
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            assertEquals(expected, runPart1(input))
        }
    }

    private fun runPart1(input: GameInput) = part1(input.order, input.boardGrids)
    private fun runPart2(input: GameInput) = part2(input.order, input.boardGrids)

    @TestFactory
    fun `part2 produces sample results as expected`() = listOf(
        exampleInput to 1924,
        exampleLineParsing(exampleLineInput) to 1924
    ).map { (input, expected) ->
        dynamicTest("$input to $expected") {
            assertEquals(expected, runPart2(input))
        }
    }

    @Test
    fun `part1 on puzzle input succeeds`() {
        val result = runPart1(puzzleInput)
        println("Result: $result")
    }

    @Test
    fun `part2 on puzzle input succeeds`() {
        val result = runPart2(puzzleInput)
        println("Result: $result")
    }
}
