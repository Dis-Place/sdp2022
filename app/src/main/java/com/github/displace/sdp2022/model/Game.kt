package com.github.displace.sdp2022.model

import com.github.displace.sdp2022.gameComponents.Coordinates

abstract class Game(goals: List<Coordinates>, nbPlayer: Int, threshold : Double) {
    abstract fun verify(test: Coordinates): Int
}