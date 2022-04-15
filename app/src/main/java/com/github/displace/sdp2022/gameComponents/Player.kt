package com.github.displace.sdp2022.gameComponents

//com.github.displace.sdp2022.model of a player

class Player(val x: Double, val y: Double, val id: String) : Coordinates {
    val uid = id
    override val pos: Pair<Double, Double> = Pair(x, y)
}