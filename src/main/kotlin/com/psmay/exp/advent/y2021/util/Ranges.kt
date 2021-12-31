package com.psmay.exp.advent.y2021.util

/** Returns whether this range is contained within the specified bounds. */
fun IntRange.isInBounds(bounds: IntRange): Boolean = this.first >= bounds.first && this.last <= bounds.last

/** Throws if this range is not contained within the specified bounds. */
fun IntRange.ensureInBounds(bounds: IntRange) {
    if (!isInBounds(bounds)) throw IndexOutOfBoundsException()
}

/** Returns the number of discrete elements in this range. */
fun IntRange.count(): Int = if (isEmpty()) 0 else last - first + 1
