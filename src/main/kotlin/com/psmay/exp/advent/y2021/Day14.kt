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
}