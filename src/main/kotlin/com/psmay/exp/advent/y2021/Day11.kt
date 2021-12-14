package com.psmay.exp.advent.y2021

import com.psmay.exp.advent.y2021.util.Grid
import com.psmay.exp.advent.y2021.util.adjacentCells
import com.psmay.exp.advent.y2021.util.repeatedForever

object Day11 {
    data class StepResult(val grid: Grid<Int>, val flashCount: Int)

    private fun getFlashPositions(grid: Grid<Int>) = grid
        .allPositions
        .filter { position -> grid[position] == 10 }

    private tailrec fun processFlashes(grid: Grid<Int>, usedFlashPositions: Set<Pair<Int, Int>>): StepResult {
        val flashPositionsThisPass = getFlashPositions(grid) - usedFlashPositions
        val changedThisPass = flashPositionsThisPass.any()

        val updatedGrid = if (changedThisPass) {
            val affectedPositions = flashPositionsThisPass
                .flatMap { it.adjacentCells() }
                .groupingBy { it }
                .eachCount()

            grid
                .mapIndexed { position, value ->
                    val countToAdd = affectedPositions.getOrDefault(position, 0)
                    (value + countToAdd).coerceAtMost(10)
                }
        } else {
            grid
        }

        return if (changedThisPass) {
            processFlashes(updatedGrid, usedFlashPositions + flashPositionsThisPass)
        } else {
            StepResult(grid, usedFlashPositions.size)
        }
    }

    fun step(grid: Grid<Int>): StepResult {
        val incrementedGrid = grid.map { (it + 1).coerceAtMost(10) }
        val (flashedGrid, flashCount) = processFlashes(incrementedGrid, emptySet())
        val flashResetGrid = flashedGrid.map { if (it >= 10) 0 else it }
        return StepResult(flashResetGrid, flashCount)
    }

    fun run(grid: Grid<Int>, count: Int) = run(grid).take(count + 1)

    fun run(grid: Grid<Int>): Sequence<StepResult> {
        val initial = StepResult(grid, 0)
        return repeatedForever().runningFold(initial) { (grid, _), _ -> step(grid) }
    }
}