package util

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class SparePartsTest {

    @Test
    fun testListSplit() {

        val data = listOf("a", "aa", "===", "b", "===", "c", "cc").split { it.startsWith("===") }

        assertThat(data).containsExactly(
            listOf("a", "aa"),
            listOf("b"),
            listOf("c", "cc")
        )
    }

    @Test
    fun deriveRangesSingle() {
        assertThat(listOf(1).deriveRanges()).isEqualTo(listOf(IntRange(1, 1)))
    }

    @Test
    fun deriveRangesSimple1() {
        assertThat(listOf(1, 2).deriveRanges()).isEqualTo(listOf(IntRange(1, 2)))
    }

    @Test
    fun deriveRangesSimple2() {
        assertThat(listOf(1, 3).deriveRanges()).isEqualTo(listOf(IntRange(1, 1), IntRange(3, 3)))
    }

    @Test
    fun deriveRangesSimple3() {
        assertThat(listOf(1, 2, 3).deriveRanges()).isEqualTo(listOf(IntRange(1, 3)))
    }

    @Test
    fun testCombinationsCommutative1() {
        val xs = listOf(2, 1)
        val combinations = combinationsCommutative(xs)
        assertThat(combinations).containsExactlyInAnyOrder(PairUnordered(1, 2))
    }

    @Test
    fun testCombinationsCommutative2() {
        val xs = listOf(2, 1, 3)
        val combinations = combinationsCommutative(xs)
        assertThat(combinations).containsExactlyInAnyOrder(PairUnordered(1, 2), PairUnordered(1, 3), PairUnordered(3, 2))
    }

    @Test
    fun test_allAdjacentPairs1() {
        assertThat(listOf(1, 2, 3).allAdjacentPairs().toList())
            .isEqualTo(
                listOf(Pair(1, 2), Pair(2, 3), Pair(3, 1))
            )
    }
}