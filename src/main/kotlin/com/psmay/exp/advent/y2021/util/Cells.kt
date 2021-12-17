@file:Suppress("unused")

package com.psmay.exp.advent.y2021.util

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
    val (ax, ay) = this
    val (bx, by) = other
    return (ax + bx) to (ay + by)
}

val Pair<Int, Int>.up get() = this + (0 to -1)
val Pair<Int, Int>.down get() = this + (0 to 1)
val Pair<Int, Int>.left get() = this + (-1 to 0)
val Pair<Int, Int>.right get() = this + (1 to 0)

val Pair<Int, Int>.upLeft get() = this + (-1 to -1)
val Pair<Int, Int>.upRight get() = this + (1 to -1)
val Pair<Int, Int>.downLeft get() = this + (-1 to 1)
val Pair<Int, Int>.downRight get() = this + (1 to 1)

fun Pair<Int, Int>.adjacentCells(): List<Pair<Int, Int>> = listOf(
    (0 to -1),
    (1 to -1),
    (1 to 0),
    (1 to 1),
    (0 to 1),
    (-1 to 1),
    (-1 to 0),
    (-1 to -1),
).map { it + this }

fun Pair<Int, Int>.laterallyAdjacentCells(): List<Pair<Int, Int>> = listOf(
    (0 to -1),
    (1 to 0),
    (0 to 1),
    (-1 to 0),
).map { it + this }

fun Pair<Int, Int>.diagonallyAdjacentCells(): List<Pair<Int, Int>> = listOf(
    (1 to -1),
    (1 to 1),
    (-1 to 1),
    (-1 to -1),
).map { it + this }