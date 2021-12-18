package com.psmay.exp.advent.y2021.util

fun <T> Iterator<T>.listNext() = if (hasNext()) listOf(next()) else emptyList()
fun <T> Iterator<T>.nextOrNull() = if (hasNext()) next() else null
fun <T> Iterator<T>.nextOrElse(defaultValue: () -> T) = if (hasNext()) next() else defaultValue()