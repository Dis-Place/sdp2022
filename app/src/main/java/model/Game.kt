package model

import gameComponents.Coordinate

open abstract class Game(goal : Coordinate){
    abstract fun verify(test: Coordinate) : Boolean
}