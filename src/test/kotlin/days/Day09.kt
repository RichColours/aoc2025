package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.*
import kotlin.io.path.Path
import kotlin.io.path.readLines

class Day09 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/{day}/samp1.txt, 2, 5, 11, 1, 50",
            "src/test/resources/days/{day}/prod1.txt, 0, 0, 0, 0, 4759531084"
        ]
    )
    fun day09Question1(inputFile: String, x1: Int, y1: Int, x2: Int, y2: Int, expected: Long) {

        val lines = Path(inputFile.replaceWithDayOfKClass(this::class)).readLines()

        val coords = lines.map {
            val elems = it.split(',')
            val nums = elems.map { it.toInt() }
            Coord2<Int>(nums[0], nums[1])
        }

        println("Total coords = ${coords.size}")

        val uniqueRects = combinationsCommutative(coords).toList()
        println("Total rectangles = ${uniqueRects.size}")

        val rectToArea = uniqueRects.associateWith {
            // Special area - one larger in each dimension
            areaInclusive(it.v1, it.v2)
        }
        val sortedPairsRectToArea = rectToArea.toList().sortedByDescending { it.second }

        val largestAreaPair = sortedPairsRectToArea.first()
        val rectCoords = largestAreaPair.first
        println(rectCoords)
        val area = largestAreaPair.second
        assertThat(area).isEqualTo(expected)

        println(coords.map { it.x }.joinToString())
        println(coords.map { it.y }.joinToString())
    }


    // *****************************************************************************************************************

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/{day}/samp1.txt",
            "src/test/resources/days/{day}/prod1.txt"
        ]
    )
    fun testEachPairOfCoordsHasOneCoordInCommon(inputFile: String) {
        val lines = Path(inputFile.replaceWithDayOfKClass(this::class)).readLines()

        val coords = lines.map {
            val elems = it.split(',')
            val nums = elems.map { it.toInt() }
            Coord2(nums[0], nums[1])
        }

        val adjacentCoords = coords.allAdjacentPairs()

        val truth = adjacentCoords.all {
            val xSame = it.first.x == it.second.x
            val ySame = it.first.y == it.second.y
            xSame || ySame
        }

        assertThat(truth).isTrue()
    }

    @ParameterizedTest
    @CsvSource(value = ["src/test/resources/days/{day}/samp1.txt, true, 24"])
    fun launchQ2Sample1(inputFile: String, withGrid: Boolean, expected: Long) =
        day09Question2(inputFile, withGrid, expected)

    @ParameterizedTest
    @CsvSource(value = ["src/test/resources/days/{day}/prod1.txt, false, 1539238860"])
    fun launchQ2Prod(inputFile: String, withGrid: Boolean, expected: Long) =
        day09Question2(inputFile, withGrid, expected)

    fun day09Question2(inputFile: String, withGrid: Boolean, expected: Long) {

        val lines = Path(inputFile.replaceWithDayOfKClass(this::class)).readLines()

        val coords = lines.map {
            val elems = it.split(',')
            val nums = elems.map { it.toInt() }
            Coord2(nums[0], nums[1])
        }

        val maxX = coords.maxBy { it.x }.x
        val maxY = coords.maxBy { it.y }.y
        val minX = coords.minBy { it.x }.x
        val minY = coords.minBy { it.y }.y
        println("$minX,$minY ... $maxX,$maxY\n\n")

        val mainLines = coords.allAdjacentPairs().map {
            Line(it.first, it.second)
        }.toList()

        val junctionCoords = mainLines.allAdjacentPairs().map {
            leftSideOuterJunctionCoordinate(it.first, it.second)
        }.toList()

        val outerLines = junctionCoords.allAdjacentPairs().map {
            normaliseLineCoords(Line(it.first, it.second))
        }.toList()

        val sortedOuterLines = outerLines.sortedByDescending { lengthLineHV(it) }

//        val outerShape: List<Line<Int>> = listOf(
//            Line(Coord2(6, 0), Coord2(6, 2)),
//            Line(Coord2(6, 2), Coord2(1, 2)),
//            Line(Coord2(1, 2), Coord2(1, 6)),
//            Line(Coord2(1, 6), Coord2(8, 6)),
//            Line(Coord2(8, 6), Coord2(8, 8)),
//            Line(Coord2(8, 8), Coord2(12, 8)),
//            Line(Coord2(12, 8), Coord2(12, 0)),
//            Line(Coord2(12, 0), Coord2(6, 0)),
//        )

        val gridWidth = maxX + 2
        val gridHeight = maxY + 2

        val gridData = if (withGrid) mutableListOf(
            *sequenceNOf(gridWidth * gridHeight) { '.' }.toList().toTypedArray()
        ) else mutableListOf()

        val grid = MutableFlatListDataSource(gridData, gridWidth).newGrid()

        if (withGrid) {
            coords.allAdjacentPairs().forEach {

                val line = Line(it.first, it.second)
                PointIndexedGeometry.coordsInLine(line).forEach {
                    grid.setValueAt(it.x, it.y, 'X')
                }
            }
        }

        if (withGrid) {
            // Draw in the red tiles again
            coords.forEach {
                grid.setValueAt(it.x, it.y, '#')
            }

            grid.printGrid()
        }

        // Draw outerShape
        if (withGrid) {
            sortedOuterLines.forEach {
                PointIndexedGeometry.coordsInLine(it).forEach {
                    grid.setValueAt(it.x, it.y, '*')
                }
            }

            grid.printGrid()
        }


        val uniqueRects = combinationsCommutative(coords).toList()

        val rectToArea = uniqueRects.associateWith {
            // Special area - one larger in each dimension
            areaInclusive(it.v1, it.v2)
        }

        val sortedPairsRectToArea = rectToArea.toList().sortedByDescending { it.second }

        val largestInnerRect = sortedPairsRectToArea.first { pair ->

            val normalisedRectangle = normaliseRectangleCoords(Pair(pair.first.v1, pair.first.v2))

            // None of the outerShape lines touch this rectangle anywhere
            sortedOuterLines.none { outsideLine ->

                val normalisedLine = normaliseLineCoords(outsideLine)
                val touches = PointIndexedGeometry.lineTouchesRectangle(
                    normalisedRectangle.first,
                    normalisedRectangle.second,
                    normalisedLine
                )
                touches
            }
        }

        println("Largest inner rectangle = ${largestInnerRect.first}")

        val area = largestInnerRect.second
        assertThat(area).isEqualTo(expected)
    }
}
