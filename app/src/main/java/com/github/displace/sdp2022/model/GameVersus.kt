package com.github.displace.sdp2022.model

import com.github.displace.sdp2022.gameComponents.Coordinates
import com.github.displace.sdp2022.util.math.Constants
import com.github.displace.sdp2022.util.math.CoordinatesUtil

//com.github.displace.sdp2022.model of a gameversus

class GameVersus(
    val goal: Coordinates,
    val nbTry: Int,
    val nbTryMax: Int,
    val threshold: Double,
    val nbPlayer : Int
) : Game(goal,nbPlayer,threshold) {

    override fun verify(test: Coordinates): Boolean {
        return CoordinatesUtil.distance(goal,test) < threshold

    }

    fun isInGameArea(pos: Coordinates): Boolean {
        return CoordinatesUtil.distance(goal,pos) < Constants.GAME_AREA_RADIUS
    }

}