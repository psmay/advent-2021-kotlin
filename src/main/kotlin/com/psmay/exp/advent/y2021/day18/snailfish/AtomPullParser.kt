package com.psmay.exp.advent.y2021.day18.snailfish

import com.psmay.exp.advent.y2021.util.PeekingIterator

internal typealias AtomSource = PeekingIterator<Atom>

internal fun AtomSource.pullZeroOrMoreElements() = sequence {
    while (hasNext()) {
        yield(pullElement())
    }
}

private fun AtomSource.pullElement(): Element =
    when (val atom = peek()) {
        is Atom.DescendAtom -> pullDoublet()
        is Atom.FigureAtom -> pullFigure()
        else -> throw IllegalStateException("Expected figure atom or descend atom; found $atom.")
    }

private fun AtomSource.pullDoublet(): Element.Doublet {
    this.discardDescendAtom()
    val x = pullElement()
    val y = pullElement()
    this.discardAscendAtom()
    return Element.Doublet(x, y)
}

private fun AtomSource.pullFigure(): Element.Figure = pullFigureAtom().figure

private fun AtomSource.discardDescendAtom() {
    check(next() is Atom.DescendAtom) { "Expected descend atom; found ${next()}." }
}

private fun AtomSource.discardAscendAtom() {
    check(next() is Atom.AscendAtom) { "Expected ascend atom; found ${next()}." }
}

private fun AtomSource.pullFigureAtom(): Atom.FigureAtom =
    when (val atom = next()) {
        is Atom.FigureAtom -> atom
        else -> throw IllegalStateException("Expected figure atom; found $atom.")
    }