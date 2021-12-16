package com.psmay.exp.advent.y2021.day15

import com.psmay.exp.advent.y2021.util.Grid
import com.psmay.exp.advent.y2021.util.laterallyAdjacentCells
import com.psmay.exp.advent.y2021.util.repeatedForever

typealias IntPoint = Pair<Int, Int>

private data class RouteEdge(val from: IntPoint, val via: IntPoint, val totalScore: Int) {
    val stop get() = from == via
}

fun Grid<Int>.findPath(): Sequence<IntPoint> = this.findPath(0 to 0, this.width - 1 to this.height - 1)

fun Grid<Int>.findPath(startAt: IntPoint, endAt: IntPoint): Sequence<IntPoint> {
    val grid = this

    if (!grid.isInBounds(startAt) || !grid.isInBounds(endAt)) {
        throw IndexOutOfBoundsException()
    }

    val seen = mutableMapOf<IntPoint, RouteEdge>()

    seen[endAt] = RouteEdge(endAt, endAt, grid[endAt])

    tailrec fun evaluateCosts(currentPoints: Set<IntPoint>) {
        if (currentPoints.isEmpty()) {
            // do nothing
        } else {
            val newRoutePieces = currentPoints.flatMap { viaPoint ->
                val seenPiece = seen[viaPoint]!!
                viaPoint.laterallyAdjacentCells()
                    .filter { grid.isInBounds(it) }
                    .filter { !seen.containsKey(it) }
                    .map { RouteEdge(it, viaPoint, grid[it] + seenPiece.totalScore) }
            }
                .groupBy { it.from }.entries
                .map { (fromPoint, it) -> fromPoint to it.minByOrNull { it.totalScore }!! }
                .associate { it }

            seen.putAll(newRoutePieces)
            evaluateCosts(newRoutePieces.keys)
        }
    }

    evaluateCosts(setOf(endAt))

    val trace = repeatedForever()
        .runningFold(false to seen[startAt]!!) { (_, edge), _ -> edge.stop to seen[edge.via]!! }
        .takeWhile { (stop, _) -> !stop }
        .map { (_, edge) -> edge }

    return trace.map { it.from }
}

