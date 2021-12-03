package com.psmay.exp.advent.y2021

object Day02 {

    enum class Direction(val keyword: String) {
        FORWARD("forward"),
        DOWN("down"),
        UP("up");


        companion object {
            private val byKeyword = values().associateBy({ it.keyword }, { it })

            fun valueOfKeyword(keyword: String): Direction =
                byKeyword.getOrElse(keyword) {
                    throw IllegalArgumentException("'$keyword' is not the name of a valid direction.")
                }

        }
    }

    data class PositionWithAim(val x: Int, val y: Int, val aim: Int)

    data class Instruction(val direction: Direction, val magnitude: Int) {
        fun apply(origin: Pair<Int, Int>): Pair<Int, Int> {
            val (x, y) = origin
            val dx = when (direction) {
                Direction.FORWARD -> magnitude
                else -> 0
            }
            val dy = when (direction) {
                Direction.DOWN -> -magnitude
                Direction.UP -> magnitude
                else -> 0
            }
            return (x + dx) to (y + dy)
        }


        fun apply(origin: PositionWithAim): PositionWithAim {
            val dAim = when (direction) {
                Direction.DOWN -> magnitude
                Direction.UP -> -magnitude
                else -> 0
            }
            val dx = when (direction) {
                Direction.FORWARD -> magnitude
                else -> 0
            }
            val dy = when (direction) {
                Direction.FORWARD -> -origin.aim * magnitude
                else -> 0
            }
            return PositionWithAim(origin.x + dx, origin.y + dy, origin.aim + dAim)
        }


        companion object {
            private val commandFormat = """^(\w+)\s+(\d+)$""".toRegex()
            fun parse(command: String): Instruction {
                val (keywordIn, magnitudeIn) = commandFormat.find(command)?.destructured
                    ?: throw IllegalArgumentException("Instruction '$command' is not in a recognized format.")
                val direction = Direction.valueOfKeyword(keywordIn)
                val magnitude = magnitudeIn.toIntOrNull()
                    ?: throw IllegalArgumentException("$magnitudeIn could not be parsed as an integer.")
                return Instruction(direction, magnitude)
            }
        }
    }

    fun Iterable<Instruction>.applyAll(position: Pair<Int, Int> = Pair(0, 0)) =
        this.fold(position) { origin, instruction -> instruction.apply(origin) }

    fun Iterable<Instruction>.applyAll(position: PositionWithAim) =
        this.fold(position) { origin, instruction -> instruction.apply(origin) }

    fun part1(instructions: Iterable<Instruction>): Int {
        val (x, y) = instructions.applyAll()
        val depth = -y
        return x * depth
    }

    fun part2(instructions: Iterable<Instruction>): Int {
        val (x, y, _) = instructions.applyAll(PositionWithAim(0, 0, 0))
        val depth = -y
        return x * depth
    }
}