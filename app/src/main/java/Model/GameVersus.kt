package Model

import GameComponents.Coordinate

//model of a gameversus

class GameVersus(val goal : Coordinate,
                 val Photo : List<Double>,
                 val nbTry : Int,
                 val nbTryMax : Int,
                 val threshold : Double) : Game(goal) {

    override fun verify(test: Coordinate) : Boolean {
        return goal.pos[0] + threshold * goal.pos[0] > test.pos[0] &&
                goal.pos[0] - threshold * goal.pos[0] < test.pos[0] &&
                goal.pos[1] + threshold * goal.pos[1] > test.pos[1] &&
                goal.pos[1] - threshold * goal.pos[1] < test.pos[1]

    }
}