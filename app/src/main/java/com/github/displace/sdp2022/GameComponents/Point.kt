package gameComponents

import android.util.Pair

//com.github.displace.sdp2022.model of a point
class Point(x: Double, y: Double) : Coordinate {
    override val pos: Pair<Double,Double> = Pair(x,y)
}