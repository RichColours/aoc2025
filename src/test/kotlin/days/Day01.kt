package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.filePathToLines
import kotlin.math.abs

class Day01 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/01/samp1.txt, -1",
            "src/test/resources/days/01/prod1.txt, -1"
        ]
    )
    fun day01Question1(inputFile: String, expected: Int) {

        val lines = filePathToLines(inputFile)


    }
}
