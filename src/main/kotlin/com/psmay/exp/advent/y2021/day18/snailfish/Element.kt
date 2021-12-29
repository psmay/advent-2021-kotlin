package com.psmay.exp.advent.y2021.day18.snailfish

import com.psmay.exp.advent.y2021.day18.snailfish.Element.Doublet
import com.psmay.exp.advent.y2021.day18.snailfish.Element.Figure

sealed class Element {
    data class Doublet(val x: Element, val y: Element) : Element() {
        override fun toString() = "[$x,$y]"
    }

    data class Figure(val value: Long) : Element() {
        override fun toString() = "$value"
    }

    companion object {
        fun parse(input: String) = Parser.parse(input)
    }
}

infix fun Element.snailTo(y: Element) = Doublet(this, y)
infix fun Element.snailTo(y: Long) = this snailTo Figure(y)
infix fun Element.snailTo(y: Int) = this snailTo y.toLong()

infix fun Long.snailTo(y: Element) = Figure(this) snailTo y
infix fun Long.snailTo(y: Long) = this snailTo Figure(y)
infix fun Long.snailTo(y: Int) = this snailTo y.toLong()

infix fun Int.snailTo(y: Element) = this.toLong() snailTo y
infix fun Int.snailTo(y: Long) = this.toLong() snailTo y
infix fun Int.snailTo(y: Int) = this snailTo y.toLong()