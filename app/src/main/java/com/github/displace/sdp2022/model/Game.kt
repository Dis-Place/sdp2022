package com.github.displace.sdp2022.model

import com.github.displace.sdp2022.gameComponents.Coordinates

open abstract class Game(goal: Coordinates) {
    abstract fun verify(test: Coordinates): Boolean
}
