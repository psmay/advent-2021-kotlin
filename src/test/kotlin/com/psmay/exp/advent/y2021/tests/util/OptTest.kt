package com.psmay.exp.advent.y2021.tests.util

import com.psmay.exp.advent.y2021.tests.helpers.FooNone
import com.psmay.exp.advent.y2021.tests.helpers.FooSome
import com.psmay.exp.advent.y2021.tests.helpers.TestParams
import com.psmay.exp.advent.y2021.tests.helpers.getTests
import com.psmay.exp.advent.y2021.util.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

    private fun isEven(n: Int) = n % 2 == 0

    @Test
    fun `operations on iterator`() {
        run {
            val iterator = listOf("A", "B", "C").iterator()
            assertEquals(optOf("A"), iterator.nextOpt())
            assertEquals(optOf("B"), iterator.nextOpt())
            assertEquals(optOf("C"), iterator.nextOpt())
            assertEquals(emptyOpt(), iterator.nextOpt())
            assertEquals(emptyOpt(), iterator.nextOpt())
            assertEquals(emptyOpt(), iterator.nextOpt())
        }

        run {
            val iterator = (1..5).iterator()
            assertEquals(optOf(2), iterator.nextOpt { isEven(it) })
            assertEquals(optOf(4), iterator.nextOpt { isEven(it) })
            assertEquals(emptyOpt(), iterator.nextOpt { isEven(it) })
        }

        run {
            val iterator = listOf("A", "B", "C").iterator()
            assertEquals(optOf("A") to true, iterator.nextOptHasNext())
            assertEquals(optOf("B") to true, iterator.nextOptHasNext())
            assertEquals(optOf("C") to false, iterator.nextOptHasNext())
            assertEquals(emptyOpt<String>() to false, iterator.nextOptHasNext())
        }

        // nextOptHasNext() with a predicate is not implemented due to having multiple sketchy implementation details
        // with no good default answer. For one thing, a conditional has-next can't be determined for a non-peeking
        // iterator without losing the item being checked. For another, the predicate should only be called once for
        // a given element, and a trivial peeking implementation would evaluate the predicate twice on any element that
        // might be returned—once when it's the next element and again when it's the current element.
        //
        // The preferred alternative is to apply the predicate as a filter, then use the non-conditional
        // nextOptHasNext(). Here is an example of how to do that.
        run {
            val sourceIterator = (1..5).iterator()
            val iterator = sourceIterator.asSequence().filter { isEven(it) }.iterator()
            assertEquals(optOf(2) to true, iterator.nextOptHasNext())
            assertEquals(optOf(4) to false, iterator.nextOptHasNext())
            assertEquals(emptyOpt<Int>() to false, iterator.nextOptHasNext())
        }

        run {
            val iterator = listOf("A", "B", "C", "D").iterator()
            assertEquals(optOf("B"), iterator.elementAtOpt(1))
            assertEquals(optOf("D"), iterator.elementAtOpt(1))
            assertEquals(emptyOpt(), iterator.elementAtOpt(1))
        }

        run {
            val iterator = listOf("A", "B", "C").iterator()
            assertEquals(optOf("C"), iterator.lastOpt())
            assertEquals(emptyOpt(), iterator.lastOpt())
        }

        run {
            val iterator = (1..5).iterator()
            assertEquals(optOf(4), iterator.lastOpt { isEven(it) })
            assertEquals(emptyOpt(), iterator.lastOpt { isEven(it) })
        }
    }

    @Test
    fun `operations on peeking iterator`() {
        run {
            val peekingIterator = listOf("A", "B", "C").iterator().withPeeking()
            assertEquals(optOf("A"), peekingIterator.peekOpt())
            assertEquals(optOf("A"), peekingIterator.peekOpt())
            assertEquals(optOf("A"), peekingIterator.nextOpt())
            assertEquals(optOf("B"), peekingIterator.peekOpt())
            assertEquals(optOf("B"), peekingIterator.peekOpt())
            assertEquals(optOf("B"), peekingIterator.nextOpt())
            assertEquals(optOf("C"), peekingIterator.peekOpt())
            assertEquals(optOf("C"), peekingIterator.peekOpt())
            assertEquals(optOf("C"), peekingIterator.nextOpt())
            assertEquals(emptyOpt(), peekingIterator.peekOpt())
            assertEquals(emptyOpt(), peekingIterator.peekOpt())
            assertEquals(emptyOpt(), peekingIterator.nextOpt())
        }
    }

    @Test
    fun `operations on list`() {
        val listAlpha = listOf("A", "B", "C")
        val listNum = (1..5).toList()
        val listStrSingle = listOf("A")
        val listStrEmpty = listOf<String>()

        assertEquals(optOf("A"), listAlpha.firstOpt())
        assertEquals(optOf(1), listNum.firstOpt())
        assertEquals(optOf("A"), listStrSingle.firstOpt())
        assertEquals(emptyOpt(), listStrEmpty.firstOpt())

        assertEquals(optOf("A") to true, listAlpha.firstOptHasNext())
        assertEquals(optOf(1) to true, listNum.firstOptHasNext())
        assertEquals(optOf("A") to false, listStrSingle.firstOptHasNext())
        assertEquals(emptyOpt<String>() to false, listStrEmpty.firstOptHasNext())

        assertEquals(emptyOpt(), listAlpha.singleOpt())
        assertEquals(emptyOpt(), listNum.singleOpt())
        assertEquals(optOf("A"), listStrSingle.singleOpt())
        assertEquals(emptyOpt(), listStrEmpty.singleOpt())

        assertEquals(null, listAlpha.singleOptOrNull()) // null means > 1
        assertEquals(null, listNum.singleOptOrNull()) // null means > 1
        assertEquals(optOf("A"), listStrSingle.singleOptOrNull()) // full means 1
        assertEquals(emptyOpt(), listStrEmpty.singleOptOrNull()) // empty means 0

        assertEquals(optOf("C"), listAlpha.lastOpt())
        assertEquals(optOf(5), listNum.lastOpt())
        assertEquals(optOf("A"), listStrSingle.lastOpt())
        assertEquals(emptyOpt(), listStrEmpty.lastOpt())

        assertEquals(optOf("A"), listAlpha.elementAtOpt(0))
        assertEquals(optOf(1), listNum.elementAtOpt(0))
        assertEquals(optOf("A"), listStrSingle.elementAtOpt(0))
        assertEquals(emptyOpt(), listStrEmpty.elementAtOpt(0))

        assertEquals(optOf("B"), listAlpha.elementAtOpt(1))
        assertEquals(optOf(2), listNum.elementAtOpt(1))
        assertEquals(emptyOpt(), listStrSingle.elementAtOpt(1))
        assertEquals(emptyOpt(), listStrEmpty.elementAtOpt(1))

        assertEquals(optOf("C"), listAlpha.elementAtOpt(2))
        assertEquals(optOf(3), listNum.elementAtOpt(2))
        assertEquals(emptyOpt(), listStrSingle.elementAtOpt(2))
        assertEquals(emptyOpt(), listStrEmpty.elementAtOpt(2))

        assertEquals(emptyOpt(), listAlpha.elementAtOpt(3))
        assertEquals(optOf(4), listNum.elementAtOpt(3))
        assertEquals(emptyOpt(), listStrSingle.elementAtOpt(3))
        assertEquals(emptyOpt(), listStrEmpty.elementAtOpt(3))

        assertEquals(emptyOpt(), listAlpha.elementAtOpt(4))
        assertEquals(optOf(5), listNum.elementAtOpt(4))
        assertEquals(emptyOpt(), listStrSingle.elementAtOpt(4))
        assertEquals(emptyOpt(), listStrEmpty.elementAtOpt(4))

        assertEquals(emptyOpt(), listAlpha.elementAtOpt(5))
        assertEquals(emptyOpt(), listNum.elementAtOpt(5))
        assertEquals(emptyOpt(), listStrSingle.elementAtOpt(5))
        assertEquals(emptyOpt(), listStrEmpty.elementAtOpt(5))
    }

    @Test
    fun `operations on sequence`() {
        // NB: Copy any changes to operations on iterables.
        val alphas: Sequence<String> = sequenceOf("A", "B", "C")
        val numbers: Sequence<Int> = (1..5).asSequence()
        val singleString: Sequence<String> = sequenceOf("A")
        val noStrings: Sequence<String> = sequenceOf<String>()

        assertEquals(optOf("A"), alphas.firstOpt())
        assertEquals(optOf(1), numbers.firstOpt())
        assertEquals(optOf("A"), singleString.firstOpt())
        assertEquals(emptyOpt(), noStrings.firstOpt())

        assertEquals(optOf("B"), alphas.firstOpt { it > "A" })
        assertEquals(optOf(2), numbers.firstOpt { isEven(it) })
        assertEquals(emptyOpt(), singleString.firstOpt { it > "A" })
        assertEquals(emptyOpt(), noStrings.firstOpt { it > "A" })

        assertEquals(optOf("A") to true, alphas.firstOptHasNext())
        assertEquals(optOf(1) to true, numbers.firstOptHasNext())
        assertEquals(optOf("A") to false, singleString.firstOptHasNext())
        assertEquals(emptyOpt<String>() to false, noStrings.firstOptHasNext())

        assertEquals(optOf("B") to true, alphas.firstOptHasNext { it > "A" })
        assertEquals(optOf(2) to true, numbers.firstOptHasNext { isEven(it) })
        assertEquals(optOf("A") to false, singleString.firstOptHasNext { it == "A" })
        assertEquals(emptyOpt<String>() to false, noStrings.firstOptHasNext { it == "A" })

        assertEquals(emptyOpt(), alphas.singleOpt())
        assertEquals(emptyOpt(), numbers.singleOpt())
        assertEquals(optOf("A"), singleString.singleOpt())
        assertEquals(emptyOpt(), noStrings.singleOpt())

        assertEquals(emptyOpt(), alphas.singleOpt { it.isNotEmpty() })
        assertEquals(emptyOpt(), numbers.singleOpt { it > 0 })
        assertEquals(optOf("A"), singleString.singleOpt { it.isNotEmpty() })
        assertEquals(emptyOpt(), noStrings.singleOpt { true })

        assertEquals(null, alphas.singleOptOrNull()) // null means > 1
        assertEquals(null, numbers.singleOptOrNull()) // null means > 1
        assertEquals(optOf("A"), singleString.singleOptOrNull()) // full means 1
        assertEquals(emptyOpt(), noStrings.singleOptOrNull()) // empty means 0

        assertEquals(null, alphas.singleOptOrNull { it.isNotEmpty() }) // null means > 1
        assertEquals(null, numbers.singleOptOrNull { it > 0 }) // null means > 1
        assertEquals(optOf("A"), singleString.singleOptOrNull { it.isNotEmpty() }) // full means 1
        assertEquals(emptyOpt(), noStrings.singleOptOrNull { true }) // empty means 0

        assertEquals(optOf("C"), alphas.lastOpt())
        assertEquals(optOf(5), numbers.lastOpt())
        assertEquals(optOf("A"), singleString.lastOpt())
        assertEquals(emptyOpt(), noStrings.lastOpt())

        assertEquals(optOf("C"), alphas.lastOpt { it > "A" })
        assertEquals(optOf(4), numbers.lastOpt { isEven(it) })
        assertEquals(emptyOpt(), singleString.lastOpt { it > "A" })
        assertEquals(emptyOpt(), noStrings.lastOpt { it > "A" })

        assertEquals(optOf("A"), alphas.elementAtOpt(0))
        assertEquals(optOf(1), numbers.elementAtOpt(0))
        assertEquals(optOf("A"), singleString.elementAtOpt(0))
        assertEquals(emptyOpt(), noStrings.elementAtOpt(0))

        assertEquals(optOf("B"), alphas.elementAtOpt(1))
        assertEquals(optOf(2), numbers.elementAtOpt(1))
        assertEquals(emptyOpt(), singleString.elementAtOpt(1))
        assertEquals(emptyOpt(), noStrings.elementAtOpt(1))

        assertEquals(optOf("C"), alphas.elementAtOpt(2))
        assertEquals(optOf(3), numbers.elementAtOpt(2))
        assertEquals(emptyOpt(), singleString.elementAtOpt(2))
        assertEquals(emptyOpt(), noStrings.elementAtOpt(2))

        assertEquals(emptyOpt(), alphas.elementAtOpt(3))
        assertEquals(optOf(4), numbers.elementAtOpt(3))
        assertEquals(emptyOpt(), singleString.elementAtOpt(3))
        assertEquals(emptyOpt(), noStrings.elementAtOpt(3))

        assertEquals(emptyOpt(), alphas.elementAtOpt(4))
        assertEquals(optOf(5), numbers.elementAtOpt(4))
        assertEquals(emptyOpt(), singleString.elementAtOpt(4))
        assertEquals(emptyOpt(), noStrings.elementAtOpt(4))

        assertEquals(emptyOpt(), alphas.elementAtOpt(5))
        assertEquals(emptyOpt(), numbers.elementAtOpt(5))
        assertEquals(emptyOpt(), singleString.elementAtOpt(5))
        assertEquals(emptyOpt(), noStrings.elementAtOpt(5))
    }

    @Test
    fun `operations on iterable`() {
        // NB: Should always be a copy of operations on sequence, but using iterables.
        val alphas: Iterable<String> = listOf("A", "B", "C").asIterable()
        val numbers: Iterable<Int> = (1..5).asIterable()
        val singleString: Iterable<String> = listOf("A").asIterable()
        val noStrings: Iterable<String> = listOf<String>().asIterable()

        assertEquals(optOf("A"), alphas.firstOpt())
        assertEquals(optOf(1), numbers.firstOpt())
        assertEquals(optOf("A"), singleString.firstOpt())
        assertEquals(emptyOpt(), noStrings.firstOpt())

        assertEquals(optOf("B"), alphas.firstOpt { it > "A" })
        assertEquals(optOf(2), numbers.firstOpt { isEven(it) })
        assertEquals(emptyOpt(), singleString.firstOpt { it > "A" })
        assertEquals(emptyOpt(), noStrings.firstOpt { it > "A" })

        assertEquals(optOf("A") to true, alphas.firstOptHasNext())
        assertEquals(optOf(1) to true, numbers.firstOptHasNext())
        assertEquals(optOf("A") to false, singleString.firstOptHasNext())
        assertEquals(emptyOpt<String>() to false, noStrings.firstOptHasNext())

        assertEquals(optOf("B") to true, alphas.firstOptHasNext { it > "A" })
        assertEquals(optOf(2) to true, numbers.firstOptHasNext { isEven(it) })
        assertEquals(optOf("A") to false, singleString.firstOptHasNext { it == "A" })
        assertEquals(emptyOpt<String>() to false, noStrings.firstOptHasNext { it == "A" })

        assertEquals(emptyOpt(), alphas.singleOpt())
        assertEquals(emptyOpt(), numbers.singleOpt())
        assertEquals(optOf("A"), singleString.singleOpt())
        assertEquals(emptyOpt(), noStrings.singleOpt())

        assertEquals(emptyOpt(), alphas.singleOpt { it.isNotEmpty() })
        assertEquals(emptyOpt(), numbers.singleOpt { it > 0 })
        assertEquals(optOf("A"), singleString.singleOpt { it.isNotEmpty() })
        assertEquals(emptyOpt(), noStrings.singleOpt { true })

        assertEquals(null, alphas.singleOptOrNull()) // null means > 1
        assertEquals(null, numbers.singleOptOrNull()) // null means > 1
        assertEquals(optOf("A"), singleString.singleOptOrNull()) // full means 1
        assertEquals(emptyOpt(), noStrings.singleOptOrNull()) // empty means 0

        assertEquals(null, alphas.singleOptOrNull { it.isNotEmpty() }) // null means > 1
        assertEquals(null, numbers.singleOptOrNull { it > 0 }) // null means > 1
        assertEquals(optOf("A"), singleString.singleOptOrNull { it.isNotEmpty() }) // full means 1
        assertEquals(emptyOpt(), noStrings.singleOptOrNull { true }) // empty means 0

        assertEquals(optOf("C"), alphas.lastOpt())
        assertEquals(optOf(5), numbers.lastOpt())
        assertEquals(optOf("A"), singleString.lastOpt())
        assertEquals(emptyOpt(), noStrings.lastOpt())

        assertEquals(optOf("C"), alphas.lastOpt { it > "A" })
        assertEquals(optOf(4), numbers.lastOpt { isEven(it) })
        assertEquals(emptyOpt(), singleString.lastOpt { it > "A" })
        assertEquals(emptyOpt(), noStrings.lastOpt { it > "A" })

        assertEquals(optOf("A"), alphas.elementAtOpt(0))
        assertEquals(optOf(1), numbers.elementAtOpt(0))
        assertEquals(optOf("A"), singleString.elementAtOpt(0))
        assertEquals(emptyOpt(), noStrings.elementAtOpt(0))

        assertEquals(optOf("B"), alphas.elementAtOpt(1))
        assertEquals(optOf(2), numbers.elementAtOpt(1))
        assertEquals(emptyOpt(), singleString.elementAtOpt(1))
        assertEquals(emptyOpt(), noStrings.elementAtOpt(1))

        assertEquals(optOf("C"), alphas.elementAtOpt(2))
        assertEquals(optOf(3), numbers.elementAtOpt(2))
        assertEquals(emptyOpt(), singleString.elementAtOpt(2))
        assertEquals(emptyOpt(), noStrings.elementAtOpt(2))

        assertEquals(emptyOpt(), alphas.elementAtOpt(3))
        assertEquals(optOf(4), numbers.elementAtOpt(3))
        assertEquals(emptyOpt(), singleString.elementAtOpt(3))
        assertEquals(emptyOpt(), noStrings.elementAtOpt(3))

        assertEquals(emptyOpt(), alphas.elementAtOpt(4))
        assertEquals(optOf(5), numbers.elementAtOpt(4))
        assertEquals(emptyOpt(), singleString.elementAtOpt(4))
        assertEquals(emptyOpt(), noStrings.elementAtOpt(4))

        assertEquals(emptyOpt(), alphas.elementAtOpt(5))
        assertEquals(emptyOpt(), numbers.elementAtOpt(5))
        assertEquals(emptyOpt(), singleString.elementAtOpt(5))
        assertEquals(emptyOpt(), noStrings.elementAtOpt(5))
    }

    @Test
    fun `operations on mutable list`() {
        run {
            val list = mutableListOf("A", "B", "C")
            assertEquals(optOf("A"), list.removeFirstOpt())
            assertEquals(listOf("B", "C"), list.toList())
            assertEquals(optOf("B"), list.removeFirstOpt())
            assertEquals(listOf("C"), list.toList())
            assertEquals(optOf("C"), list.removeFirstOpt())
            assertEquals(listOf(), list.toList())
            assertEquals(emptyOpt(), list.removeFirstOpt())
            assertEquals(listOf(), list.toList())
        }

        run {
            val list = mutableListOf("A", "B", "C")
            assertEquals(optOf("C"), list.removeLastOpt())
            assertEquals(listOf("A", "B"), list.toList())
            assertEquals(optOf("B"), list.removeLastOpt())
            assertEquals(listOf("A"), list.toList())
            assertEquals(optOf("A"), list.removeLastOpt())
            assertEquals(listOf(), list.toList())
            assertEquals(emptyOpt(), list.removeLastOpt())
            assertEquals(listOf(), list.toList())
        }

        run {
            val list = mutableListOf("A", "B", "C")
            assertEquals(optOf("A"), list.removeFirstOpt())
            assertEquals(listOf("B", "C"), list.toList())
            assertEquals(optOf("C"), list.removeLastOpt())
            assertEquals(listOf("B"), list.toList())
            assertEquals(optOf("B"), list.removeFirstOpt())
            assertEquals(listOf(), list.toList())
            assertEquals(emptyOpt(), list.removeLastOpt())
            assertEquals(listOf(), list.toList())
            assertEquals(emptyOpt(), list.removeFirstOpt())
            assertEquals(listOf(), list.toList())
        }
    }
}
