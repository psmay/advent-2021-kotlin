package com.psmay.exp.advent.y2021

object Day09 {
    class HeightMapRow {
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
}