package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.replaceWithDayOfKClass
import util.toGrid
import java.math.BigInteger
import kotlin.io.path.Path
import kotlin.io.path.readLines

class Day07 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/{day}/samp1.txt, 21",
            "src/test/resources/days/{day}/prod1.txt, 1605"
        ]
    )
    fun day07Question1(inputFile: String, expected: Int) {

        val lines = Path(inputFile.replaceWithDayOfKClass(this::class)).readLines()

        val grid = lines.toGrid()

        val beamHeads = mutableSetOf<Int>() // Just the X coordinates
        val sX = lines[0].indexOf('S')
        beamHeads.add(sX)

        var nextY = 1
        var splits = 0

        while (nextY < lines.size) {

            val nextBeamHeads = mutableSetOf<Int>()

            beamHeads.forEach { beamX ->

                val below = grid.elemAt(beamX, nextY)

                if (below.value() == '^') {

                    splits++
                    nextBeamHeads.add(beamX - 1)
                    nextBeamHeads.add(beamX + 1)

                } else {

                    nextBeamHeads.add(beamX)
                }
            }

            beamHeads.clear()
            beamHeads.addAll(nextBeamHeads)

            nextY++
        }

        val sum = splits
        assertThat(sum).isEqualTo(expected)
    }

    // *****************************************************************************************************************

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/{day}/samp1.txt, 40",
            "src/test/resources/days/{day}/prod1.txt, 29893386035180"
        ]
    )
    fun day07Question2(inputFile: String, expected: BigInteger) {

        val lines = Path(inputFile.replaceWithDayOfKClass(this::class)).readLines()

        val grid = lines.toGrid()

        val beamHeads = mutableMapOf<Int, BigInteger>() // X -> Timelines
        val sX = lines[0].indexOf('S')
        beamHeads[sX] = BigInteger.ONE

        var nextY = 1
        var splits = 0

        while (nextY < lines.size) {

            val nextBeamHeads = mutableListOf<Pair<Int, BigInteger>>()

            beamHeads.forEach { beam ->

                val below = grid.elemAt(beam.key, nextY)

                if (below.value() == '^') {

                    splits++
                    nextBeamHeads.add(beam.key - 1 to beam.value) // Split beam, same power
                    nextBeamHeads.add(beam.key + 1 to beam.value)

                } else {

                    nextBeamHeads.add(beam.key to beam.value)
                }
            }

            beamHeads.clear()

            // Gather stage
            val groups = nextBeamHeads.groupBy { it.first }
            val newBeams = groups.map { it.key to it.value.sumOf { it.second } }

            newBeams.forEach {
                beamHeads[it.first] = it.second
            }

            nextY++
        }

        val sum = beamHeads.values.reduce(BigInteger::add)
        assertThat(sum).isEqualTo(expected)
    }
}
