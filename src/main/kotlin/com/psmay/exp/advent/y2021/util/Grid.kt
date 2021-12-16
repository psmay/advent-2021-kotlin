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
    val size get() = width * height

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

fun Set<Pair<Int, Int>>.plotToGrid(): Grid<Boolean> {
    val set = this

    return if (set.isEmpty()) {
        Grid(emptyList())
    } else {
        val (minX, maxX) = set.map { (x, _) -> x }.minAndMax()
        val (minY, maxY) = set.map { (_, y) -> y }.minAndMax()
        if (minX < 0 || minY < 0) throw IndexOutOfBoundsException("Negative points cannot be plotted.")

        val rows = (0..maxY).map { rowIndex ->
            (0..maxX).map { columnIndex -> set.contains(columnIndex to rowIndex) }
        }

        Grid(rows)
    }
}

fun Grid<Boolean>.plotToStrings(forFalse: String = ".", forTrue: String = "#"): List<String> {
    return rows.map { row ->
        row.joinToString("") { if (it) forTrue else forFalse }
    }
}

fun <T> Iterable<Grid<T>>.concatenateHorizontally(): Grid<T> {
    val grids = this.toList()
    if (grids.isEmpty()) {
        return Grid(listOf())
    } else if (grids.size == 1) {
        return grids[0]
    }

    run {
        val heights = grids.map { it.height }.distinct()
        if (heights.size > 1) {
            throw IllegalArgumentException("Grids to concatenate must have same height.")
        }
    }

    val rowIndices = grids[0].rowIndices

    val rows = rowIndices.map { rowIndex -> grids.map { grid -> grid.rows[rowIndex] }.flatten() }

    return Grid(rows)
}

fun <T> Iterable<Grid<T>>.concatenateVertically(): Grid<T> {
    val grids = this.toList()
    if (grids.isEmpty()) {
        return Grid(listOf())
    } else if (grids.size == 1) {
        return grids[0]
    }

    run {
        val widths = grids.map { it.width }.distinct()
        if (widths.size > 1) {
            throw IllegalArgumentException("Grids to concatenate must have same width.")
        }
    }

    val rows = grids.flatMap { it.rows }

    return Grid(rows)
}