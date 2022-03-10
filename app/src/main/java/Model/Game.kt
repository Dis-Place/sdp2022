package Model

import GameComponents.Coordinate

open abstract class Game(goal : Coordinate){
    abstract fun verify(test: Coordinate) : Boolean
}
