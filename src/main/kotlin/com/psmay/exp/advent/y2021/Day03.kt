package com.psmay.exp.advent.y2021

object Day03 {

    private fun gammaAndEpsilonBits(comparedCountsPerColumn: List<Int>): Pair<List<Boolean>, List<Boolean>> {
        val gammaBits = gammaBits(comparedCountsPerColumn)
        val epsilonBits = gammaBits.map { !it }
        return gammaBits to epsilonBits
    }

    private fun gammaBits(comparedCountsPerColumn: List<Int>): List<Boolean> =
        comparedCountsPerColumn.mapIndexed() { i, x ->
            if (x < 0) false
            else if (x > 0) true
            else throw IllegalArgumentException("Column at index $i has an equal number of true and false values.")
        }

    private fun compareCountsPerColumn(columns: List<List<Boolean>>): List<Int> = columns.map { column ->
        val (trueCount, falseCount) = counts(column)
        trueCount.compareTo(falseCount)
    }

    private fun counts(column: List<Boolean>): Pair<Int, Int> {
        val counts = column
            .groupBy { it }
            .map { (k, v) -> k to v.size }
            .associate { it }
        val trueCount = counts[true] ?: 0
        val falseCount = counts[false] ?: 0
        return trueCount to falseCount
    }

    private fun mostCommon(input: List<Boolean>): Boolean? {
        val (trueCount, falseCount) = counts(input)
        val compared = trueCount.compareTo(falseCount)
        return if (compared == 0) null else (compared > 0)
    }

    private fun leastCommon(input: List<Boolean>): Boolean? {
        val (trueCount, falseCount) = counts(input)
        val compared = trueCount.compareTo(falseCount)
        return if (compared == 0) null else (compared < 0)
    }

    private fun applyBitCriteria(
        rows: List<List<Boolean>>,
        getPreferredValue: (List<Boolean>) -> Boolean,
    ): List<Boolean> {
        var remainingRows = rows
        val columnCount = rows.minOfOrNull { it.size } ?: 0

        // NB: If I were confident that I could get it to work without too many tries, this would probably be a swell
        // place for a tail recursion.

        for (columnIndex in 0 until columnCount) {
            val column = remainingRows.map { it[columnIndex] }
            val preferredValue = getPreferredValue(column)
            remainingRows = remainingRows.filter { it[columnIndex] == preferredValue }
            if (remainingRows.count() == 1) {
                return remainingRows[0]
            }
        }

        throw IllegalStateException("Bit criteria filtering resulted in a number of lines other than 1 (${remainingRows.size}).")
    }

    private fun applyOxygenGeneratorRatingBitCriteria(rows: List<List<Boolean>>) =
        applyBitCriteria(rows) { mostCommon(it) ?: true }

    private fun applyCo2ScrubberRatingBitCriteria(rows: List<List<Boolean>>) =
        applyBitCriteria(rows) { leastCommon(it) ?: false }

    private fun parseBinary(input: List<Boolean>) = input
        .joinToString("") { if (it) "1" else "0" }
        .toInt(2)

    fun part1(input: List<List<Boolean>>): Int {
        val columns = input.transpose()

        val comparedCountsPerColumn = compareCountsPerColumn(columns)

        val (gammaBits, epsilonBits) = gammaAndEpsilonBits(comparedCountsPerColumn)
        val gamma = parseBinary(gammaBits)
        val epsilon = parseBinary(epsilonBits)

        return gamma * epsilon
    }

    fun part2(input: List<List<Boolean>>): Int {
        val oxygenGeneratorRatingBits = applyOxygenGeneratorRatingBitCriteria(input)
        val co2ScrubberRatingBits = applyCo2ScrubberRatingBitCriteria(input)

        val oxygenGeneratorRating = parseBinary(oxygenGeneratorRatingBits)
        val co2ScrubberRating = parseBinary(co2ScrubberRatingBits)

        return oxygenGeneratorRating * co2ScrubberRating
    }
}