@file:Suppress("unused")

package com.psmay.exp.advent.y2021

import com.psmay.exp.advent.y2021.util.asSet

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

        private var graph: Map<CaveNode, Set<CaveNode>>

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

        private fun availableExitsUsingInitialLogic(path: List<CaveNode>): Set<CaveNode> {
            val head = path.last()
            val visited = path.toHashSet().asSet()
            val avoid = visited.filter { it.isSmall }.toHashSet().asSet()
            val exits = graph[head] ?: emptySet()
            return exits - avoid
        }

        private fun availableExitsUsingNewLogic(path: List<CaveNode>): Set<CaveNode> {
            val head = path.last()

            val allPossibleExits = graph[head] ?: emptySet()

            val pathSmallNodes = path.filter { it.isSmall }
            val visitedSmallNodes = pathSmallNodes.toHashSet().asSet()

            val smallNodesToAvoid = if (pathSmallNodes.size > visitedSmallNodes.size) {
                // At least one small node was visited twice
                visitedSmallNodes
            } else {
                // No small nodes are off limits yet
                emptySet()
            }

            // In this version, revisiting the start node is not allowed ever.
            val avoid = smallNodesToAvoid + startNode

            return allPossibleExits - avoid
        }

        private tailrec fun traversePathsToEnd(
            paths: List<List<CaveNode>>,
            getExits: (List<CaveNode>) -> Set<CaveNode>,
        ): List<List<CaveNode>> {

            return if (paths.all { it.last() == endNode }) {
                paths
            } else {
                traversePathsToEnd(
                    paths.flatMap { path ->
                        if (path.last() == endNode) {
                            listOf(path)
                        } else {
                            getExits(path).sortedBy { it.name }.map { path + it }
                        }
                    }, getExits)
            }
        }

        private fun traverseStartToEnd(getExits: (List<CaveNode>) -> Set<CaveNode>): List<List<CaveNode>> {
            return traversePathsToEnd(listOf(listOf(startNode))) { getExits(it) }
        }

        fun traverseStartToEndUsingInitialLogic(): List<List<CaveNode>> =
            traverseStartToEnd { availableExitsUsingInitialLogic(it) }

        fun traverseStartToEndUsingNewLogic(): List<List<CaveNode>> =
            traverseStartToEnd { availableExitsUsingNewLogic(it) }
    }

    // FIXME: Should be ^[[:upper:]]+$ and ^[[:lower:]]+$, but it was recognizing "h" as neither upper nor lower
    private val allUpperCaseRegex = """^[A-Z]+$""".toRegex()
    private val allLowerCaseRegex = """^[a-z]+$""".toRegex()

    val startNode = "start".toCaveNode()
    val endNode = "end".toCaveNode()
}