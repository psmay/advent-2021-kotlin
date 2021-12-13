package com.psmay.exp.advent.y2021

object Day13 {
    sealed class DotFieldInstruction {
        abstract fun apply(dots: Set<Pair<Int, Int>>): Set<Pair<Int, Int>>

        data class FoldThroughColumn(val columnIndex: Int) : DotFieldInstruction() {
            override fun apply(dots: Set<Pair<Int, Int>>): Set<Pair<Int, Int>> = foldThroughColumn(dots, columnIndex)
        }

        data class FoldThroughRow(val rowIndex: Int) : DotFieldInstruction() {
            override fun apply(dots: Set<Pair<Int, Int>>): Set<Pair<Int, Int>> = foldThroughRow(dots, rowIndex)
        }
    }

    private data class SignumGroups<T>(val less: List<T>, val equal: List<T>, val greater: List<T>)

    // Normalizes result of compareTo() for use in keying
    private fun sgn(n: Int) = if (n < 0) -1 else if (n > 0) 1 else 0

    private fun <T> Iterable<T>.groupBySignum(compareResultSelector: (T) -> Int): SignumGroups<T> {
        val grouped = this.groupBy { sgn(compareResultSelector(it)) }
        return SignumGroups(
            grouped[-1] ?: emptyList(),
            grouped[0] ?: emptyList(),
            grouped[1] ?: emptyList())
    }

    private fun getCutResults(
        dots: Set<Pair<Int, Int>>,
        compare: (Pair<Int, Int>) -> Int,
    ): Pair<Set<Pair<Int, Int>>, Set<Pair<Int, Int>>> {
        val (less, _, greater) = dots.groupBySignum(compare)
        return less.toSet() to greater.toSet()
    }

    /**
     * Removes all dots at the specified column, then returns all dots to the left and right of the column.
     */
    private fun cutThroughColumn(
        dots: Set<Pair<Int, Int>>,
        columnIndex: Int,
    ): Pair<Set<Pair<Int, Int>>, Set<Pair<Int, Int>>> {
        return getCutResults(dots) { (x, _) -> x.compareTo(columnIndex) }
    }

    /**
     * Removes all dots at the specified row, then returns all rows to the top and bottom of the column.
     */
    private fun cutThroughRow(
        dots: Set<Pair<Int, Int>>,
        rowIndex: Int,
    ): Pair<Set<Pair<Int, Int>>, Set<Pair<Int, Int>>> {
        return getCutResults(dots) { (_, y) -> y.compareTo(rowIndex) }
    }

    /**
     * Mirrors all dots through the middle of the specified column; dots on this column remain unchanged.
     */
    private fun mirrorThroughColumn(dots: Set<Pair<Int, Int>>, columnIndex: Int): Set<Pair<Int, Int>> {
        val n = columnIndex * 2
        return dots.map { (x, y) -> (n - x) to y }.toSet()
    }

    /**
     * Mirrors all dots through the middle of the specified row; dots on this row remain unchanged.
     */
    private fun mirrorThroughRow(dots: Set<Pair<Int, Int>>, rowIndex: Int): Set<Pair<Int, Int>> {
        val n = rowIndex * 2
        return dots.map { (x, y) -> x to (n - y) }.toSet()
    }

    private fun foldThroughColumn(dots: Set<Pair<Int, Int>>, columnIndex: Int): Set<Pair<Int, Int>> {
        val (left, right) = cutThroughColumn(dots, columnIndex)
        val flippedRight = mirrorThroughColumn(right, columnIndex)
        return left + flippedRight
    }

    private fun foldThroughRow(dots: Set<Pair<Int, Int>>, rowIndex: Int): Set<Pair<Int, Int>> {
        val (top, bottom) = cutThroughRow(dots, rowIndex)
        val flippedBottom = mirrorThroughRow(bottom, rowIndex)
        return top + flippedBottom
    }
}