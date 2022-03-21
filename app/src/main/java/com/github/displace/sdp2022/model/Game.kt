package com.github.displace.sdp2022.model

import com.github.displace.sdp2022.gameComponents.Coordinate

open abstract class Game(goal: Coordinate) {
    abstract fun verify(test: Coordinate): Boolean
}
