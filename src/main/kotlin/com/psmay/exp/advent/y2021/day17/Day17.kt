package com.psmay.exp.advent.y2021.day17

data class TargetArea(val xRange: IntRange, val yRange: IntRange)

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