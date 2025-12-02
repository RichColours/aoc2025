package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.countDigits
import util.getLeftAndRightHalfDigits
import util.isEven
import java.math.BigInteger
import kotlin.io.path.Path
import kotlin.io.path.readLines

class Day02 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/02/samp1.txt, 1227775554",
            "src/test/resources/days/02/prod1.txt, 32976912643"
        ]
    )
    fun day02Question1(inputFile: String, expected: BigInteger) {

        val oneLine = Path(inputFile).readLines()[0]

        val ranges = oneLine.split(',')
            .map {
                val partStrings = it.split('-')
                val partNums = partStrings.map { it.toLong() }
                partNums[0] to partNums[1]
            }

        val invalids = ranges
            .flatMap {
                invalidInRange(LongRange(it.first, it.second))
            }
            .map { it.toBigInteger() }
            .reduce(BigInteger::add)

        assertThat(invalids).isEqualTo(expected)
    }

    fun invalidInRange(range: LongRange): Sequence<Long> {

        return range.asSequence()
            .filter {
                val count = it.countDigits()

                if (count.isEven()) {

                    val lr = it.getLeftAndRightHalfDigits()

                    lr.first == lr.second

                } else {
                    false
                }
            }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/02/samp1.txt, 4174379265",
            "src/test/resources/days/02/prod1.txt, 54446379122"
        ]
    )
    fun day02Question2(inputFile: String, expected: BigInteger) {

        val oneLine = Path(inputFile).readLines()[0]

        val ranges = oneLine.split(',')
            .map {
                val partStrings = it.split('-')
                val partNums = partStrings.map { it.toLong() }
                partNums[0] to partNums[1]
            }

        val invalids = ranges
            .flatMap {
                invalidInRange2(LongRange(it.first, it.second))
            }
            .map { it.toBigInteger() }
            .reduce(BigInteger::add)

        assertThat(invalids).isEqualTo(expected)
    }

    fun invalidInRange2(range: LongRange): Sequence<Long> {

        return range.asSequence()
            .filter {
                invalid2(it)
            }
    }

    fun invalid2(it: Long): Boolean {

        val fullLength = it.countDigits()
        val halfLength = fullLength / 2

        val array = it.toString().toCharArray()

        return (1..halfLength).any {

            val segment = array.slice(IntRange(0, it - 1))
            val reps = fullLength / segment.size

            if (fullLength.mod(segment.size) != 0) {
                false
            } else {

                (1..(reps - 1)).all {

                    val startAt = it * segment.size
                    array.slice(IntRange(startAt, startAt + (segment.size) - 1)) == segment
                }
            }
        }
    }

    @Test
    fun testInvalid2() {

        assertThat(invalid2(1)).isFalse()
        assertThat(invalid2(11)).isTrue()
        assertThat(invalid2(12)).isFalse()
    }
}
