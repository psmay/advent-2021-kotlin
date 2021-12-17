package com.psmay.exp.advent.y2021.day15

import com.psmay.exp.advent.y2021.util.Grid
import com.psmay.exp.advent.y2021.util.laterallyAdjacentCells
import com.psmay.exp.advent.y2021.util.repeatedForever

typealias IntPoint = Pair<Int, Int>

fun Grid<Int>.findPath() = this.findPath(0 to 0, this.width - 1 to this.height - 1)

fun Grid<Int>.findPath(startAt: IntPoint, endAt: IntPoint): Sequence<IntPoint> {
    val grid = this
    val distanceFromStartTo = mutableMapOf<IntPoint, Int>() // missing will represent infinity
    val previousStepFrom = mutableMapOf<IntPoint, IntPoint>() // missing will represent undefined
    distanceFromStartTo[startAt] = 0

    val unseen = mutableSetOf<IntPoint>()

    unseen.addAll(grid.allPositions)

    while (unseen.isNotEmpty()) {
        val (u, _) = distanceFromStartTo
            .filter { (vertex, _) -> unseen.contains(vertex) }
            .minByOrNull { (_, cost) -> cost }!!

        unseen.remove(u)

        val neighbors = u.laterallyAdjacentCells()
            .filter { unseen.contains(it) }

        for (v in neighbors) {
            val alt = distanceFromStartTo[u]!! + grid[v]

            val distV = distanceFromStartTo[v]
            if (distV == null || (alt < distV)) {
                distanceFromStartTo[v] = alt
                previousStepFrom[v] = u
            }
        }
    }

    val path = repeatedForever()
        .runningFold(endAt as IntPoint?) { acc, _ -> previousStepFrom[acc] }
        .takeWhile { it != null }
        .filterNotNull()
        .toList()
        .reversed()

    return path.asSequence()
}