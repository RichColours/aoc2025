package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.replaceWithDayOfKClass
import util.toGrid
import kotlin.io.path.Path
import kotlin.io.path.readLines

class Day04 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/{day}/samp1.txt, 13",
            "src/test/resources/days/{day}/prod1.txt, 1527"
        ]
    )
    fun day04Question1(inputFile: String, expected: Int) {

        val lines = Path(inputFile.replaceWithDayOfKClass(this::class))
            .readLines()

        val grid = lines.toGrid()

        val numAccessibleRolls = grid.count {
            if (it.value() != '@') {
                false
            } else {
                val surroundingRolls = it.neighboursExc().filter {
                    it.value() == '@'
                }

                surroundingRolls.size < 4
            }
        }

        val sum = numAccessibleRolls

        assertThat(sum).isEqualTo(expected)
    }

    // *****************************************************************************************************************

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/{day}/samp1.txt, 43",
            "src/test/resources/days/{day}/prod1.txt, 8690"
        ]
    )
    fun day04Question2(inputFile: String, expected: Int) {

        val lines = Path(inputFile.replaceWithDayOfKClass(this::class))
            .readLines()

        val oneLongLine = lines.joinToString("")

        val grid = oneLongLine.toMutableList().toGrid(lines[0].length)

        var totalFound = 0

        do {

            val accessibleRolls = grid.filter {
                if (it.value() != '@') {
                    false
                } else {
                    val surroundingRolls = it.neighboursExc().filter {
                        it.value() == '@'
                    }

                    surroundingRolls.size < 4
                }
            }

            if (accessibleRolls.size == 0)
                break


            totalFound += accessibleRolls.size

            // Mod the grid
            // Replace each elem with an x
            accessibleRolls.forEach {

                grid.setValueAt(it.x, it.y, 'x')
            }

        } while (true)

        val sum = totalFound

        assertThat(sum).isEqualTo(expected)
    }

}
