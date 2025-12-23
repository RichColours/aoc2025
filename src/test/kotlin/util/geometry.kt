package util

import kotlin.math.pow
import kotlin.math.sqrt

data class Coord3(val x: Double, val y: Double, val z: Double)

data class Distance3(val p1: Coord3, val p2: Coord3, val distance: Double)

fun distance(p1: Coord3, p2: Coord3) = sqrt((p2.x - p1.x).pow(2) + (p2.y - p1.y).pow(2) + (p2.z - p1.z).pow(2))

