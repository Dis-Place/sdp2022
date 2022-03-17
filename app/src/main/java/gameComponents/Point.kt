package gameComponents

import android.util.Pair

//model of a point
class Point(x: Double, y: Double) : Coordinate {
    override val pos: Pair<Double,Double> = Pair(x,y)
}