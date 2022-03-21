package com.github.displace.sdp2022.gameComponents

import android.util.Pair
import com.github.displace.sdp2022.gameComponents.Coordinate

//com.github.displace.sdp2022.model of a point
class Point(x: Double, y: Double) : Coordinate {
    override val pos: Pair<Double, Double> = Pair(x, y)
}