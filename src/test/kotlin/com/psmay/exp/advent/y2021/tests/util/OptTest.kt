package com.psmay.exp.advent.y2021.tests.util

import com.psmay.exp.advent.y2021.tests.helpers.FooNone
import com.psmay.exp.advent.y2021.tests.helpers.FooSome
import com.psmay.exp.advent.y2021.tests.helpers.TestParams
import com.psmay.exp.advent.y2021.tests.helpers.getTests
import com.psmay.exp.advent.y2021.util.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

internal class OptTest {
    @Test
    fun `empty optOf() does the same thing as emptyOpt()`() {
        // This is merely to suppress a warning.
        val a = emptyOpt<String>()
        val b = optOf<String>()
        assertEquals(a, b)
    }

    @Test
    fun `empty Opt identity`() {
        val opt = emptyOpt<String>()

        assertTrue { opt.isEmpty() }
        assertFalse { opt.isNotEmpty() }
        assertThrows<NoSuchElementException> { opt.get() }
        assertNull(opt.getOrNull())
        assertEquals("or else", opt.getOrElse { "or else" })
        assertEquals("folded as empty", opt.fold({ "folded as full ($it)" }, { "folded as empty" }))
        assertTrue { opt.map { "mapped as full ($it)" }.isEmpty() }
        assertTrue { opt.filter { true }.isEmpty() }
        assertTrue { opt.filter { false }.isEmpty() }

        assertTrue { opt.cleared().isEmpty() }
        assertEquals("changed value", opt.changed("changed value").get())
        assertEquals("defaulted value", opt.defaulted { "defaulted value" }.get())

        var touched = false
        opt.forEach { touched = true }
        assertFalse { touched }

        val sequence = opt.asSequence()
        assertEquals(0, sequence.count())
        assertEquals(opt, sequence.asOpt())

        val iterable = opt.asIterable()
        assertEquals(0, iterable.count())
        assertEquals(opt, iterable.asOpt())

        val list = opt.asList()
        assertEquals(0, list.size)
        assertEquals(opt, list.asOpt())
        assertFalse(list.contains(""))
        assertFalse(list.containsAll(listOf("")))
        assertTrue(list.containsAll(emptyList()))
        assertThrows<IndexOutOfBoundsException> { list.get(-1) }
        assertThrows<IndexOutOfBoundsException> { list.get(0) }
        assertThrows<IndexOutOfBoundsException> { list.get(1) }
        assertEquals(-1, list.indexOf(""))
        assertTrue(list.isEmpty())
        assertEquals(-1, list.lastIndexOf(""))
        assertEquals(emptyList(), list.subList(0, 0))
        assertThrows<IndexOutOfBoundsException> { list.subList(-1, -1) }
        assertThrows<IndexOutOfBoundsException> { list.subList(0, 1) }
        assertThrows<IndexOutOfBoundsException> { list.subList(1, 1) }

        assertFalse { list.iterator().hasNext() }
        assertThrows<NoSuchElementException> { list.iterator().next() }

        assertEquals(0, list.listIterator().nextIndex())
        assertEquals(-1, list.listIterator().previousIndex())
        assertFalse { list.listIterator().hasNext() }
        assertFalse { list.listIterator().hasPrevious() }
        assertThrows<NoSuchElementException> { list.listIterator().next() }
        assertThrows<NoSuchElementException> { list.listIterator().previous() }

        assertEquals(0, list.listIterator(0).nextIndex())
        assertEquals(-1, list.listIterator(0).previousIndex())
        assertFalse { list.listIterator(0).hasNext() }
        assertFalse { list.listIterator(0).hasPrevious() }
        assertThrows<NoSuchElementException> { list.listIterator(0).next() }
        assertThrows<NoSuchElementException> { list.listIterator(0).previous() }

        assertThrows<IndexOutOfBoundsException> { list.listIterator(-1) }
        assertThrows<IndexOutOfBoundsException> { list.listIterator(1) }
    }

    data class OptFullIdentityTestParams(
        val value: String?,
        val nonMatchingValue: String?,
        val matchingPredicate: (String?) -> Boolean,
    ) : TestParams {
        init {
            require(matchingPredicate(value))
            require(!matchingPredicate(nonMatchingValue))
        }

        override fun runTest() {
            fun checkListIteratorAtStartAndStepForward(li: ListIterator<String?>) {
                assertFalse { li.hasPrevious() }
                assertTrue { li.hasNext() }
                assertEquals(-1, li.previousIndex())
                assertEquals(0, li.nextIndex())
                val element = li.next()
                assertEquals(value, element)
            }

            fun checkListIteratorAtEndAndStepBackward(li: ListIterator<String?>) {
                assertFalse { li.hasNext() }
                assertTrue { li.hasPrevious() }
                assertEquals(0, li.previousIndex())
                assertEquals(1, li.nextIndex())
                val element = li.previous()
                assertEquals(value, element)
            }

            val opt = optOf(value)


            assertFalse { opt.isEmpty() }
            assertTrue { opt.isNotEmpty() }
            assertEquals(value, opt.get())
            assertEquals(value, opt.getOrNull())
            assertEquals(value, opt.getOrElse { nonMatchingValue })
            assertEquals(FooSome(value), opt.fold({ FooSome(it) }, { FooNone }))
            assertEquals(FooSome(value), opt.map { FooSome(it) }.get())
            assertEquals(value, opt.filter { true }.get())
            assertEquals(value, opt.filter { matchingPredicate(it) }.get())
            assertTrue { opt.filter { false }.isEmpty() }
            assertTrue { opt.filter { !matchingPredicate(it) }.isEmpty() }

            assertTrue { opt.cleared().isEmpty() }
            assertEquals(nonMatchingValue, opt.changed(nonMatchingValue).get())
            assertEquals(value, opt.defaulted { nonMatchingValue }.get())

            var touched = false
            var seenValue = nonMatchingValue
            opt.forEach {
                touched = true
                seenValue = it
            }
            assertTrue { touched }
            assertEquals(value, seenValue)

            val sequence = opt.asSequence()
            assertEquals(1, sequence.count())
            assertEquals(opt, sequence.asOpt())
            assertEquals(listOf(value), sequence.toList())

            val iterable = opt.asIterable()
            assertEquals(1, iterable.count())
            assertEquals(opt, iterable.asOpt())
            assertEquals(listOf(value), iterable.toList())

            val list = opt.asList()
            assertEquals(1, list.size)
            assertEquals(opt, list.asOpt())
            assertFalse(list.contains(nonMatchingValue))
            assertTrue(list.contains(value))
            assertFalse(list.containsAll(listOf(nonMatchingValue)))
            assertTrue(list.containsAll(listOf(value)))
            assertTrue(list.containsAll(listOf(value, value)))
            assertTrue(list.containsAll(emptyList()))
            assertThrows<IndexOutOfBoundsException> { list.get(-1) }
            assertEquals(value, list.get(0))
            assertThrows<IndexOutOfBoundsException> { list.get(1) }
            assertEquals(-1, list.indexOf(nonMatchingValue))
            assertEquals(0, list.indexOf(value))
            assertFalse(list.isEmpty())
            assertEquals(-1, list.lastIndexOf(nonMatchingValue))
            assertEquals(0, list.lastIndexOf(value))
            assertEquals(emptyList(), list.subList(0, 0))
            assertThrows<IndexOutOfBoundsException> { list.subList(-1, -1) }
            assertEquals(listOf(value), list.subList(0, 1))
            assertEquals(emptyList(), list.subList(1, 1))

            val iterator = list.iterator()
            assertTrue { iterator.hasNext() }
            assertEquals(value, iterator.next())
            assertFalse { iterator.hasNext() }

            val listIterator = list.listIterator()
            checkListIteratorAtStartAndStepForward(listIterator)
            checkListIteratorAtEndAndStepBackward(listIterator)

            val listIterator0 = list.listIterator(0)
            checkListIteratorAtStartAndStepForward(listIterator0)
            checkListIteratorAtEndAndStepBackward(listIterator0)

            val listIterator1 = list.listIterator(1)
            checkListIteratorAtEndAndStepBackward(listIterator1)
            checkListIteratorAtStartAndStepForward(listIterator1)

            assertThrows<IndexOutOfBoundsException> { list.listIterator(-1) }
        }
    }

    @TestFactory
    fun `full Opt identity`() = listOf(
        OptFullIdentityTestParams("value", "") { (it != null) && it.length > 3 },
        OptFullIdentityTestParams(null, "") { it == null })
        .getTests()


}
