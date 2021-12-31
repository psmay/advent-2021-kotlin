package com.psmay.exp.advent.y2021.day19

internal typealias Cell3d = Triple<Long, Long, Long>
internal val Cell3d.x get() = this.first
internal val Cell3d.y get() = this.second
internal val Cell3d.z get() = this.third