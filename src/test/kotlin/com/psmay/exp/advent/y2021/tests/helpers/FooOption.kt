package com.psmay.exp.advent.y2021.tests.helpers

// This is a perfectly serviceable start to a complete Option class, exploiting unionish behavior in Kotlin. While Opt
// takes a different approach that is theoretically more efficient, an implementation like this is straightforward and
// can be used to test Opt itself, which has its own subtleties and isn't necessarily as clear.

internal sealed class FooOption<T> {
    abstract fun isEmpty(): Boolean
    abstract val value: T
}

internal data class FooSome<T>(override val value: T) : FooOption<T>() {
    override fun isEmpty() = false
    override fun equals(other: Any?): Boolean = if (other is FooSome<*>) value == other.value else false
    override fun hashCode() = value.hashCode()
}

internal object FooNone : FooOption<Nothing>() {
    override fun isEmpty() = true
    override val value: Nothing get() = throw Exception("PROGRAMMER ERROR - Tried to get value from FooNone")
    override fun equals(other: Any?): Boolean = other is FooNone
    override fun hashCode(): Int = -1
}