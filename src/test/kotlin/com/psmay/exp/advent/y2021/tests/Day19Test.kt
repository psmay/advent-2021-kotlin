package com.psmay.exp.advent.y2021.tests

import com.psmay.exp.advent.y2021.tests.helpers.UseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.asUseLinesSource
import com.psmay.exp.advent.y2021.tests.helpers.getTextLineSource
import com.psmay.exp.advent.y2021.util.PeekingIterator
import com.psmay.exp.advent.y2021.util.withPeeking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal typealias LongTriple = Triple<Long, Long, Long>
internal typealias ScannerPair = Pair<Int, List<LongTriple>>
internal typealias ScannerMap = Map<Int, List<LongTriple>>

internal class Day19Test {
    private fun pos(a: Long, b: Long, c: Long) = Triple(a, b, c)
    private fun scanner(key: Int, positions: List<Triple<Long, Long, Long>>) = key to positions

    private val exampleInput1 = listOf(
        scanner(0, listOf(
            pos(404, -588, -901),
            pos(528, -643, 409),
            pos(-838, 591, 734),
            pos(390, -675, -793),
            pos(-537, -823, -458),
            pos(-485, -357, 347),
            pos(-345, -311, 381),
            pos(-661, -816, -575),
            pos(-876, 649, 763),
            pos(-618, -824, -621),
            pos(553, 345, -567),
            pos(474, 580, 667),
            pos(-447, -329, 318),
            pos(-584, 868, -557),
            pos(544, -627, -890),
            pos(564, 392, -477),
            pos(455, 729, 728),
            pos(-892, 524, 684),
            pos(-689, 845, -530),
            pos(423, -701, 434),
            pos(7, -33, -71),
            pos(630, 319, -379),
            pos(443, 580, 662),
            pos(-789, 900, -551),
            pos(459, -707, 401),
        )),
        scanner(1, listOf(
            pos(686, 422, 578),
            pos(605, 423, 415),
            pos(515, 917, -361),
            pos(-336, 658, 858),
            pos(95, 138, 22),
            pos(-476, 619, 847),
            pos(-340, -569, -846),
            pos(567, -361, 727),
            pos(-460, 603, -452),
            pos(669, -402, 600),
            pos(729, 430, 532),
            pos(-500, -761, 534),
            pos(-322, 571, 750),
            pos(-466, -666, -811),
            pos(-429, -592, 574),
            pos(-355, 545, -477),
            pos(703, -491, -529),
            pos(-328, -685, 520),
            pos(413, 935, -424),
            pos(-391, 539, -444),
            pos(586, -435, 557),
            pos(-364, -763, -893),
            pos(807, -499, -711),
            pos(755, -354, -619),
            pos(553, 889, -390),
        )),
        scanner(2, listOf(
            pos(649, 640, 665),
            pos(682, -795, 504),
            pos(-784, 533, -524),
            pos(-644, 584, -595),
            pos(-588, -843, 648),
            pos(-30, 6, 44),
            pos(-674, 560, 763),
            pos(500, 723, -460),
            pos(609, 671, -379),
            pos(-555, -800, 653),
            pos(-675, -892, -343),
            pos(697, -426, -610),
            pos(578, 704, 681),
            pos(493, 664, -388),
            pos(-671, -858, 530),
            pos(-667, 343, 800),
            pos(571, -461, -707),
            pos(-138, -166, 112),
            pos(-889, 563, -600),
            pos(646, -828, 498),
            pos(640, 759, 510),
            pos(-630, 509, 768),
            pos(-681, -892, -333),
            pos(673, -379, -804),
            pos(-742, -814, -386),
            pos(577, -820, 562),
        )),
        scanner(3, listOf(
            pos(-589, 542, 597),
            pos(605, -692, 669),
            pos(-500, 565, -823),
            pos(-660, 373, 557),
            pos(-458, -679, -417),
            pos(-488, 449, 543),
            pos(-626, 468, -788),
            pos(338, -750, -386),
            pos(528, -832, -391),
            pos(562, -778, 733),
            pos(-938, -730, 414),
            pos(543, 643, -506),
            pos(-524, 371, -870),
            pos(407, 773, 750),
            pos(-104, 29, 83),
            pos(378, -903, -323),
            pos(-778, -728, 485),
            pos(426, 699, 580),
            pos(-438, -605, -362),
            pos(-469, -447, -387),
            pos(509, 732, 623),
            pos(647, 635, -688),
            pos(-868, -804, 481),
            pos(614, -800, 639),
            pos(595, 780, -596),
        )),
        scanner(4, listOf(
            pos(727, 592, 562),
            pos(-293, -554, 779),
            pos(441, 611, -461),
            pos(-714, 465, -776),
            pos(-743, 427, -804),
            pos(-660, -479, -426),
            pos(832, -632, 460),
            pos(927, -485, -438),
            pos(408, 393, -506),
            pos(466, 436, -512),
            pos(110, 16, 151),
            pos(-258, -428, 682),
            pos(-393, 719, 612),
            pos(-211, -452, 876),
            pos(808, -476, -593),
            pos(-575, 615, 604),
            pos(-485, 667, 467),
            pos(-680, 325, -822),
            pos(-627, -443, -432),
            pos(872, -547, -609),
            pos(833, 512, 582),
            pos(807, 604, 487),
            pos(839, -516, 451),
            pos(891, -625, 532),
            pos(-652, -548, -490),
            pos(30, -46, -14),
        ))
    ).associate { it }

    private val exampleRawInput1 = run {
        sequenceOf(
            "--- scanner 0 ---",
            "404,-588,-901",
            "528,-643,409",
            "-838,591,734",
            "390,-675,-793",
            "-537,-823,-458",
            "-485,-357,347",
            "-345,-311,381",
            "-661,-816,-575",
            "-876,649,763",
            "-618,-824,-621",
            "553,345,-567",
            "474,580,667",
            "-447,-329,318",
            "-584,868,-557",
            "544,-627,-890",
            "564,392,-477",
            "455,729,728",
            "-892,524,684",
            "-689,845,-530",
            "423,-701,434",
            "7,-33,-71",
            "630,319,-379",
            "443,580,662",
            "-789,900,-551",
            "459,-707,401",
            "",
            "--- scanner 1 ---",
            "686,422,578",
            "605,423,415",
            "515,917,-361",
            "-336,658,858",
            "95,138,22",
            "-476,619,847",
            "-340,-569,-846",
            "567,-361,727",
            "-460,603,-452",
            "669,-402,600",
            "729,430,532",
            "-500,-761,534",
            "-322,571,750",
            "-466,-666,-811",
            "-429,-592,574",
            "-355,545,-477",
            "703,-491,-529",
            "-328,-685,520",
            "413,935,-424",
            "-391,539,-444",
            "586,-435,557",
            "-364,-763,-893",
            "807,-499,-711",
            "755,-354,-619",
            "553,889,-390",
            "",
            "--- scanner 2 ---",
            "649,640,665",
            "682,-795,504",
            "-784,533,-524",
            "-644,584,-595",
            "-588,-843,648",
            "-30,6,44",
            "-674,560,763",
            "500,723,-460",
            "609,671,-379",
            "-555,-800,653",
            "-675,-892,-343",
            "697,-426,-610",
            "578,704,681",
            "493,664,-388",
            "-671,-858,530",
            "-667,343,800",
            "571,-461,-707",
            "-138,-166,112",
            "-889,563,-600",
            "646,-828,498",
            "640,759,510",
            "-630,509,768",
            "-681,-892,-333",
            "673,-379,-804",
            "-742,-814,-386",
            "577,-820,562",
            "",
            "--- scanner 3 ---",
            "-589,542,597",
            "605,-692,669",
            "-500,565,-823",
            "-660,373,557",
            "-458,-679,-417",
            "-488,449,543",
            "-626,468,-788",
            "338,-750,-386",
            "528,-832,-391",
            "562,-778,733",
            "-938,-730,414",
            "543,643,-506",
            "-524,371,-870",
            "407,773,750",
            "-104,29,83",
            "378,-903,-323",
            "-778,-728,485",
            "426,699,580",
            "-438,-605,-362",
            "-469,-447,-387",
            "509,732,623",
            "647,635,-688",
            "-868,-804,481",
            "614,-800,639",
            "595,780,-596",
            "",
            "--- scanner 4 ---",
            "727,592,562",
            "-293,-554,779",
            "441,611,-461",
            "-714,465,-776",
            "-743,427,-804",
            "-660,-479,-426",
            "832,-632,460",
            "927,-485,-438",
            "408,393,-506",
            "466,436,-512",
            "110,16,151",
            "-258,-428,682",
            "-393,719,612",
            "-211,-452,876",
            "808,-476,-593",
            "-575,615,604",
            "-485,667,467",
            "-680,325,-822",
            "-627,-443,-432",
            "872,-547,-609",
            "833,512,582",
            "807,604,487",
            "839,-516,451",
            "891,-625,532",
            "-652,-548,-490",
            "30,-46,-14",
        ).asUseLinesSource()
    }

    data class TestCase(
        val exampleRawInput: UseLinesSource,
        val exampleInput: ScannerMap,
        val part1Result: Long,
        val part2Result: Long,
    )

    private val testCases = listOf(
        TestCase(exampleRawInput1, exampleInput1, 79, -1),
    )

    private val puzzleRawInput = getTextLineSource("y2021/Day19Input")

    private sealed class InputLine {
        data class Heading(val key: Int) : InputLine()
        data class Position(val position: LongTriple) : InputLine()
        object Blank : InputLine()

        companion object {
            private val HeadingRegex = """^--- scanner ([0-9]+) ---""".toRegex()
            private val PositionRegex = run {
                val element = """(-?[0-9]+)"""
                "^$element,$element,$element$".toRegex()
            }
            private val BlankRegex = """^$""".toRegex()

            private fun matchHeadingOrNull(line: String): Heading? {
                val (key) = HeadingRegex.matchEntire(line)?.destructured ?: return null
                return Heading(key.toInt())
            }

            private fun matchPositionOrNull(line: String): Position? {
                val (x, y, z) = PositionRegex.matchEntire(line)?.destructured ?: return null
                return Position(Triple(x.toLong(), y.toLong(), z.toLong()))
            }

            private fun matchBlankOrNull(line: String): Blank? {
                BlankRegex.matchEntire(line) ?: return null
                return Blank
            }

            private fun matchOrNull(line: String) =
                matchHeadingOrNull(line) ?: matchPositionOrNull(line) ?: matchBlankOrNull(line)

            fun parse(line: String) =
                matchOrNull(line) ?: throw IllegalArgumentException("Line format is invalid: '$line'")
        }
    }

    private fun parseAll(lines: Sequence<String>): ScannerMap {
        return lines
            .map { InputLine.parse(it) }
            .withPeeking()
            .peekingIterator()
            .parseScanners()
            .associate { it }
    }

    private fun PeekingIterator<InputLine>.parseScanners() = sequence {
        while (hasNext()) {
            when (val next = peek()) {
                is InputLine.Heading -> {
                    val key = (next() as InputLine.Heading).key
                    val positions = mutableListOf<LongTriple>()
                    while (hasNext() && peek() is InputLine.Position) {
                        val position = (next() as InputLine.Position).position
                        positions.add(position)
                    }
                    yield(key to positions.toList())
                }
                is InputLine.Blank -> {
                    next()
                }
                else -> {
                    throw IllegalStateException("Expected heading or blank line; found $next.")
                }
            }
        }
    }

    private fun part1(input: ScannerMap): Long {
        return 0
    }

    private fun part2(input: ScannerMap): Long {
        return 0
    }

    @TestFactory
    fun `raw input cooks to correct cooked form`() = testCases.map { (actualRaw, expected, _, _) ->
        dynamicTest("$actualRaw to $expected") {
            val result = actualRaw.useLines { lines -> parseAll(lines) }
            Assertions.assertEquals(expected.toList(), result.toList())
        }
    }

    @TestFactory
    fun `part1 produces sample results as expected`() = testCases.map { (input, _, expected, _) ->
        dynamicTest("$input to $expected") {
            val result = input.useLines { lines -> part1(parseAll(lines)) }
            Assertions.assertEquals(expected, result)
        }
    }

    @TestFactory
    fun `part2 produces sample results as expected`() = testCases.map { (input, _, _, expected) ->
        dynamicTest("$input to $expected") {
            val result = input.useLines { lines -> part2(parseAll(lines)) }
            Assertions.assertEquals(expected, result)
        }
    }

    @Test
    fun `part1 on puzzle input succeeds`() {
        val result = puzzleRawInput.useLines { lines -> part1(parseAll(lines)) }
        println("Result: $result")
    }

    @Test
    fun `part2 on puzzle input succeeds`() {
        val result = puzzleRawInput.useLines { lines -> part2(parseAll(lines)) }
        println("Result: $result")
    }
}