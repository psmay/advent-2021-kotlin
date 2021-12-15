@file:Suppress("MemberVisibilityCanBePrivate")

package com.psmay.exp.advent.y2021

import com.psmay.exp.advent.y2021.util.eachLongCount
import com.psmay.exp.advent.y2021.util.repeatedForever

object Day14 {
    data class FormulaElement(val name: String) {
        override fun toString() = "<$name>"
    }

    data class FormulaPairInsertionRule(val pair: FormulaElementPair, val result: FormulaElement)

    fun String.toElement() = FormulaElement(this)
    fun Pair<String, String>.toElements() = this.first.toElement() to this.second.toElement()

    // A real lib would probably have more of these DSLish operators.

    infix fun FormulaElementPair.yields(result: FormulaElement) =
        FormulaPairInsertionRule(this, result)

    infix fun Pair<String, String>.yields(result: String) =
        FormulaPairInsertionRule(this.toElements(), result.toElement())

    fun Iterable<FormulaPairInsertionRule>.toMap() = associateBy({ it.pair }, { it.result })

    private sealed class ProcessingPiece {}
    private data class ElementPiece(val element: FormulaElement) : ProcessingPiece()
    private data class InterPiece(val elements: FormulaElementPair) : ProcessingPiece()

    private fun interleaved(elements: Sequence<FormulaElement>) = elements
        .windowed(2, partialWindows = true)
        .flatMap {
            if (it.size == 2) {
                val (before, after) = it
                listOf(ElementPiece(before), InterPiece(before to after))
            } else {
                val (last) = it
                listOf(ElementPiece(last))
            }
        }

    private fun FormulaElement?.asOptionalList() = if (this == null) emptyList() else listOf(this)

    fun Sequence<FormulaElement>.expanded(rules: FormulaExpansionRules) =
        interleaved(this).flatMap {
            when (it) {
                is ElementPiece -> listOf(it.element)
                is InterPiece -> rules[it.elements].asOptionalList()
            }
        }

    fun Iterable<FormulaElement>.expanded(rules: FormulaExpansionRules) =
        this.asSequence().expanded(rules).toList()

    private const val growthThreshold = 8192

    private abstract class FormulaPart : Sequence<FormulaElement> {
        abstract fun elementSequence(): Sequence<FormulaElement>
        override fun iterator() = elementSequence().iterator()
    }

    private data class ResolvedFormulaPart(val elements: List<FormulaElement>) : FormulaPart() {
        override fun elementSequence() = elements.asSequence()
    }

    private data class UnresolvedFormulaPart(
        val elements: List<FormulaElement>,
        val remainingCount: Int,
        val rules: FormulaExpansionRules,
    ) : FormulaPart() {
        override fun elementSequence() = subdivisions().flatten()

        private fun resolvedOrNull(): ResolvedFormulaPart? {
            return if (remainingCount > 0)
                null
            else
                ResolvedFormulaPart(elements.dropLast(1))
        }

        private fun resolvedOrThis(): FormulaPart = resolvedOrNull() ?: this

        private fun expandedOnce(): UnresolvedFormulaPart {
            return if (remainingCount > 0) {
                UnresolvedFormulaPart(elements.expanded(rules), remainingCount - 1, rules)
            } else {
                this
            }
        }

        private val shouldExpand get() = (remainingCount > 0) && elements.size < growthThreshold

        private fun expandedPastThreshold(): UnresolvedFormulaPart {
            return repeatedForever()
                .runningFold(this) { acc, _ -> acc.expandedOnce() }
                .first { !it.shouldExpand }
        }

        fun advanced() = expandedPastThreshold().resolvedOrThis()

        fun subdivisions(): Sequence<FormulaPart> = if (remainingCount > 0) {
            elements.asSequence()
                .windowed(2)
                .map { UnresolvedFormulaPart(it, remainingCount, rules).advanced() }
        } else {
            sequenceOf(this.resolvedOrThis())
        }
    }

    fun Sequence<FormulaElement>.expanded(rules: FormulaExpansionRules, count: Int): Sequence<FormulaElement> {
        return this.windowed(2, partialWindows = true)
            .flatMap {
                when (it.size) {
                    2 -> UnresolvedFormulaPart(it, count, rules).advanced()
                    1 -> ResolvedFormulaPart(it)
                    else -> throw IllegalStateException()
                }
            }
    }

    // The above was a nice idea, but still not efficient enough. Let's do something with just the counts.

    private sealed class CountingWindow {
        abstract val primary: FormulaElement
        abstract fun expandOnce(rules: FormulaExpansionRules): List<CountingWindow>
    }

    private data class DiatomicWindow(override val primary: FormulaElement, val lookahead: FormulaElement) :
        CountingWindow() {
        override fun expandOnce(rules: FormulaExpansionRules): List<CountingWindow> {
            val newElement = rules[primary to lookahead]
            return if (newElement == null) {
                listOf(this)
            } else {
                listOf(
                    DiatomicWindow(primary, newElement),
                    DiatomicWindow(newElement, lookahead),
                )
            }
        }
    }

    private data class MonoatomicWindow(override val primary: FormulaElement) : CountingWindow() {
        override fun expandOnce(rules: FormulaExpansionRules) = listOf(this)
    }

    private fun <T> List<Pair<T, Long>>.listSums(): List<Pair<T, Long>> =
        groupBy({ it.first }, { it.second })
            .map { (k, v) -> k to v.sum() }

    private fun expandLongCounts(
        entries: List<Pair<CountingWindow, Long>>,
        rules: FormulaExpansionRules,
    ): List<Pair<CountingWindow, Long>> {
        return entries
            .flatMap { (window, count) ->
                window.expandOnce(rules).map { it to count }
            }
            .listSums()
    }

    fun Sequence<FormulaElement>.expandedLongCounts(
        rules: FormulaExpansionRules,
        count: Int,
    ): Map<FormulaElement, Long> {
        val windows = windowed(2, partialWindows = true)
            .map {
                when (it.size) {
                    2 -> {
                        val (a, b) = it
                        DiatomicWindow(a, b)
                    }
                    1 -> {
                        val (a) = it
                        MonoatomicWindow(a)
                    }
                    else -> throw IllegalStateException()
                }
            }

        val windowCounts = run {
            val initialEntries = windows
                .groupingBy { it }
                .eachLongCount()
                .entries
                .map { (k, v) -> k to v }
            (0 until count).fold(initialEntries) { acc, _ -> expandLongCounts(acc, rules) }
        }

        return windowCounts
            .map { (window, count) -> window.primary to count }
            .listSums()
            .associate { it }
    }

    fun Sequence<FormulaElement>.getElementCounts() = this.groupingBy { it }.eachCount()
    fun Iterable<FormulaElement>.getElementCounts() = this.asSequence().getElementCounts()

    fun Sequence<FormulaElement>.getElementLongCounts() = this.groupingBy { it }.eachLongCount()
}

typealias FormulaElementPair = Pair<Day14.FormulaElement, Day14.FormulaElement>
typealias FormulaExpansionRules = Map<FormulaElementPair, Day14.FormulaElement>