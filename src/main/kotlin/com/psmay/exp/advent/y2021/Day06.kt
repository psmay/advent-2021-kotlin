package com.psmay.exp.advent.y2021

object Day06 {

    data class LanternfishClock(val timeToNextRespawn: Int) {
        // Note that this model is equivalent to, but not the same as, the problem description, which
        // always puts the new fish at the end.
        fun ageOneDay(): List<LanternfishClock> {
            val newAge = timeToNextRespawn - 1

            return if (newAge < 0)
                listOf(LanternfishClock(6), LanternfishClock(8))
            else
                listOf(LanternfishClock(newAge))
        }
    }

    private fun ageAllOneDay(clocks: List<LanternfishClock>) = clocks.flatMap { it.ageOneDay() }

    // This looks like a job for tail recursion!
    private tailrec fun ageAll(clocks: List<LanternfishClock>, days: Int): List<LanternfishClock> =
        if (days <= 0) clocks
        else ageAll(ageAllOneDay(clocks), days - 1)

    // Part 2 needs an approach that, ahem, *scales* better. Fortunately, I have an idea.

    private fun age(ageTableInput: Map<Int, Long>, days: Int): Map<Int, Long> {
        data class Counted(val timeLeft: Int, val numberAffected: Long)

        fun ageOneDay(counts: List<Counted>): List<Counted> {
            // For each existing row, produces rows to add to the result.
            val nextPartialRows = counts.map { (currentTimeLeft, numberAffected) ->
                val newTimeLeft = currentTimeLeft - 1
                if (newTimeLeft == -1) {
                    listOf(
                        Counted(6, numberAffected),
                        Counted(8, numberAffected),
                    )
                } else {
                    listOf(Counted(newTimeLeft, numberAffected))
                }
            }.flatten()

            return nextPartialRows
                .groupBy({ (timeLeft, _) -> timeLeft }, { (_, numberAffected) -> numberAffected })
                .map { (timeLeft, numbersAffected) -> Counted(timeLeft, numbersAffected.sum()) }
        }

        tailrec fun age(counts: List<Counted>, days: Int): List<Counted> {
            return if (days <= 0)
                counts
            else
                age(ageOneDay(counts), days - 1)
        }

        if (ageTableInput.keys.any { it < 0 }) {
            throw IllegalArgumentException("Input contains negative timers.")
        }

        val counts = ageTableInput.entries.map { (k, v) -> Counted(k, v) }
        val aged = age(counts, days)
        return aged.associateBy({ it.timeLeft }, { it.numberAffected })
    }

    fun part1(input: List<Int>): Int {
        val clocks = input.map { LanternfishClock(it) }
        val aged = ageAll(clocks, 80)
        return aged.size
    }

    fun part2(input: List<Int>): Long {
        val timerTable = input
            .groupBy { it }
            .map { (k, v) -> k to v.size.toLong() }
            .toMap()

        return age(timerTable, 256).values.sum()
    }
}