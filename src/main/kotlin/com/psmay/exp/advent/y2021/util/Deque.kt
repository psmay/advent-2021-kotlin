package com.psmay.exp.advent.y2021.util

interface Deque<T> : Queue<T> {
    fun addFirst(element: T): Boolean
    fun addFirstIfRoom(element: T): Boolean

    fun last(): T
    fun lastOrNull(): T?
    fun lastOrDefault(defaultValue: T): T
    fun lastOrElse(defaultValue: () -> T): T

    fun removeLast(): T
    fun removeLastOrNull(): T?
    fun removeLastOrDefault(defaultValue: T): T
    fun removeLastOrElse(defaultValue: () -> T): T
}

fun <T> emptyDeque(): Deque<T> = MutableListBasedDeque()