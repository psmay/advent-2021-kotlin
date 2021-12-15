package com.psmay.exp.advent.y2021.util

interface Stack<T> : Collection<T> {
    fun addLast(element: T): Boolean
    fun addLastIfRoom(element: T): Boolean

    fun last(): T
    fun lastOrNull(): T?
    fun lastOrDefault(defaultValue: T): T
    fun lastOrElse(defaultValue: () -> T): T

    fun removeLast(): T
    fun removeLastOrNull(): T?
    fun removeLastOrDefault(defaultValue: T): T
    fun removeLastOrElse(defaultValue: () -> T): T
}

fun <T> emptyStack(): Stack<T> = MutableListBasedDeque()