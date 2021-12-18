package com.psmay.exp.advent.y2021.util

fun <T> Sequence<T>.takeThroughFirst(predicate: (T) -> Boolean) = this.mapIterator { it.takeThroughFirst(predicate) }

fun <T> Sequence<T>.dropThroughFirst(predicate: (T) -> Boolean) = this.mapIterator { it.dropThroughFirst(predicate) }
