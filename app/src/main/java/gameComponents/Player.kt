package gameComponents
import android.util.Pair
//model of a player

class Player(val x:Double, val y:Double, val id:Int ) : Coordinate{
    val uid = id
    override val pos: Pair<Double,Double> = Pair(x,y)
}