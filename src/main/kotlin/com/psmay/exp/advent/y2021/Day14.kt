@file:Suppress("MemberVisibilityCanBePrivate")

package com.psmay.exp.advent.y2021

object Day14 {
    data class FormulaElement(val name: String)
    data class FormulaPairInsertionRule(val pair: Pair<FormulaElement, FormulaElement>, val result: FormulaElement)

    fun String.toElement() = FormulaElement(this)
    fun Pair<String, String>.toElements() = this.first.toElement() to this.second.toElement()

    // A real lib would probably have more of these DSLish operators.

    infix fun Pair<FormulaElement, FormulaElement>.yields(result: FormulaElement) =
        FormulaPairInsertionRule(this, result)

    infix fun Pair<String, String>.yields(result: String) =
        FormulaPairInsertionRule(this.toElements(), result.toElement())

    fun Iterable<FormulaPairInsertionRule>.toMap() = associateBy({ it.pair }, { it.result })

    private sealed class ProcessingPiece {}
    private data class ElementPiece(val element: FormulaElement) : ProcessingPiece()
    private data class InterPiece(val elements: Pair<FormulaElement, FormulaElement>) : ProcessingPiece()

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

    fun Sequence<FormulaElement>.expanded(rules: Map<Pair<FormulaElement, FormulaElement>, FormulaElement>) =
        interleaved(this).flatMap {
            when (it) {
                is ElementPiece -> listOf(it.element)
                is InterPiece -> rules[it.elements].asOptionalList()
            }
        }

    fun Iterable<FormulaElement>.expanded(rules: Map<Pair<FormulaElement, FormulaElement>, FormulaElement>) =
        this.asSequence().expanded(rules).toList()

    fun Sequence<FormulaElement>.getElementCounts() = this.groupingBy { it }.eachCount()
    fun Iterable<FormulaElement>.getElementCounts() = this.asSequence().getElementCounts()
}