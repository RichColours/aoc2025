package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.Coord3
import util.Distance3
import util.distance
import util.replaceWithDayOfKClass
import kotlin.io.path.Path
import kotlin.io.path.readLines

class Day08 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/{day}/samp1.txt, 10, 40",
            "src/test/resources/days/{day}/prod1.txt, 1000, 29406"
        ]
    )
    fun day08Question1(inputFile: String, makeConnections: Int, expected: Int) {

        val lines = Path(inputFile.replaceWithDayOfKClass(this::class)).readLines()

        val boxes = lines.map {
            val elems = it.split(',').map { it.toInt() }
            Coord3(elems[0].toDouble(), elems[1].toDouble(), elems[2].toDouble())
        }

        val totalDistances = (lines.size * (lines.size - 1)) / 2

        var xCursor = 1
        var yCursor = 0

        val distances = arrayOfNulls<Distance3>(totalDistances)

        (0..<totalDistances).forEach {

            val p1 = boxes[yCursor]
            val p2 = boxes[xCursor]
            distances[it] = Distance3(p1, p2, distance(p1, p2))
            xCursor++
            if (xCursor == boxes.size) {
                yCursor++
                xCursor = yCursor + 1
            }
        }

        distances.sortBy { it!!.distance }

        val circuits = mutableListOf<MutableSet<Coord3>>()

        var connectionsMade = 0
        var nextDistanceIndex = 0

        while (connectionsMade < makeConnections) {

            val distance = distances[nextDistanceIndex]

            val connectedP1 = circuits.find { it.contains(distance!!.p1) }
            val connectedP2 = circuits.find { it.contains(distance!!.p2) }

            if (connectedP1 == null && connectedP2 == null) {
                // Put into new circuit
                circuits.add(mutableSetOf(distance!!.p1, distance.p2))
                connectionsMade++

            } else if (connectedP1 != null && connectedP2 != null && connectedP1 === connectedP2) {
                // Same circuit, do nothing
                connectionsMade++
            } else if (connectedP1 != null && connectedP2 == null) {

                connectedP1.add(distance!!.p2)
                connectionsMade++

            } else if (connectedP2 != null && connectedP1 == null) {

                connectedP2.add(distance!!.p1)
                connectionsMade++

            } else {
                // P1 and P2 are different - join the circuits!

                val newCircuit = mutableSetOf<Coord3>()
                newCircuit.addAll(connectedP1!!)
                newCircuit.addAll(connectedP2!!)
                circuits.remove(connectedP1)
                circuits.remove(connectedP2)
                circuits.add(newCircuit)
                connectionsMade++
            }

            nextDistanceIndex++
        }

        circuits.sortByDescending { it.size }

        val product = circuits.take(3).map { it.size }.reduce(Int::times)
        assertThat(product).isEqualTo(expected)
    }


    // *****************************************************************************************************************

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/{day}/samp1.txt, 10, 25272",
            "src/test/resources/days/{day}/prod1.txt, 1000, 7499461416"
        ]
    )
    fun day08Question2(inputFile: String, makeConnections: Int, expected: Long) {

        val lines = Path(inputFile.replaceWithDayOfKClass(this::class)).readLines()

        val boxes = lines.map {
            val elems = it.split(',').map { it.toInt() }
            Coord3(elems[0].toDouble(), elems[1].toDouble(), elems[2].toDouble())
        }

        val totalDistances = (lines.size * (lines.size - 1)) / 2

        var xCursor = 1
        var yCursor = 0

        val distances = arrayOfNulls<Distance3>(totalDistances)

        (0..<totalDistances).forEach {

            val p1 = boxes[yCursor]
            val p2 = boxes[xCursor]
            distances[it] = Distance3(p1, p2, distance(p1, p2))
            xCursor++
            if (xCursor == boxes.size) {
                yCursor++
                xCursor = yCursor + 1
            }
        }

        distances.sortBy { it!!.distance }

        val boxToCircuit = mutableMapOf<Coord3, Int>()

        boxes.forEachIndexed { index, box ->
            boxToCircuit[box] = index
        }

        var nextDistanceIndex = 0
        var distanceWhichJoinedAll: Distance3? = null

        while (distanceWhichJoinedAll == null) {

            val distance = distances[nextDistanceIndex]!!

            val connectedP1 = boxToCircuit[distance.p1]!!
            val connectedP2 = boxToCircuit[distance.p2]!!

            if (connectedP1 == connectedP2) {
                // Same circuit, do nothing
            } else {
                // P1 and P2 are different - join the circuits!

                // Move P2's circuit boxes into P1's circuit
                val allP2CircuitBoxes = boxToCircuit.filter { it.value == connectedP2 }.map { it.key }

                allP2CircuitBoxes.forEach { boxToCircuit[it] = connectedP1 }

                if (boxToCircuit.values.toSet().size == 1) {
                    // found total joiner
                    distanceWhichJoinedAll = distance
                }
            }

            nextDistanceIndex++
        }

        val product = distanceWhichJoinedAll.p1.x.toLong() * distanceWhichJoinedAll.p2.x.toLong()
        assertThat(product).isEqualTo(expected)
    }
}
