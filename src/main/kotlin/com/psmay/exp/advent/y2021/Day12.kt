@file:Suppress("unused")

package com.psmay.exp.advent.y2021

object Day12 {
    data class CaveNode(val name: String, val isSmall: Boolean) {
        override fun toString() = "<$name>"
    }

    fun String.toCaveNode(): CaveNode {
        val name = this
        val isSmall = when {
            allLowerCaseRegex.matches(name) -> true
            allUpperCaseRegex.matches(name) -> false
            else -> throw IllegalArgumentException("Name '$name' cannot be classified as big or small.")
        }

        return CaveNode(name, isSmall)
    }

    class CaveSystem {

        private var graph: Map<CaveNode, HashSet<CaveNode>>

        constructor(bidirectionalSegments: Sequence<Pair<CaveNode, CaveNode>>) {
            graph = bidirectionalSegments
                .onEach { (a, b) ->
                    if (a == b)
                        throw IllegalArgumentException("Node cannot direct to self.")
                    else if (!a.isSmall && !b.isSmall)
                        throw IllegalArgumentException("Bidirectional path cannot exist between two big nodes.")
                }
                .flatMap { (a, b) -> listOf((a to b), (b to a)) }
                .distinct()
                .groupBy({ (from, _) -> from }, { (_, to) -> to })
                .map { (from, tos) -> from to tos.toHashSet() }
                .associate { it }
        }

        constructor(bidirectionalSegments: Iterable<Pair<CaveNode, CaveNode>>)
                : this(bidirectionalSegments.asSequence())

        fun extendPathByOne(path: List<CaveNode>): List<List<CaveNode>> {
            val nextNodes = run {
                val head = path.last()
                val visited = path.toHashSet()
                val avoid = visited.filter { it.isSmall }.toHashSet()
                val exits = graph[head] ?: emptySet()
                val availableExits = exits - avoid
                availableExits.sortedBy { it.name }
            }
            return nextNodes.map { path + it }
        }

        tailrec fun traversePathsToEnd(paths: List<List<CaveNode>>): List<List<CaveNode>> =
            if (paths.all { it.last() == endNode }) {
                paths
            } else {
                traversePathsToEnd(
                    paths.flatMap { path ->
                        if (path.last() == endNode) {
                            listOf(path)
                        } else {
                            extendPathByOne(path)
                        }
                    })
            }

        fun traverseStartToEnd(): List<List<CaveNode>> =
            traversePathsToEnd(listOf(listOf(startNode)))
    }

    // FIXME: Should be ^[[:upper:]]+$ and ^[[:lower:]]+$, but it was recognizing "h" as neither upper nor lower
    private val allUpperCaseRegex = """^[A-Z]+$""".toRegex()
    private val allLowerCaseRegex = """^[a-z]+$""".toRegex()

    val startNode = "start".toCaveNode()
    val endNode = "end".toCaveNode()
}