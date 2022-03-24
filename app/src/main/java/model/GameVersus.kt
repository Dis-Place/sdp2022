package model

import gameComponents.Coordinate

//model of a gameversus

class GameVersus(val goal : Coordinate,
                 val Photo : Int,
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