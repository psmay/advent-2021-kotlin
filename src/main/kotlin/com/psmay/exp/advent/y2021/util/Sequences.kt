package com.psmay.exp.advent.y2021.util

fun <T> Sequence<T>.takeThroughFirst(predicate: (T) -> Boolean) = this.mapIterator { it.takeThroughFirst(predicate) }

fun <T> Sequence<T>.dropThroughFirst(predicate: (T) -> Boolean) = this.mapIterator { it.dropThroughFirst(predicate) }

fun <T> Sequence<T>.takeWhileThenTake(n: Int, predicate: (T) -> Boolean) =
    this.mapIterator { it.takeWhileThenTake(n, predicate) }

fun <T> Sequence<T>.dropWhileThenDrop(n: Int, predicate: (T) -> Boolean) =
    this.mapIterator { it.dropWhileThenDrop(n, predicate) }