package com.psmay.exp.advent.y2021

// This one turned out a little ugly. If it were code I had to live with I'd probably put in a ticket to clean it up.
//
// After part2 was defined, part1 was rewritten to use the same logic. I think I may have done this previously, but
// the change was big enough here to bear mentioning.
object Day04 {

    private const val bingoNumberOrderEntryPattern = """(?:0|[1-9][0-9]*)"""
    private const val bingoNumberOrderLinePattern = "^$bingoNumberOrderEntryPattern(?:,$bingoNumberOrderEntryPattern)*$"
    private val bingoNumberOrderLineRegex = bingoNumberOrderLinePattern.toRegex()

    private const val bingoBoardRowEntryPattern = """(?:[ 1-9][0-9])"""
    private const val bingoBoardRowLinePattern = "^$bingoBoardRowEntryPattern(?: $bingoBoardRowEntryPattern)*$"
    private val bingoBoardRowLineRegex = bingoBoardRowLinePattern.toRegex()

    private val spacesRegex = """\s+""".toRegex()

    private val bingoNumberValidRange = 0..99
    private const val bingoColumnsCount = 5
    private const val bingoRowsCount = 5

    private val allLocations: Set<Pair<Int, Int>>
        get() {
            val allLocations = (0 until bingoRowsCount).flatMap { rowNumber ->
                (0 until bingoColumnsCount).map { columnNumber -> (columnNumber to rowNumber) }
            }.toSet()
            return allLocations
        }

    data class BingoNumberOrder(val numbers: List<Int>) {
        init {
            ensureNumbersAreInRange(numbers)
        }
    }

    data class BingoBoardRow(val cells: List<Int>) {
        init {
            if (cells.size != bingoColumnsCount) {
                throw IllegalArgumentException("Contains ${cells.size} values; exactly $bingoColumnsCount are required.")
            }
            ensureNumbersAreInRange(cells)
            ensureValuesAreUnique(cells)
        }
    }

    data class BingoBoardGrid(val rows: List<BingoBoardRow>) {

        private var locationsToValues: Map<Pair<Int, Int>, Int>
        private var valuesToLocations: Map<Int, Pair<Int, Int>>

        init {
            if (rows.size != bingoRowsCount) {
                throw IllegalArgumentException("Contains ${rows.size} rows; exactly $bingoRowsCount are required.")
            }
            ensureValuesAreUnique(rows.flatMap { it.cells })

            // Value-to-coordinate pairs
            val pairs = rows.flatMapIndexed { rowIndex, row ->
                row.cells.mapIndexed { cellIndex, cellValue ->
                    cellValue to (cellIndex to rowIndex)
                }
            }

            valuesToLocations = pairs.associate { it }
            locationsToValues = pairs.map { (x, y) -> y to x }.associate { it }
        }

        fun valueAt(location: Pair<Int, Int>): Int? {
            ensureLocationIsInRange(location)
            return locationsToValues[location]
        }

        fun find(value: Int): Pair<Int, Int>? {
            ensureNumberIsInRange(value)
            return valuesToLocations[value]
        }
    }

    class BingoBoard(private val grid: BingoBoardGrid) {
        private val marks = mutableSetOf<Pair<Int, Int>>()

        private fun addMark(location: Pair<Int, Int>): Boolean {
            ensureLocationIsInRange(location)
            return marks.add(location)
        }

        fun addMarkForValue(value: Int): Pair<Int, Int>? {
            val location = grid.find(value)
            return if (location != null && addMark(location)) location else null
        }

        fun hasAnyFullColumnOrRow() = (fullColumns().map { true } + fullRows().map { true }).any()

        private fun fullColumns() = (0 until bingoColumnsCount).asSequence().filter { rowIsFull(it) }
        private fun fullRows() = (0 until bingoRowsCount).asSequence().filter { columnIsFull(it) }

        private fun rowIsFull(rowNumber: Int): Boolean {
            ensureRowNumberIsInRange(rowNumber)
            val columnNumbers = (0 until bingoColumnsCount)
            val coordinatesToCheck = columnNumbers.map { it to rowNumber }
            return marks.containsAll(coordinatesToCheck)
        }

        private fun columnIsFull(columnNumber: Int): Boolean {
            ensureRowNumberIsInRange(columnNumber)
            val rowNumbers = (0 until bingoRowsCount)
            val coordinatesToCheck = rowNumbers.map { columnNumber to it }
            return marks.containsAll(coordinatesToCheck)
        }

        private fun getUnmarkedLocations(): Set<Pair<Int, Int>> = allLocations - marks

        fun getBaseScore(): Int {
            return getUnmarkedLocations().sumOf { grid.valueAt(it) ?: 0 }
        }
    }

    fun parseBingoNumberOrder(input: String): BingoNumberOrder {
        if (looksLikeBingoNumberOrderLine(input)) {
            val entries = input
                .split(",")
                .map { it.toInt(10) }
                .toList()
            return BingoNumberOrder(entries)
        } else {
            throw IllegalArgumentException("Values are not in expected format.")
        }
    }

    private fun looksLikeBingoNumberOrderLine(input: String) = bingoNumberOrderLineRegex.matches(input)

    fun parseBingoBoardRow(input: String): BingoBoardRow {
        if (looksLikeBingoBoardRowLine(input)) {
            val splitResults = input.trim()
                .split(spacesRegex)
            val entries = splitResults
                .map { it.toInt(10) }
                .toList()

            return BingoBoardRow(entries)
        } else {
            throw IllegalArgumentException("Values are not in expected format.")
        }
    }

    private fun looksLikeBingoBoardRowLine(input: String) = bingoBoardRowLineRegex.matches(input)

    private fun ensureNumberIsInRange(value: Int) {
        if (!bingoNumberValidRange.contains(value)) {
            throw IndexOutOfBoundsException("Value $value is not in valid range: $bingoNumberValidRange")
        }
    }

    private fun ensureNumbersAreInRange(values: List<Int>) {
        val outOfRangeValues = values
            .filter { !bingoNumberValidRange.contains(it) }
            .toList()

        if (outOfRangeValues.any()) {
            throw IndexOutOfBoundsException("Contains values outside valid range: $outOfRangeValues")
        }
    }

    private fun ensureRowNumberIsInRange(rowNumber: Int) {
        val range = 0 until bingoRowsCount
        if (!range.contains(rowNumber))
            throw IndexOutOfBoundsException("Row number $rowNumber is not in valid range: $range")
    }

    private fun ensureColumnNumberIsInRange(columnNumber: Int) {
        val range = 0 until bingoColumnsCount
        if (!range.contains(columnNumber))
            throw IndexOutOfBoundsException("Column number $columnNumber is not in valid range: $range")
    }

    private fun ensureLocationIsInRange(location: Pair<Int, Int>) {
        val (columnNumber, rowNumber) = location
        ensureColumnNumberIsInRange(columnNumber)
        ensureRowNumberIsInRange(rowNumber)
    }

    private fun ensureValuesAreUnique(values: List<Int>) {
        val valuesSequence = values.asSequence()

        val duplicateEntries = valuesSequence
            .groupBy { it }
            .map { (value, matches) -> value to matches.size }
            .filter { (_, count) -> count > 1 }
            .map { (value, _) -> value }
            .sortedBy { it }
            .toList()

        if (duplicateEntries.any()) {
            throw IndexOutOfBoundsException("Contains duplicate entries: $duplicateEntries")
        }
    }

    data class MultiPlayResult(val lastCalledNumber: Int, val winningBoards: List<BingoBoard>) {
        fun toSingleResults() = winningBoards.map { PlayResult(lastCalledNumber, it) }
    }

    data class PlayResult(val lastCalledNumber: Int, val winningBoard: BingoBoard)

    private fun playBoards(order: BingoNumberOrder, inboards: List<BingoBoard>): Sequence<MultiPlayResult> {
        if (order.numbers.isEmpty()) throw IllegalArgumentException("Called number sequence cannot be empty.")

        data class Play(val index: Int, val board: BingoBoard)

        val plays = inboards.mapIndexed { index, board -> Play(index, board) }.toMutableSet()

        return sequence {
            for (calledNumber in order.numbers) {
                if (plays.isEmpty()) {
                    break
                }

                for (play in plays) {
                    play.board.addMarkForValue(calledNumber)
                }

                val winners = plays
                    .filter { it.board.hasAnyFullColumnOrRow() }
                    .sortedBy { it.index }

                val winningBoards = winners.map { it.board }

                if (winningBoards.any()) {
                    yield(MultiPlayResult(calledNumber, winningBoards))
                }

                plays.removeAll(winners.toSet())
            }
        }
    }

    fun part1(order: BingoNumberOrder, boardGrids: List<BingoBoardGrid>): Int {
        val boards = boardGrids.map { BingoBoard(it) }

        val firstWinSet = playBoards(order, boards).firstOrNull()
            ?: throw IllegalArgumentException("There are no boards that win.")

        val bestWinsWithBaseScores = firstWinSet
            .toSingleResults()
            .map { it to it.winningBoard.getBaseScore() }
            .sortedByDescending { (_, baseScore) -> baseScore }

        val (playResult, baseScore) = bestWinsWithBaseScores.first()

        return baseScore * playResult.lastCalledNumber
    }

    fun part2(order: BingoNumberOrder, boardGrids: List<BingoBoardGrid>): Int {
        val boards = boardGrids.map { BingoBoard(it) }

        val lastWinSet = playBoards(order, boards).lastOrNull()
            ?: throw IllegalArgumentException("There are no boards that win.")

        val worstWinsWithBaseScores = lastWinSet
            .toSingleResults()
            .map { it to it.winningBoard.getBaseScore() }
            .sortedBy { (_, baseScore) -> baseScore }

        val (playResult, baseScore) = worstWinsWithBaseScores.first()

        return baseScore * playResult.lastCalledNumber
    }
}


