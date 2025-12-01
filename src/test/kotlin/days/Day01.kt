package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isZero
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.filePathToLines
import util.section
import kotlin.io.path.Path
import kotlin.io.path.useLines

class Day01 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/01/samp1.txt, 3",
            "src/test/resources/days/01/prod1.txt, 980"
        ]
    )
    fun day01Question1(inputFile: String, expected: Int) {

        val numberOfZeroHits = Path(inputFile).useLines {

            val output = it
                .map {
                    // Turn it into a positive turn
                    val dir = it[0]
                    val num = it.drop(1).toInt()
                    if (dir == 'R')
                        num
                    else
                        100 - num
                }
                .fold(50 to 0) { acc, next ->

                    val newPointing = (acc.first + next).mod(100)
                    val newZeros = acc.second + (if (newPointing == 0) 1 else 0)
                    newPointing to newZeros
                }

            output.second
        }

        assertThat(numberOfZeroHits).isEqualTo(expected)
    }

    fun newPosition(current: Int, turn: Int): Int = (current + turn).mod(100)

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/01/mine1.txt, 0, 2",
            "src/test/resources/days/01/mine1.txt, 1, 1",
            "src/test/resources/days/01/mine1.txt, 2, 1",
            "src/test/resources/days/01/mine1.txt, 3, 2",
            "src/test/resources/days/01/mine1.txt, 4, 2",

            "src/test/resources/days/01/samp1.txt, 0, 6",
            "src/test/resources/days/01/prod1.txt, 0, 5961"
        ]
    )
    fun day01Question2(inputFile: String, section: Int, expected: Int) {

        val lines = filePathToLines(inputFile).section(section)

        val numberOfZeroHits = lines.let {

            val output = it
                .map {
                    // Turn it into left/right turn
                    val dir = it[0]
                    val num = it.drop(1).toInt()
                    if (dir == 'R')
                        num
                    else
                        -num
                }
                .fold(50 to 0) { acc, next ->

                    val next = next

                    val current = acc.first

                    val accZeros = acc.second

                    val isPositive = next >= 0

                    val newPosition = newPosition(current, next)

                    val diffToZero = if (isPositive)
                        100 - current
                    else {
                        // Will be negative
                        if (current == 0)
                            -100
                        else
                            (-current)
                    }

                    if (isPositive)
                        assert(diffToZero >= 0)
                    else
                        assert(diffToZero <= 0)

                    val moreZeros = if (isPositive) {

                        if (next >= diffToZero) {
                            1 + ((next - diffToZero) / 100)
                        } else {
                            0
                        }

                    } else {

                        assert(next < 0)

                        if (next <= diffToZero) {
                            1 + ((next - diffToZero) / -100)
                        } else {
                            0
                        }

                    }

                    //println("current=$current next=$next diffToZero=$diffToZero newPosition=$newPosition moreZeros=$moreZeros")

                    newPosition to accZeros + moreZeros
                }

            output.second
        }

        assertThat(numberOfZeroHits).isEqualTo(expected)
    }

    @Test
    fun testMod() {
        assertThat(100.mod(100)).isZero()
        assertThat((-100).mod(100)).isZero()
        assertThat((-60).mod(20)).isEqualTo(0)
        assertThat((-99).mod(-100)).isEqualTo(-99)
        assertThat((-99).mod(100)).isEqualTo(1)
    }

    @Test
    fun testNewPosition() {
        assertThat(newPosition(0, 0)).isEqualTo(0)
        assertThat(newPosition(0, 100)).isEqualTo(0)
        assertThat(newPosition(0, -10)).isEqualTo(90)
        assertThat(newPosition(0, 90)).isEqualTo(90)
        assertThat(newPosition(0, 110)).isEqualTo(10)
        assertThat(newPosition(0, -110)).isEqualTo(90)
        assertThat(newPosition(40, -10)).isEqualTo(30)
        assertThat(newPosition(40, -50)).isEqualTo(90)
    }
}
