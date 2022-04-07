package com.github.displace.sdp2022.gameComponents

//com.github.displace.sdp2022.model of a point
class Point(x: Double, y: Double) : Coordinates {
    override val pos: Pair<Double, Double> = Pair(x, y)
}