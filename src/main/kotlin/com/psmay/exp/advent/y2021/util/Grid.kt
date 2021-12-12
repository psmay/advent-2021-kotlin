@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.psmay.exp.advent.y2021.util

data class Grid<T>(val rows: List<List<T>>) {
    init {
        rows
            .map { it.size }
            .reduce { rowSize, nextRowSize ->
                if (rowSize == nextRowSize) rowSize
                else throw IllegalArgumentException("Grid rows must all have the same size.")
            }
    }

    val width get() = if (rows.isEmpty()) 0 else rows[0].size
    val height get() = rows.size

    val rowIndices get() = rows.indices
    val columnIndices get() = 0 until width

    fun isInBounds(position: Pair<Int, Int>): Boolean {
        val (columnIndex, rowIndex) = position
        return columnIndices.contains(columnIndex) && rowIndices.contains(rowIndex)
    }

    private fun uncheckedGet(position: Pair<Int, Int>): T {
        val (columnIndex, rowIndex) = position
        return rows[rowIndex][columnIndex]
    }

    operator fun get(position: Pair<Int, Int>): T =
        if (isInBounds(position)) uncheckedGet(position) else throw IndexOutOfBoundsException()

    fun getOrNull(position: Pair<Int, Int>): T? = if (isInBounds(position)) uncheckedGet(position) else null

    fun getOrElse(position: Pair<Int, Int>, defaultValue: (Pair<Int, Int>) -> T): T =
        if (isInBounds(position)) uncheckedGet(position) else defaultValue(position)

    val allPositions
        get() = rowIndices.asSequence()
            .flatMap { rowIndex -> columnIndices.asSequence().map { columnIndex -> (columnIndex to rowIndex) } }

    fun <R> map(transform: (T) -> R) = Grid(rows.map { columns -> columns.map(transform) })

    fun <R> mapIndexed(transform: (Pair<Int, Int>, T) -> R) = Grid(rows.mapIndexed { rowIndex, columns ->
        columns.mapIndexed { columnIndex, cell ->
            transform((columnIndex to rowIndex), cell)
        }
    })
}

fun <T> List<List<T>>.toGrid() = Grid(this)

fun <T> Iterable<Iterable<T>>.toGrid() = Grid(this.map { it.toList() })
