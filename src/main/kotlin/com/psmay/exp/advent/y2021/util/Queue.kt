package com.psmay.exp.advent.y2021.util

interface Queue<T> : Collection<T> {
    fun addLast(element: T): Boolean
    fun addLastIfRoom(element: T): Boolean

    fun first(): T
    fun firstOrNull(): T?
    fun firstOrDefault(defaultValue: T): T
    fun firstOrElse(defaultValue: () -> T): T

    fun removeFirst(): T
    fun removeFirstOrNull(): T?
    fun removeFirstOrDefault(defaultValue: T): T
    fun removeFirstOrElse(defaultValue: () -> T): T
}

fun <T> emptyQueue(): Queue<T> = MutableListBasedDeque()