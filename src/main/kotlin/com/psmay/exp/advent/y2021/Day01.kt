package com.psmay.exp.advent.y2021


object Day01 {
    fun part1(input: Iterable<Int>) = input
        .zipWithNext() // .windowed(2) actually also works here
        .count { (a, b) -> b > a }

    fun part2(input: Iterable<Int>) = input
        .windowed(3)
        .map { list -> list.sum() }
        .zipWithNext()
        .count { (a, b) -> b > a }
}