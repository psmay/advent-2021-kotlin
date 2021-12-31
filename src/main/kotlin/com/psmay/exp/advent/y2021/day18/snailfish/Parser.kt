package com.psmay.exp.advent.y2021.day18.snailfish

import com.psmay.exp.advent.y2021.day18.snailfish.Element.Doublet
import com.psmay.exp.advent.y2021.day18.snailfish.Element.Figure
import com.psmay.exp.advent.y2021.day18.snailfish.Parser.Token.*
import com.psmay.exp.advent.y2021.util.PeekingIterator
import com.psmay.exp.advent.y2021.util.nextOrElse
import com.psmay.exp.advent.y2021.util.peekOrElse
import com.psmay.exp.advent.y2021.util.withPeeking

internal object Parser {
    sealed class Token {
        abstract val text: String

        data class StartBracketToken(override val text: String) : Token()
        data class EndBracketToken(override val text: String) : Token()
        data class CommaToken(override val text: String) : Token()
        data class FigureToken(override val text: String) : Token() {
            fun toLong() = text.toLong()
        }

        data class InvalidCharacterToken(override val text: String) : Token()
    }

    private var tokenRegex = """\G(?:(\[)|(])|(,)|(-?[0-9]+)|(.))""".toRegex()

    private fun tokenize(input: String): Sequence<Token> {
        return tokenRegex.findAll(input).map { matchResultToToken(it) }
    }

    private fun matchResultToToken(m: MatchResult): Token {
        fun <T> MatchGroup?.packInto(transform: (String) -> T): T? = if (this == null) null else transform(this.value)

        return listOfNotNull(
            m.groups[1].packInto { StartBracketToken(it) },
            m.groups[2].packInto { EndBracketToken(it) },
            m.groups[3].packInto { CommaToken(it) },
            m.groups[4].packInto { FigureToken(it) },
            m.groups[5].packInto { InvalidCharacterToken(it) }
        ).first()
    }

    private fun errorInputEnded() = IllegalStateException("Input ended before read completed.")
    private fun errorExpected(expected: String, found: String) =
        IllegalStateException("Expected $expected; found $found")

    private fun PeekingIterator<Token>.pullNextToken(): Token {
        val token = this.nextOrElse { throw errorInputEnded() }
        if (token is InvalidCharacterToken) {
            throw IllegalStateException("Input '${token.text}' is not allowed here.")
        }
        return token
    }

    private fun PeekingIterator<Token>.discardStartBracket() {
        val token = pullNextToken()
        if (token !is StartBracketToken) throw errorExpected("start bracket", "$token")
    }

    private fun PeekingIterator<Token>.discardEndBracket() {
        val token = pullNextToken()
        if (token !is EndBracketToken) throw errorExpected("end bracket", "$token")
    }

    private fun PeekingIterator<Token>.discardComma() {
        val token = pullNextToken()
        if (token !is CommaToken) throw errorExpected("end bracket", "$token")
    }

    private fun PeekingIterator<Token>.pullFigureToken(): FigureToken {
        val token = pullNextToken()
        if (token !is FigureToken) {
            throw errorExpected("figure", "$token")
        } else {
            return token
        }
    }

    private fun PeekingIterator<Token>.pullOneGroupOrFigure(): Element {
        val firstToken = this.peekOrElse { throw errorInputEnded() }
        return when (firstToken) {
            is StartBracketToken -> pullOneGroup()
            is FigureToken -> pullOneFigure()
            else -> throw errorExpected("start bracket or figure", "$firstToken")
        }
    }

    private fun PeekingIterator<Token>.pullOneFigure(): Figure {
        val figureToken = pullFigureToken()
        return Figure(figureToken.text.toLong())
    }

    private fun PeekingIterator<Token>.pullOneGroup(): Doublet {
        discardStartBracket()
        val firstElement = pullOneGroupOrFigure()
        discardComma()
        val secondElement = pullOneGroupOrFigure()
        discardEndBracket()
        return Doublet(firstElement, secondElement)
    }

    private fun PeekingIterator<Token>.parseTopLevel(): Element {
        if (!hasNext()) {
            throw errorInputEnded()
        }

        val element = pullOneGroupOrFigure()

        if (hasNext()) {
            val token = next()
            throw IllegalStateException("Trailing characters appeared after read (starting with '${token.text}'.")
        }

        return element
    }

    fun parse(input: String): Element {
        return tokenize(input).withPeeking().peekingIterator().parseTopLevel()
    }
}
