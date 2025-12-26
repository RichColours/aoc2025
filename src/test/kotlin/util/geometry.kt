package util

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

data class Coord2<T : Number>(val x: T, val y: T)

data class Coord3(val x: Double, val y: Double, val z: Double)

data class Distance3(val p1: Coord3, val p2: Coord3, val distance: Double)

/** Normalised coordinates */
data class Rectangle<T : Number>(val bl: Coord2<T>, val tr: Coord2<T>)

/*
 * Some issues in here about what a line length means in drawn pixels, with this x and xClosed stuff.
 * Should stay aligned to real geometry ideally.
 *
 * Grid is used like a light-up-the-pixels system but really ought to just align with infinite point-based geometry.
 *
 * If a line is from 0,0->0,0 then does it light anything up?
 * If a line is from 0,0->1,0 then does it light anything up? How many pixels?
 *
 * Fundamentally, is a point something that lights up a pixel or not?
 * * If so,  then 0,0->1,0 must light up 2 pixels (even though the 'distance'/'difference' is just 1.
 * * If not, then it is one pixel lit per distance/difference along a line. More uniform?
 *
 * I reckon one-pixel-per-distance is better ...
 *
 * So to light up a whole pixel needs 0,0->1,1
 *
 * But - day 09 is using coordinate numbers to index actual points rather than distances.
 * So the point 0,0 is a full pixel, not an infinitely small point.
 *
 * :-S
 */

fun distance(p1: Coord3, p2: Coord3) = sqrt((p2.x - p1.x).pow(2) + (p2.y - p1.y).pow(2) + (p2.z - p1.z).pow(2))

/**
 * Requires normalisation
 */
fun lengthLineHV(line: Line<Int>): Int {

    return if (isLineHorizontal(line))
        line.end.x - line.start.x
    else
        line.end.y - line.start.y
}

fun area(p1: Coord2<Int>, p2: Coord2<Int>): Long {
    return abs(p1.x - p2.x).toLong() * abs(p1.y - p2.y).toLong()
}

fun areaInclusive(p1: Coord2<Int>, p2: Coord2<Int>): Long {
    return (abs(p1.x - p2.x).toLong() + 1) * (abs(p1.y - p2.y).toLong() + 1)
}

data class Line<T : Number>(val start: Coord2<T>, val end: Coord2<T>)

/** Careful, this doesn't cope with 90-degree turns, reports wrongly */
fun pointsDirection(p1: Coord2<Double>, p2: Coord2<Double>, p3: Coord2<Double>): Int {

    val direction = (p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x)

    return when {
        direction < 0 -> -1
        direction > 0 -> 1
        else -> 0
    }

}

/**
 *
 */
fun lineTurnsWhichWay(l1: Line<Int>, l2: Line<Int>): Int {

    if (l2.start != l1.start) throw Error("Line2 does not continue Line1")

    // Horizontal line1
    if (l1.start.y == l1.end.y) {


    }


    return 0
}

fun normaliseRectangleCoords(inRect: Pair<Coord2<Int>, Coord2<Int>>): Pair<Coord2<Int>, Coord2<Int>> {

    val p1 = inRect.first
    val p2 = inRect.second

    val xs = listOf(p1.x, p2.x)
    val ys = listOf(p1.y, p2.y)

    val bl = Coord2(xs.min(), ys.min())
    val tr = Coord2(xs.max(), ys.max())

    return bl to tr
}

fun normaliseLineCoords(line: Line<Int>): Line<Int> {
    val newCoords = normaliseRectangleCoords(line.start to line.end)
    return Line(newCoords.first, newCoords.second)
}

fun isLineHorizontal(line: Line<Int>) = line.start.y == line.end.y

/**
 * Coordinates are infinitely small and have zero dimension in both directions.
 * 0,0-1,0 is 1,0 distance.
 */
object PointInfiniteGeometry {


}

/**
 * Coordinates point to whole pixels (they have a 1-width).
 */
object PointIndexedGeometry {

    /**
     * 0,0-0,0 is distance of 1,1
     */
    fun coordsInLine(line: Line<Int>): Sequence<Coord2<Int>> {

        if (!(line.start.x == line.end.x || line.start.y == line.end.y))
            throw Error("Line is not horizontal or vertical: $line")

        val c1 = line.start
        val c2 = line.end

        val xRangeTemp = c1.x.autoProgressionTo(c2.x)
        val yRangeTemp = c1.y.autoProgressionTo(c2.y)

        val xRange =
            if (xRangeTemp.absDifference() == 0) sequenceNOf(yRangeTemp.absDifferenceClosed()) { xRangeTemp.first } else xRangeTemp.asSequence()
        val yRange =
            if (yRangeTemp.absDifference() == 0) sequenceNOf(xRangeTemp.absDifferenceClosed()) { yRangeTemp.first } else yRangeTemp.asSequence()


        return xRange.zip(yRange).map { Coord2(it.first, it.second) }
    }

    fun rectangleContainsPoint(bl: Coord2<Int>, tr: Coord2<Int>, point: Coord2<Int>) =
        bl.x <= point.x && point.x <= tr.x && bl.y <= point.y && point.y <= tr.y

    fun horizontalLineOverlapsRectangle(normalisedRectangle: Rectangle<Int>, normalisedLine: Line<Int>): Boolean {

        val inYRange =
            normalisedRectangle.bl.y <= normalisedLine.start.y && normalisedLine.start.y <= normalisedRectangle.tr.y

        val bothLeft =
            normalisedLine.start.x < normalisedRectangle.bl.x && normalisedLine.end.x < normalisedRectangle.bl.x
        val bothRight =
            normalisedLine.start.x > normalisedRectangle.tr.x && normalisedLine.end.x > normalisedRectangle.tr.x
        return inYRange && !(bothRight || bothLeft)
    }

    fun verticalLineOverlapsRectangle(normalisedRectangle: Rectangle<Int>, normalisedLine: Line<Int>): Boolean {

        val inXRange =
            normalisedRectangle.bl.x <= normalisedLine.start.x && normalisedLine.start.x <= normalisedRectangle.tr.x

        val bothBelow =
            normalisedLine.start.y < normalisedRectangle.bl.y && normalisedLine.end.y < normalisedRectangle.bl.y
        val bothAbove =
            normalisedLine.start.y > normalisedRectangle.tr.y && normalisedLine.end.y > normalisedRectangle.tr.y
        return inXRange && !(bothBelow || bothAbove)
    }

    fun lineTouchesRectangle(bl: Coord2<Int>, tr: Coord2<Int>, line: Line<Int>): Boolean {

        return if (isLineHorizontal(line))
            horizontalLineOverlapsRectangle(Rectangle(bl, tr), line)
        else
            verticalLineOverlapsRectangle(Rectangle(bl, tr), line)
    }
}

enum class Heading {
    EASTWARD, DOWNWARD, WESTWARD, UPWARD
}

fun heading(line: Line<Int>): Heading {
    return if (isLineHorizontal(line)) {
        if (line.start.x < line.end.x) Heading.EASTWARD else Heading.WESTWARD
    } else {
        if (line.start.y < line.end.y) Heading.UPWARD else Heading.DOWNWARD
    }
}

fun leftSideOuterJunctionCoordinate(p1: Line<Int>, p2: Line<Int>): Coord2<Int> {

    val mid = p1.end
    val firstHeading = heading(p1)
    val secondHeading = heading(p2)

    val shift = when (firstHeading to secondHeading) {

        Heading.EASTWARD to Heading.UPWARD -> -1 to 1
        Heading.EASTWARD to Heading.DOWNWARD -> 1 to 1
        Heading.DOWNWARD to Heading.EASTWARD -> 1 to 1
        Heading.DOWNWARD to Heading.WESTWARD -> 1 to -1
        Heading.WESTWARD to Heading.DOWNWARD -> 1 to -1
        Heading.WESTWARD to Heading.UPWARD -> -1 to -1
        Heading.UPWARD to Heading.WESTWARD -> -1 to -1
        Heading.UPWARD to Heading.EASTWARD -> -1 to 1

        else -> throw Error("Impossible junction of lines $firstHeading->$secondHeading")
    }.let {
        Pair(it.first * -1, it.second * -1)
    }

    val shifted = Coord2(mid.x + shift.first, mid.y + shift.second)

    return shifted
}
