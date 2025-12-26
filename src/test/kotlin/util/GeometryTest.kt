package util

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test
import util.PointIndexedGeometry.rectangleContainsPoint

class GeometryTest {

    @Test
    fun testAreaInt() {
        assertThat(area(Coord2<Int>(3, 4), Coord2(9, 8)))
            .isEqualTo(24)
    }

    @Test
    fun testAreaInt2() {
        assertThat(area(Coord2<Int>(11, 1), Coord2(2, 5)))
            .isEqualTo(36)
    }

    @Test
    fun testAreaInclusiveInt1() {
        assertThat(areaInclusive(Coord2<Int>(11, 1), Coord2(2, 5)))
            .isEqualTo(50)
    }

    @Test
    fun test_pointsDirectionLeft1() {
        assertThat(pointsDirection(Coord2(0.0, 0.0), Coord2(2.0, 2.0), Coord2(6.0, 5.0)))
            .isEqualTo(-1)
    }

    @Test
    fun test_pointsDirectionLeft2() {
        assertThat(pointsDirection(Coord2(0.0, 0.0), Coord2(2.0, 2.0), Coord2(0.0, 4.0)))
            .isEqualTo(-1)
    }

    @Test
    fun test_pointsDirectionRight1() {
        assertThat(pointsDirection(Coord2(0.0, 0.0), Coord2(2.0, 2.0), Coord2(4.0, 5.0)))
            .isEqualTo(1)
    }

    @Test
    fun test_pointsDirectionStraight1() {
        assertThat(pointsDirection(Coord2(0.0, 0.0), Coord2(2.0, 2.0), Coord2(5.0, 5.0)))
            .isEqualTo(0)
    }

    @Test
    fun test_normaliseRectangleCoords() {

        assertThat(normaliseRectangleCoords(Coord2(1, 1) to Coord2(3, 3)))
            .isEqualTo(Coord2(1, 1) to Coord2(3, 3))

        assertThat(normaliseRectangleCoords(Coord2(1, 3) to Coord2(3, 1)))
            .isEqualTo(Coord2(1, 1) to Coord2(3, 3))
    }

    @Test
    fun test_rectangleContainsPoint() {

        assertThat(rectangleContainsPoint(Coord2(0, 0), Coord2(5, 5), Coord2(3, 3)))
            .isTrue()

    }

    @Test
    fun test_lineTouchesRectangle1() {
        val rect = Coord2(2, 3) to Coord2(9, 5)
        val line = Coord2(1, 2) to Coord2(6, 2)
        assertThat(
            PointIndexedGeometry.lineTouchesRectangle(
                rect.first,
                rect.second,
                Line(line.first, line.second)
            )
        ).isFalse()
    }


}