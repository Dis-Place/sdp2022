package gameComponents
import android.util.Pair
//com.github.displace.sdp2022.model of a player

class Player(val x:Double, val y:Double, val id:Int ) : Coordinate{
    val uid = id
    override val pos: Pair<Double,Double> = Pair(x,y)
}