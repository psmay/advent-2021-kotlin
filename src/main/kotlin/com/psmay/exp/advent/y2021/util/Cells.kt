package com.psmay.exp.advent.y2021.util

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
    val (ax, ay) = this
    val (bx, by) = other
    return (ax + bx) to (ay + by)
}

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