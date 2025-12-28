package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.combinationsExclusive
import util.flipBits
import util.replaceWithDayOfKClass
import util.section
import kotlin.io.path.Path
import kotlin.io.path.readLines

class Day10 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/{day}/samp1.txt, 0, 2",
            "src/test/resources/days/{day}/samp1.txt, 1, 3",
            "src/test/resources/days/{day}/samp1.txt, 2, 2",
            "src/test/resources/days/{day}/prod1.txt, 0, 494"
        ]
    )
    fun day10Question1(inputFile: String, section: Int, expected: Int) {

        val lines = Path(inputFile.replaceWithDayOfKClass(this::class)).readLines().section(section)

        val sum = lines.sumOf(::processMachineLine)

        assertThat(sum).isEqualTo(expected)
    }

    fun processMachineLine(line: String): Int {

        val elems = line.split(' ')

        val diagram = elems[0].drop(1).dropLast(1).map { it == '#' }
        val diagramAsLong: Long = diagram.foldIndexed(0L) { index, acc, it ->
            acc + (if (it) 1.shl(index) else 0)
        }

        val buttons = elems
            .drop(1)
            .dropLast(1)
            .map {
                it.drop(1)
                    .dropLast(1)
                    .split(',')
                    .map { it.toInt() }
            }

        val buttonsAsLongs = buttons.map {
            it.sumOf {
                1L.shl(it)
            }
        }

        var foundOnButtonCombo: List<List<Int>>? = null

        // Go through all 1-button combos, then all 2-button, ...
        // n, then n*n-1, then n*n-1*n-2 ...
        val foundWithCombo = (1..buttons.size).first { nChoices ->

            val buttonIndexCombos = combinationsExclusive(nChoices, buttons.size)

            val foundWithCombo = buttonIndexCombos.any {

                val buttons = it.map { buttons[it] }
                //println("Trying button index combo $it, actual $buttons")
                // Bits from all buttons
                val bitsToFlip = buttons.flatten()

                val calculated = 0L.flipBits(bitsToFlip)
                if (calculated == diagramAsLong) {
                    foundOnButtonCombo = buttons
                    println(foundOnButtonCombo)
                    true
                } else {
                    false
                }
            }

            foundWithCombo
        }

        return foundWithCombo
    }

    // *****************************************************************************************************************

}
