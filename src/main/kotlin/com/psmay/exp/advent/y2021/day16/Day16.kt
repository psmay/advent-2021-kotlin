package com.psmay.exp.advent.y2021.day16

import com.psmay.exp.advent.y2021.util.emptyQueue
import com.psmay.exp.advent.y2021.util.mapIterator
import kotlin.math.max
import kotlin.math.min

private typealias Biterator = Iterator<Boolean>
private typealias LongReduce = (Long, Long) -> Long

fun Sequence<Int>.parseNybbleBitsInputToTokens() = nybblesToBits().parseBitsInput()

fun Sequence<BitsOutputToken>.parseTokensToPackets() = this.mapIterator { TokenSource(it).packetSequence().iterator() }

fun Sequence<Boolean>.parseBitsInput() = mapIterator { it.parseBitsInput().iterator() }

//fun <T> List<T>.getOrElse(index: Int, defaultValue: (Int) -> T): T {

// Header(packet type = LITERAL_VALUE) Literal Footer
// Header(packet type = other) (Packet Length in Bits/Subpackets) (Packet*) Footer

sealed class Packet {
    abstract val header: Header
    abstract fun evaluateAsLong(): Long
}

private data class LiteralPacket(override val header: Header, val literal: Literal) : Packet() {
    override fun evaluateAsLong(): Long = literal.toLong()
}

private data class BinaryOperationPacket(
    override val header: Header,
    val operation: PacketOperation,
    val a: Packet,
    val b: Packet,
) : Packet() {
    override fun evaluateAsLong(): Long = operation.operate(a.evaluateAsLong(), b.evaluateAsLong())
}

private data class VariadicOperationPacket(
    override val header: Header,
    val operation: PacketOperation,
    val operands: List<Packet>,
) : Packet() {
    override fun evaluateAsLong(): Long = operation.operate(operands.map { it.evaluateAsLong() })
}

private class TokenSource(val iterator: Iterator<BitsOutputToken>) {
    private val buffer = emptyQueue<BitsOutputToken>()

    fun ready(): Boolean = if (buffer.isNotEmpty()) {
        true
    } else if (iterator.hasNext()) {
        buffer.addLast(iterator.next())
        true
    } else {
        false
    }

    fun peek(): BitsOutputToken? = if (ready()) buffer.first() else null

    fun take(): BitsOutputToken =
        if (ready()) buffer.removeFirst() else throw IllegalStateException("Reached end of input before packet ended.")

    fun takeHeader(): Header {
        val token = take()
        return token as? Header ?: throw IllegalStateException("Expected header; read $token")
    }

    fun takeLiteral(): Literal {
        val token = take()
        return token as? Literal ?: throw IllegalStateException("Expected literal; read $token")
    }

    fun discardPacketLength() {
        val token = take()
        if (token !is PacketLengthInBits && token !is PacketLengthInSubpackets) {
            throw IllegalStateException("Expected packet length; read $token")
        }
    }

    fun takeFooter(): Footer {
        val token = take()
        return token as? Footer ?: throw IllegalStateException("Expected footer; read $token")
    }

    fun peekFooter(): Boolean {
        val token = peek()
        return (token != null) && (token is Footer)
    }

    fun packetSequence() = sequence {
        while (ready()) {
            yield(collectOnePacket())
        }
    }

    fun collectOnePacket(): Packet {
        val header = takeHeader()

        if (header.type == PacketType.LITERAL_VALUE) {
            val literal = takeLiteral()
            takeFooter()

            return LiteralPacket(header, literal)
        } else {
            val operation = header.type.operation!!
            discardPacketLength()

            val subpackets = mutableListOf<Packet>()
            while (ready() && !peekFooter()) {
                subpackets.add(collectOnePacket())
            }

            takeFooter()

            return if (operation.binaryOnly) {
                if (subpackets.size != 2) {
                    throw IllegalStateException("Operation for ${header.type} requires exactly 2 operands; ${subpackets.size} operand(s) were found.")
                }
                val (a, b) = subpackets
                BinaryOperationPacket(header, operation, a, b)
            } else {
                VariadicOperationPacket(header, operation, subpackets.toList())
            }
        }
    }
}

private fun Biterator.takeOnePacket(): Sequence<TokenWithLength> = sequence {
    var bitsRead = 0
    val yieldable = mutableListOf<TokenWithLength>()

    fun flush(): List<TokenWithLength> {
        val contents = yieldable.toList()
        yieldable.clear()
        return contents
    }

    fun <T : BitsOutputToken> put(read: InternalWithLength<T>): T {
        val (length, token) = read
        bitsRead += length
        yieldable.add(read.toOutput())
        return token
    }

    val header = put(readHeader())
    yieldAll(flush())

    if (header.type == PacketType.LITERAL_VALUE) {
        put(readLiteral())
        yieldAll(flush())
    } else {
        val token = put(readLengthTypeAndLength())
        yieldAll(flush())

        when (token) {
            is PacketLengthInBits -> {

                var bitsRemaining = token.length

                while (bitsRemaining > 0) {
                    val packetSequence = takeOnePacket()
                    for (item in packetSequence) {
                        val (length, _) = item
                        yield(item)
                        bitsRead += length
                        bitsRemaining -= length
                        if (bitsRemaining < 0) {
                            throw IllegalStateException("Subpacket length has been succeeded.")
                        }
                    }
                }
            }
            is PacketLengthInSubpackets -> {

                var subpacketsRemaining = token.length

                while (subpacketsRemaining > 0) {
                    val packetSequence = takeOnePacket()
                    for (item in packetSequence) {
                        val (length, _) = item
                        yield(item)
                        bitsRead += length
                    }
                    --subpacketsRemaining
                }
            }
            else -> throw IllegalStateException("Expected a packet length token.")
        }
    }

    yield(Footer.withLength(0).toOutput())
}

fun Biterator.parseBitsInput() = takeOnePacket().map { (_, token) -> token }

private fun Int.truncateToNybble() = this and 0xF
private fun Int.canBeNybble() = (truncateToNybble() == this)
private fun Int.ensureIsNybble(): Int {
    if (!canBeNybble()) {
        throw IllegalArgumentException("Converting this value to 4-bit would lose information.")
    }
    return this
}

private fun Int.nybbleToBits(): List<Boolean> {
    ensureIsNybble()
    return listOf(
        (this and 0x8 != 0),
        (this and 0x4 != 0),
        (this and 0x2 != 0),
        (this and 0x1 != 0),
    )
}

private fun Sequence<Int>.nybblesToBits() = this.flatMap { it.nybbleToBits() }

private fun Int.nybbleToHex(): Char {
    return ensureIsNybble().toString(16).uppercase()[0]
}

private fun Biterator.pullBoolean(): Boolean {
    if (!hasNext()) throw NoSuchElementException()
    return next()
}

private fun Biterator.pullBit() = if (pullBoolean()) 1 else 0

private fun Biterator.pullInt(count: Int): Int {
    var result = 0
    (0 until count).forEach { _ ->
        result = (result shl 1) or pullBit()
    }
    return result
}

// Non-blank, no sign, no leading zero, all upper-case.
private val allowedHexDigitsPattern = """^(?:0|[1-9A-F][0-9A-F]*)$""".toRegex()

sealed class BitsOutputToken {}

data class Header(val version: Int, val typeId: Int) : BitsOutputToken()
object Footer : BitsOutputToken() {}

@Suppress("unused")
data class Literal(val hexDigits: String) : BitsOutputToken() {
    init {
        require(allowedHexDigitsPattern.matches(hexDigits))
    }

    fun toInt() = hexDigits.toInt(16)
    fun toLong() = hexDigits.toLong(16)

    fun fitsInInt() = hexDigits == toInt().toString(16).uppercase()
    fun fitsInLong() = hexDigits == toLong().toString(16).uppercase()
}

data class PacketLengthInBits(val length: Int) : BitsOutputToken()
data class PacketLengthInSubpackets(val length: Int) : BitsOutputToken()

private val Header.type get() = PacketType.valueOfId(typeId)

private fun LengthType.toToken(length: Int): BitsOutputToken = when (this) {
    LengthType.LENGTH_BITS -> PacketLengthInBits(length)
    LengthType.LENGTH_SUBPACKETS -> PacketLengthInSubpackets(length)
}

private data class LiteralGroup(val more: Boolean, val nybble: Int)

private data class TokenWithLength(val length: Int, val token: BitsOutputToken)
private data class InternalWithLength<T>(val length: Int, val payload: T)

private fun <T, R> InternalWithLength<T>.mapPayload(transform: (T) -> R): InternalWithLength<R> {
    return InternalWithLength(length, transform(payload))
}

private fun <T> T.withLength(length: Int) = InternalWithLength(length, this)
private fun <T : BitsOutputToken> InternalWithLength<T>.toOutput() = TokenWithLength(length, payload)

private fun <T> Sequence<InternalWithLength<T>>.total(): InternalWithLength<List<T>> {
    var totalLength = 0
    val payloads = mutableListOf<T>()

    for (item in this) {
        totalLength += item.length
        payloads += item.payload
    }

    return payloads.toList().withLength(totalLength)
}

private fun <T, R> Sequence<InternalWithLength<T>>.total(transform: (List<T>) -> R): InternalWithLength<R> =
    total().mapPayload(transform)

private fun Biterator.readHeader(): InternalWithLength<Header> {
    val version = pullInt(3)
    val type = pullInt(3)
    return Header(version, type).withLength(6)
}

private fun Biterator.readLiteralGroup(): InternalWithLength<LiteralGroup> {
    val more = pullBoolean()
    val nybble = pullInt(4)
    return LiteralGroup(more, nybble).withLength(5)
}

private fun Biterator.readLiteral(): InternalWithLength<Literal> {
    return readLiteralGroupsUntilEnd()
        .map { it.mapPayload { g -> g.nybble.nybbleToHex() } }
        .total { Literal(hexDigitsToString(it)) }
}

private fun Biterator.readLiteralGroupsUntilEnd(): Sequence<InternalWithLength<LiteralGroup>> {
    return sequence {
        var more = true
        while (more) {
            val group = readLiteralGroup()
            yield(group)
            more = group.payload.more
        }
    }
}

private fun Biterator.readLengthType(): InternalWithLength<LengthType> {
    val bit = pullBit()
    val lengthType = LengthType.valueOfId(bit)
    return lengthType.withLength(1)
}

private fun Biterator.readLength(type: LengthType): InternalWithLength<Int> {
    val size = type.size
    val n = pullInt(size)
    return n.withLength(size)
}

private fun Biterator.readLengthTypeAndLength(): InternalWithLength<BitsOutputToken> {
    val (lengthTypeLength, lengthType) = readLengthType()
    val (lengthLength, payload) = readLength(lengthType)
    return lengthType
        .toToken(payload)
        .withLength(lengthTypeLength + lengthLength)
}

private fun hexDigitsToString(chars: List<Char>): String {
    return chars
        .dropWhile { it == '0' } // exclude leading zeros
        .toCharArray()
        .concatToString()
        .ifEmpty { "0" } // if the string was all zeros, prevent it from being blank
}

private class PacketOperation(val binaryOnly: Boolean, val reduceOperation: LongReduce) {
    fun operate(a: Long, b: Long) = reduceOperation(a, b)
    fun operate(operands: Iterable<Long>): Long {
        if (binaryOnly) {
            throw IllegalStateException("This operation cannot be applied to an iterable.")
        }
        return operands.reduce(reduceOperation)
    }
}

@Suppress("unused")
private enum class PacketType(val id: Int, val operation: PacketOperation?) {
    SUM(0, PacketOperation(false) { a, b -> a + b }),
    PRODUCT(1, PacketOperation(false) { a, b -> a * b }),
    MINIMUM(2, PacketOperation(false) { a, b -> min(a, b) }),
    MAXIMUM(3, PacketOperation(false) { a, b -> max(a, b) }),
    LITERAL_VALUE(4, null),
    GREATER_THAN(5, PacketOperation(true) { a, b -> if (a > b) 1 else 0 }),
    LESS_THAN(6, PacketOperation(true) { a, b -> if (a < b) 1 else 0 }),
    EQUAL_TO(7, PacketOperation(true) { a, b -> if (a == b) 1 else 0 });

    companion object {
        private val byId = values().associateBy({ it.id }, { it })

        fun valueOfId(id: Int) =
            byId[id] ?: throw IllegalArgumentException("'$id' is not a valid packet type id.")

        fun valueOfIdOrNull(id: Int) = byId[id]
    }
}

@Suppress("unused")
private enum class LengthType(val id: Int, val size: Int) {
    LENGTH_BITS(0, 15), // Length is given in bits in immediate 15-bit value
    LENGTH_SUBPACKETS(1, 11); // Length is given in subpacket count in immediate 11-bit value

    companion object {
        private val byId = values().associateBy({ it.id }, { it })

        fun valueOfId(id: Int) =
            byId[id] ?: throw IllegalArgumentException("'$id' is not a valid length type id.")

        fun valueOfIdOrNull(id: Int) = byId[id]
    }
}