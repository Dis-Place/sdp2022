package com.github.displace.sdp2022.model

import gameComponents.Coordinate

//com.github.displace.sdp2022.model of a gameversus

class GameVersus(val goal : Coordinate,
                 val Photo : List<Double>,
                 val nbTry : Int,
                 val nbTryMax : Int,
                 val threshold : Double) : Game(goal) {

    override fun verify(test: Coordinate) : Boolean {
        return goal.pos.first + threshold * goal.pos.first > test.pos.first &&
                goal.pos.first - threshold * goal.pos.first < test.pos.first &&
                goal.pos.second + threshold * goal.pos.second > test.pos.second &&
                goal.pos.second - threshold * goal.pos.second < test.pos.second

    }
}