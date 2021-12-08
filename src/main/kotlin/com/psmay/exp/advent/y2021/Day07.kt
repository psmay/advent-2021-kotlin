package com.psmay.exp.advent.y2021

import kotlin.math.abs

object Day07 {
    data class Itinerary(val destination: Int, val cost: Int)

    fun findCostInitialFormula(destination: Int, positions: List<Int>) =
        positions.sumOf { abs(destination - it) }

    fun findCostNewFormula(destination: Int, positions: List<Int>) =
        positions.sumOf { triangle(abs(destination - it)) }

    fun findBestItineraries(positions: List<Int>, findCostTo: (Int, List<Int>) -> Int): List<Itinerary> {
        val (low, high) = positions.minAndMaxOrNull() ?: throw NoSuchElementException()
        val trials = (low..high).map { Itinerary(it, findCostTo(it, positions)) }
        val bestCost = trials.minOf { it.cost }
        val bestTrials = trials.filter { it.cost == bestCost }
        return bestTrials.sortedBy { it.destination }
    }
}
