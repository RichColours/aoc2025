package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.mergeRanges
import util.replaceWithDayOfKClass
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.useLines

class Day05 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/{day}/samp1.txt, 3",
            "src/test/resources/days/{day}/prod1.txt, 607"
        ]
    )
    fun day05Question1(inputFile: String, expected: Int) {

        var ranges = mutableListOf<LongRange>()
        var fresh = 0

        Path(inputFile.replaceWithDayOfKClass(this::class)).useLines {

            it
                .forEach {

                    if (it.contains('-')) {

                        val items = it.split('-')
                        ranges.add(LongRange(items[0].toLong(), items[1].toLong()))

                    } else {

                        if (it.isBlank()) {

                            ranges.sortBy { it.first }

                        } else {

                            // Process as input line

                            val v = it.toLong()
                            val maybeFound = ranges.find { it.first <= v && v <= it.last }

                            if (maybeFound != null)
                                fresh++
                        }
                    }
                }
        }

        val sum = fresh
        assertThat(sum).isEqualTo(expected)
    }

    // *****************************************************************************************************************


    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/{day}/samp1.txt, 14",
            "src/test/resources/days/{day}/prod1.txt, 342433357244012"
        ]
    )
    fun day05Question2(inputFile: String, expected: Long) {

        var originalRanges = mutableListOf<LongRange>()

        Path(inputFile.replaceWithDayOfKClass(this::class)).useLines {

            it
                .filter { it.contains('-') }
                .forEach {
                    val items = it.split('-')
                    originalRanges.add(LongRange(items[0].toLong(), items[1].toLong()))
                }
        }

        // Process the ranges

        originalRanges.sortBy { it.first }

        val newRanges = LinkedList<LongRange>()

        newRanges.add(originalRanges.first())

        originalRanges.drop(1)
            .forEach {

                // take a pair of latest new one plus next original one
                val latestNew = newRanges.removeLast()
                val nextOriginal = it

                val inputRanges = listOf(latestNew, nextOriginal).sortedBy { it.first }
                val mergedRanges = mergeRanges(inputRanges)

                // Add merged 1 or 2 to the done list
                newRanges.addAll(mergedRanges)
            }

        val sum = newRanges.sumOf { (it.last - it.first) + 1 }

        newRanges.forEach {
            println("${it.first}-${it.last}")
        }

        assertThat(sum).isEqualTo(expected)
    }
}
