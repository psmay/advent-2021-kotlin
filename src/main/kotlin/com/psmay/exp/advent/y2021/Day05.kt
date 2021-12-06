package com.psmay.exp.advent.y2021

import kotlin.math.abs

object Day05 {

    data class Point(val x: Int, val y: Int)

    private fun sgn(n: Int) = if (n < 0) -1 else if (n > 0) 1 else 0

    fun forwardRange(a: Int, b: Int) = if (a > b) b..a else a..b

    data class Line(val start: Point, val end: Point) {
        val isHorizontal get() = deltaY == 0
        val isVertical get() = deltaX == 0
        val isDiagonal get() = !(isHorizontal || isVertical)
        val isIn45DegreeStep get() = abs(deltaX) == abs(deltaY)

        val deltaX = (end.x - start.x)
        val deltaY = (end.y - start.y)

        fun getCoveredPoints(): List<Point> {
            return if (isVertical) {
                val range = forwardRange(start.y, end.y)
                range.map { y -> Point(start.x, y) }
            } else if (isHorizontal) {
                val range = forwardRange(start.x, end.x)
                range.map { x -> Point(x, start.y) }
            } else if (isIn45DegreeStep) {
                // "a..b step s" syntax doesn't allow negative increments
                val xRange = IntProgression.fromClosedRange(start.x, end.x, sgn(deltaX))
                val yRange = IntProgression.fromClosedRange(start.y, end.y, sgn(deltaY))
                xRange.zip(yRange).map { pair -> pair.toPoint() }
            } else {
                throw IllegalStateException("Line $this is diagonal and does not fit in a 45-degree step.")
            }
        }
    }

    fun Pair<Int, Int>.toPoint(): Point {
        val (x, y) = this
        return Point(x, y)
    }

    fun Pair<Pair<Int, Int>, Pair<Int, Int>>.toLine(): Line {
        val (pair0, pair1) = this
        return Line(pair0.toPoint(), pair1.toPoint())
    }

    fun part1(lineDefinitions: Sequence<Pair<Pair<Int, Int>, Pair<Int, Int>>>): Int {
        val lines = lineDefinitions
            .map { it.toLine() }
            .filter { !it.isDiagonal }

        val hits = lines.flatMap { it.getCoveredPoints() }

        val countedHits = hits
            .groupBy { it }
            .map { (point, instances) -> point to instances.size }

        val overlappedPoints = countedHits
            .filter { (_, hitCount) -> hitCount >= 2 }
            .map { (point, _) -> point }

        return overlappedPoints.size
    }

    fun part2(lineDefinitions: Sequence<Pair<Pair<Int, Int>, Pair<Int, Int>>>): Int {
        val lines = lineDefinitions.map { it.toLine() }

        val hits = lines.flatMap { it.getCoveredPoints() }

        val countedHits = hits
            .groupBy { it }
            .map { (point, instances) -> point to instances.size }

        val overlappedPoints = countedHits
            .filter { (_, hitCount) -> hitCount >= 2 }
            .map { (point, _) -> point }

        return overlappedPoints.size
    }
}



