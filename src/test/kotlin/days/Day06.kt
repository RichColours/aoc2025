package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.difference
import util.replaceWithDayOfKClass
import java.math.BigInteger
import kotlin.io.path.Path
import kotlin.io.path.useLines
import kotlin.math.min

class Day06 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/{day}/samp1.txt, 4277556",
            "src/test/resources/days/{day}/prod1.txt, -1"
        ]
    )
    fun day06Question1(inputFile: String, expected: BigInteger) {

        val sums = mutableListOf<BigInteger>()
        val mults = mutableListOf<BigInteger>()

        val answers = mutableListOf<BigInteger>()

        Path(inputFile.replaceWithDayOfKClass(this::class)).useLines {

            it.forEachIndexed { lineIndex, it ->

                val elems = it.trim().split(Regex("\\s+"))

                if (it.contains('+') || it.contains('*')) {

                    elems.forEachIndexed { index, it ->

                        if (it == "+") {
                            answers.add(sums[index])
                        } else {
                            answers.add(mults[index])
                        }
                    }

                } else {

                    elems.forEachIndexed { index, it ->

                        if (lineIndex == 0) {
                            sums.add(BigInteger.ZERO)
                            mults.add(BigInteger.ONE)
                        }

                        sums[index] = sums[index].add(it.toBigInteger())
                        mults[index] = mults[index].multiply(it.toBigInteger())
                    }

                }


            }

        }

        val sum = answers.sumOf { it }
        assertThat(sum).isEqualTo(expected)
    }

    // *****************************************************************************************************************

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/{day}/samp1.txt, 3263827",
            "src/test/resources/days/{day}/prod1.txt, 10875057285868"
        ]
    )
    fun day06Question2(inputFile: String, expected: BigInteger) {

        /**
         * Two passes over the file:
         *  * First - count total lines and last line derive the columns info and operators
         *  * Second - process the numbers
         */

        // Pass 1
        val finalLine = Path(inputFile.replaceWithDayOfKClass(this::class)).useLines {
            it.last()
        }

        val totalWidth = finalLine.length
        val operators = finalLine.trim().split(Regex("\\s+"))
        val groups = mutableListOf<IntRange>()

        finalLine.forEachIndexed { index, it ->
            if (it == '+' || it == '*') {
                // Seek forward, easier this way
                val nextPlus = finalLine.indexOf('+', index + 1).let {
                    if (it == -1) totalWidth - 1 else it - 2
                }
                val nextMult = finalLine.indexOf('*', index + 1).let {
                    if (it == -1) totalWidth - 1 else it - 2
                }

                val closestEndIndex = min(nextPlus, nextMult)

                val newRange = IntRange(index, closestEndIndex)
                groups.add(newRange)
            }
        }

        val groupStrings: MutableList<MutableList<StringBuilder>> = mutableListOf()

        // Pass 2
        Path(inputFile.replaceWithDayOfKClass(this::class)).useLines {

            it.forEachIndexed { lineIndex, line ->

                if (line.contains('+') || line.contains('*')) {

                } else {

                    groups.forEachIndexed { groupIndex, group ->

                        if (lineIndex == 0) {
                            val strings = mutableListOf<StringBuilder>()
                            groupStrings.add(strings)
                            (1..group.difference()).forEach { _ ->
                                strings.add(StringBuilder())
                            }
                        }

                        (group.start..group.endInclusive).forEachIndexed { indexInGroup, charIndex ->
                            val c = line[charIndex]
                            groupStrings[groupIndex][indexInGroup].append(c)
                        }
                    }
                }
            }
        }

        val problems = groups.mapIndexed { groupIndex, group ->

            val operator = operators[groupIndex].trim()
            val numbers = groupStrings[groupIndex].map { it.toString().trim().toBigInteger() }

            if (operator == "+")
                numbers.reduce(BigInteger::add)
            else
                numbers.reduce(BigInteger::multiply)
        }


        val sum = problems.reduce(BigInteger::add)

        assertThat(sum).isEqualTo(expected)
    }


}
