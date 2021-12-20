@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.psmay.exp.advent.y2021.day17

import com.psmay.exp.advent.y2021.util.repeatedForever
import kotlin.math.abs
import kotlin.math.sign

private typealias IntPair = Pair<Int, Int>

private inline val IntPair.x get() = this.first
private inline val IntPair.y get() = this.second

data class TargetArea(val xRange: IntRange, val yRange: IntRange) {
    val bottom get() = yRange.first
    val top get() = yRange.last

    val left get() = xRange.first
    val right get() = xRange.last

    fun contains(position: IntPair): Boolean {
        val (x, y) = position
        return xRange.contains(x) && yRange.contains(y)
    }
}

private val targetAreaRegex = run {
    val number = """(?:-?[0-9]+)"""
    val thru = """(?:\.\.)"""
    val targetAreaPattern = """^target area: x=($number)$thru($number), y=($number)$thru($number)$"""
    targetAreaPattern.toRegex()
}

fun parseTargetAreaOrNull(value: String): TargetArea? {
    val (x0, x1, y0, y1) = targetAreaRegex.matchEntire(value)?.destructured ?: return null
    return TargetArea(x0.toInt()..x1.toInt(), y0.toInt()..y1.toInt())
}

fun parseTargetArea(value: String): TargetArea =
    parseTargetAreaOrNull(value) ?: throw IllegalArgumentException("Input does not match expected target area format.")

private fun Int.compareTo(range: IntRange): Int =
    if (this < range.first) -1 else if (this > range.last) 1 else 0

fun IntPair.compareTo(targetArea: TargetArea): IntPair {
    val (x, y) = this
    return x.compareTo(targetArea.xRange) to y.compareTo(targetArea.yRange)
}

data class SimulatedInstant(val position: IntPair, val velocity: IntPair) {
    val x get() = position.x
    val y get() = position.y
    val dx get() = velocity.x
    val dy get() = velocity.y

    val next: SimulatedInstant
        get() {
            val ddx = -dx.sign // Drag
            val ddy = -1 // Gravity

            return SimulatedInstant(x + dx to y + dy, dx + ddx to dy + ddy)
        }

    val isRising get() = dy > 0
}

fun simulatedInstants(startVelocity: IntPair, startAt: IntPair = (0 to 0)): Sequence<SimulatedInstant> {
    return repeatedForever()
        .runningFold(SimulatedInstant(startAt, startVelocity)) { acc, _ -> acc.next }
}

fun scanForHighestHit(area: TargetArea): Pair<IntPair, IntPair> {
    val simulatedHits = scanForAllHits(area)

    val simulatedArcHighs = simulatedHits.map { (startVelocity, instants) ->
        val topInstant = instants.filter { !it.isRising }.firstOrNull() ?: instants.last()
        (startVelocity to topInstant.position)
    }

    return simulatedArcHighs.maxByOrNull { (_, top) -> top.y }
        ?: throw Exception("We blame the input")
}

fun scanForAllHits(area: TargetArea): Sequence<Pair<IntPair, Sequence<SimulatedInstant>>> {
    val simulations = velocitiesToScan(area).map { startVelocity ->
        // Take number is borrowed from other people's solutions
        (startVelocity to simulatedInstants(startVelocity).take(2 * abs(area.bottom) + 2))
    }

    return simulations.filter { (_, instants) -> instants.any { area.contains(it.position) } }
}

private fun velocitiesToScan(area: TargetArea): Sequence<IntPair> {
    val yTestRange = abs(area.bottom) downTo -abs(area.bottom)
    val xTestRange = 1..area.right + 1

    return yTestRange.asSequence().flatMap { y -> xTestRange.asSequence().map { x -> x to y } }
}