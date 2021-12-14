package com.psmay.exp.advent.y2021.tests.helpers

import java.io.File
import java.io.Reader

fun getTextFile(name: String) = File("src/test/resources", "$name.txt")

fun getTextLineSource(name: String) = getTextFile(name).asUseLinesSource()

interface UseLinesSource {
    fun <T> useLines(block: (Sequence<String>) -> T): T
}

fun File.asUseLinesSource(): UseLinesSource {
    val file = this
    return object : UseLinesSource {
        override fun <T> useLines(block: (Sequence<String>) -> T): T = file.useLines(block = block)
    }
}

fun Reader.asUseLinesSource(): UseLinesSource {
    val reader = this
    return object : UseLinesSource {
        override fun <T> useLines(block: (Sequence<String>) -> T): T = reader.useLines(block)
    }
}

fun Sequence<String>.asUseLinesSource(): UseLinesSource {
    val sequence = this
    return object : UseLinesSource {
        override fun <T> useLines(block: (Sequence<String>) -> T): T = block(sequence)
    }
}

fun Iterable<String>.asUseLinesSource(): UseLinesSource {
    val iterable = this
    return object : UseLinesSource {
        override fun <T> useLines(block: (Sequence<String>) -> T): T = block(iterable.asSequence())
    }
}

fun Sequence<String>.splitAtEmptyLines(keepEmptyGroups: Boolean = false): Sequence<List<String>> {
    val lines = this

    return sequence {
        val buffer = mutableListOf<String>()

        fun flush(): List<List<String>> =
            if (keepEmptyGroups || buffer.isNotEmpty()) {
                val block = buffer.toList()
                buffer.clear()
                listOf(block)
            } else {
                emptyList()
            }

        for (line in lines) {
            if (line.isEmpty()) {
                yieldAll(flush())
            } else {
                buffer.add(line)
            }
        }
        yieldAll(flush())
    }
}