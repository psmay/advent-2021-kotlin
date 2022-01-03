package com.psmay.exp.advent.y2021.tests.helpers

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

/**
 * Slightly de-uglifies supplying different test parameters for the same code.
 *
 * - Define the test parameters as a data class.
 * - Have the data class implement [TestParams].
 * - Implement [runTest] with the logic for the test.
 * - For the test definition, create a method annotated with [TestFactory] that returns the result of calling
 *   [getTests] on a list of parameter objects.
 * - The test display name comes from [toString], so override it if the default is not suitable.
 *
 * ```
 *     data class MyTestParams(
 *         val alpha: String,
 *         val bravo: Int,
 *     ) : TestParams {
 *         override fun runTest() {
 *             /* ... test using alpha and bravo values ... */
 *         }
 *     }
 *
 *     @TestFactory
 *     fun `our tests run OK`() = listOf(
 *         MyTestParams("value", 5),
 *         MyTestParams("", 0),
 *     ).getTests()
 * ```
 */

interface TestParams {
    fun runTest()
}

fun TestParams.getTest(): DynamicTest = DynamicTest.dynamicTest(toString()) { runTest() }

fun <T : TestParams> Iterable<T>.getTests() = map { it.getTest() }
