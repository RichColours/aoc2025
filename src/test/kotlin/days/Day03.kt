package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigInteger
import kotlin.io.path.Path
import kotlin.io.path.useLines

class Day03 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/03/samp1.txt, 357",
            "src/test/resources/days/03/prod1.txt, 17179"
        ]
    )
    fun day03Question1(inputFile: String, expected: Int) {

        val sum = Path(inputFile).useLines {

            it.map { maxJoltageFromBank(it) }
                .sum()
        }

        assertThat(sum).isEqualTo(expected)
    }

    fun maxJoltageFromBank(bank: String): Int {

        var first = 0
        var second = 0

        (0..<(bank.length)).forEach {

            val thisOne = bank[it].digitToInt()

            if (thisOne > first && it < bank.length - 1) {
                // But only if not last
                first = thisOne
                second = bank[it + 1].digitToInt()

            } else if (thisOne > second) {
                second = thisOne
            } else {
                //
            }
        }

        return first * 10 + second
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "8119, 89",
            "987654321111111, 98",
            "811111111111119, 89",
            "234234234234278, 78",
            "818181911112111, 92",
        ]
    )
    fun test_maxJoltageFromBank(input: String, expected: Int) {
        assertThat(maxJoltageFromBank(input)).isEqualTo(expected)
    }

    // *****************************************************************************************************************

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/03/samp1.txt, 3121910778619",
            "src/test/resources/days/03/prod1.txt, 170025781683941"
        ]
    )
    fun day03Question2(inputFile: String, expected: BigInteger) {

        val sum = Path(inputFile).useLines {

            it.map { maxJoltageFromBank12rStarter(it) }
                .reduce(BigInteger::add)
        }

        assertThat(sum).isEqualTo(expected)
    }

    fun maxJoltageFromBank12rStarter(bank: String) =
        maxJoltageFromBank12r(
            bank.map { it.digitToInt() },
            12,
            emptyList()
        )
            .map { it.digitToChar() }
            .joinToString("").toBigInteger()

    tailrec fun maxJoltageFromBank12r(bank: List<Int>, size: Int, chosenBatteries: List<Int>): List<Int> {

        if (size == 0)
            return chosenBatteries

        if (size == bank.size)
            return chosenBatteries + bank

        if (size > bank.size)
            throw Error("More batteries needed $size than available in the battery bank ${bank.size}")

        val remain = bank.size - (bank.size - (size - 1))
        val chooseFromN = bank.size - remain

        val chosen = bank.subList(0, chooseFromN).max()
        val chosenIndex = bank.indexOf(chosen)

        val newBank = bank.subList(chosenIndex + 1, bank.size)

        return maxJoltageFromBank12r(newBank, size - 1, chosenBatteries + chosen)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "211111111111111, 211111111111",
            "121111111111111, 211111111111",
            "112111111111110, 211111111111",
            "111211111111111, 211111111111",
            "987654321111, 987654321111",
            "987654321111111, 987654321111",
            "811111111111119, 811111111119",
            "234234234234278, 434234234278",
            "818181911112111, 888911112111",
        ]
    )
    fun test_maxJoltageFromBank12(input: String, expected: BigInteger) {
        assertThat(maxJoltageFromBank12rStarter(input)).isEqualTo(expected)
    }
}
