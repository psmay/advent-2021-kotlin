package com.psmay.exp.advent.y2021

import com.psmay.exp.advent.y2021.util.Grid
import com.psmay.exp.advent.y2021.util.laterallyAdjacentCells

object Day09 {

    // This top part was for part 1. It's written to process a stream of lines and produce a stream of results; it is
    // meant to be suitable for maps with an arbitrarily large number of rows with a reasonable number of columns.

    private class HeightMapRow {
        val knownHeights: List<Int>

        constructor() {
            knownHeights = emptyList()
        }

        constructor(knownHeights: List<Int>) {
            this.knownHeights = knownHeights
        }

        operator fun get(index: Int): Int? {
            return if (knownHeights.indices.contains(index))
                knownHeights[index]
            else
                null
        }
    }

    data class CellWindow(val columnIndex: Int, val rowIndex: Int, val height: Int, val adjacentHeights: List<Int?>) {
        fun isLowPoint() = adjacentHeights.filterNotNull().all { height < it }
    }

    private fun getCellWindowsForRow(rowIndex: Int, rowAbove: HeightMapRow, row: HeightMapRow, rowBelow: HeightMapRow) =
        row.knownHeights.mapIndexed { columnIndex, height ->
            val top = rowAbove[columnIndex]
            val right = row[columnIndex + 1]
            val bottom = rowBelow[columnIndex]
            val left = row[columnIndex - 1]
            CellWindow(columnIndex, rowIndex, height, listOf(top, right, bottom, left))
        }

    private fun getCellWindowsForRows(rows: Sequence<HeightMapRow>): Sequence<CellWindow> {
        val emptyRowSequence = sequenceOf(HeightMapRow())

        // Padding added to allow scanning above the top or below the bottom.
        val padded = emptyRowSequence + rows + emptyRowSequence

        val windowed = padded.windowed(3)

        return windowed.flatMapIndexed { rowIndex, windowRows ->
            val (rowAbove, row, rowBelow) = windowRows
            getCellWindowsForRow(rowIndex, rowAbove, row, rowBelow)
        }
    }

    fun getCellWindows(rows: Sequence<List<Int>>) = getCellWindowsForRows(rows.map { HeightMapRow(it) })

    // This next part expects to hold the entire height map in memory.

    fun Grid<Int>.mapSurroundingBasin(startPosition: Pair<Int, Int>): Set<Pair<Int, Int>> {
        val seen = mutableMapOf<Pair<Int, Int>, Boolean>()
        var nextToProcess = listOf(startPosition)

        while (nextToProcess.any()) {
            val toProcess = nextToProcess.filter { !seen.contains(it) }

            nextToProcess = toProcess.flatMap { pos ->
                val value = isInBasin(pos)
                seen[pos] = value

                if (value)
                    pos.laterallyAdjacentCells()
                else
                    emptyList()
            }
                .distinct()
                .filter { !seen.contains(it) }
        }

        return seen
            .filter { (_, value) -> value }
            .map { (pos, _) -> pos }
            .toSet()
    }

    private fun Grid<Int>.isInBasin(position: Pair<Int, Int>) = (getOrNull(position) ?: Int.MAX_VALUE) < 9
}
