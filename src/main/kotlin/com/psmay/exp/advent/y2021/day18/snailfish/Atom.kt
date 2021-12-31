package com.psmay.exp.advent.y2021.day18.snailfish

import com.psmay.exp.advent.y2021.util.mapIterator
import com.psmay.exp.advent.y2021.util.splice
import com.psmay.exp.advent.y2021.util.withPeeking

internal sealed class Atom {
    open val depthChange: Int get() = 0

    object DescendAtom : Atom() {
        override val depthChange get() = 1
    }

    object AscendAtom : Atom() {
        override val depthChange get() = -1
    }

    data class FigureAtom(val figure: Element.Figure) : Atom()
}

internal fun Element.getAtoms(): Sequence<Atom> = when (this) {
    is Element.Figure -> sequenceOf(Atom.FigureAtom(this))
    is Element.Doublet -> sequence {
        val doublet = this@getAtoms
        yield(Atom.DescendAtom)
        yieldAll(doublet.x.getAtoms())
        yieldAll(doublet.y.getAtoms())
        yield(Atom.AscendAtom)
    }
}

internal fun Sequence<Atom>.getElements(): Sequence<Element> {
    return mapIterator { it.withPeeking().pullZeroOrMoreElements().iterator() }
}

private fun Sequence<Atom>.indexOfExplodablePair(): Int {
    data class WindowResult(val depthAfterFirst: Int, val windowIsExplodable: Boolean)

    val windowResults = windowed(4)
        .runningFold(WindowResult(0, false)) { (depth, _), window ->
            val (a, b, c, d) = window
            val depthAfterFirst = depth + a.depthChange
            val windowIsExplodable = depthAfterFirst >= 5 &&
                    a is Atom.DescendAtom &&
                    b is Atom.FigureAtom &&
                    c is Atom.FigureAtom &&
                    d is Atom.AscendAtom
            WindowResult(depthAfterFirst, windowIsExplodable)
        }
        .drop(1) // Skip the synthetic first result

    return windowResults.indexOfFirst { it.windowIsExplodable }
}

private fun Sequence<Atom>.indexOfSplittableFigure(): Int {
    return indexOfFirst { it is Atom.FigureAtom && it.figure.value >= 10 }
}

private fun MutableList<Atom>.explode(): Boolean {
    val foundIndexOfPair = this.asSequence().indexOfExplodablePair()

    if (foundIndexOfPair < 0) {
        return false
    }

    // Known indices
    val indicesOfPair = foundIndexOfPair..foundIndexOfPair + 3

    val indexOfXAtom = indicesOfPair.first + 1
    val indexOfYAtom = indicesOfPair.first + 2
    val indicesOfWAtom = scanForFigureAtom((indicesOfPair.first - 1 downTo 0), indicesOfPair.first)
    val indicesOfZAtom = scanForFigureAtom((indicesOfPair.last + 1..lastIndex), indicesOfPair.last + 1)

    val wAtoms = this.slice(indicesOfWAtom).map { it as Atom.FigureAtom }
    val xAtom = this[indexOfXAtom] as Atom.FigureAtom
    val yAtom = this[indexOfYAtom] as Atom.FigureAtom
    val zAtoms = this.slice(indicesOfZAtom).map { it as Atom.FigureAtom }

    val replacementWAtoms = wAtoms.map { wAtom -> sumOfFigureAtoms(wAtom, xAtom) }
    val replacementZAtoms = zAtoms.map { zAtom -> sumOfFigureAtoms(yAtom, zAtom) }
    val replacementAtomForPair = Atom.FigureAtom(Element.Figure(0))

    this.splice(indicesOfWAtom, replacementWAtoms)
    this.splice(indicesOfZAtom, replacementZAtoms)
    this.splice(indicesOfPair, listOf(replacementAtomForPair))

    return true
}

private fun sumOfFigureAtoms(a: Atom.FigureAtom, b: Atom.FigureAtom): Atom.FigureAtom {
    return Atom.FigureAtom(Element.Figure(a.figure.value + b.figure.value))
}

private fun splitFigureAtom(atom: Atom.FigureAtom): List<Atom> {
    val value = atom.figure.value
    val halfRoundedDown = value / 2
    val halfRoundedUp = value - halfRoundedDown
    return listOf(
        Atom.DescendAtom,
        Atom.FigureAtom(Element.Figure(halfRoundedDown)),
        Atom.FigureAtom(Element.Figure(halfRoundedUp)),
        Atom.AscendAtom,
    )
}

private fun MutableList<Atom>.scanForFigureAtom(indicesToScan: Iterable<Int>, emptyIndexPosition: Int): IntRange {
    val foundIndex = indicesToScan.firstOrNull { index -> this[index] is Atom.FigureAtom }

    return if (foundIndex == null) // result is an empty range
        emptyIndexPosition until emptyIndexPosition
    else // result is a single-element range
        foundIndex..foundIndex
}

private fun MutableList<Atom>.split(): Boolean {
    val index = this.asSequence().indexOfSplittableFigure()

    if (index < 0) {
        return false
    }

    val indicesToReplace = index..index
    val splitResult = splitFigureAtom(this[index] as Atom.FigureAtom)
    this.splice(indicesToReplace, splitResult)
    return true
}

private fun MutableList<Atom>.snailReduceOnce() = this.explode() || this.split()

internal fun MutableList<Atom>.snailReduce(): Boolean {
    val changed = this.snailReduceOnce()

    if (changed) {
        while (this.snailReduceOnce()) {
            // continue
        }
    }

    return changed
}