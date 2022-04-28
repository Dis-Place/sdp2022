package com.github.displace.sdp2022.model

import com.github.displace.sdp2022.gameComponents.Coordinates

//com.github.displace.sdp2022.model of a gameversus

class GameVersus(
    val goal: Coordinates,
    val nbTry: Int,
    val nbTryMax: Int,
    val threshold: Double,
    val nbPlayer : Int
) : Game(goal,nbPlayer,threshold) {

    //verify if the test coordinates is on the goal or not
    override fun verify(test: Coordinates): Boolean {
        return goal.pos.first + threshold > test.pos.first &&
                goal.pos.first - threshold < test.pos.first &&
                goal.pos.second + threshold > test.pos.second &&
                goal.pos.second - threshold < test.pos.second

    }
}