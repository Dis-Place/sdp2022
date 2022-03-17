package gameComponents

import android.util.Pair

//model of a player

class Player(x:Double, y:Double, id : Int) : Coordinate{
    val uid = id; //An id link to an account

    override val pos: Pair<Double,Double> = Pair(x,y) //His localisation
}