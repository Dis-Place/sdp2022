package gameVersus

import android.annotation.TargetApi
import android.os.Build
import gameComponents.Coordinate
import gameComponents.Point
import model.GameVersus
import com.github.blecoeur.bootcamp.RealTimeDatabase

class ClientServerLink {
    private val server = RealTimeDatabase().newInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/") as RealTimeDatabase
    private var game = GameVersus(Point(0.0,0.0),3,0,3,0.1)

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    fun SendDataToOther(goal: Coordinate, photo: Int, uid: Int) {
        server.newGet("GameInstance/GameFor" + uid,"other").addOnSuccessListener { ls:Int ->
            server.update("GameInstance/GameFor" + ls + "/goal","x",goal.pos.first)
            server.update("GameInstance/GameFor" + ls + "/goal","y",goal.pos.second)
            server.update("GameInstance/GameFor" + ls + "/photo","photo",photo)
        }
    }

    fun GetData(uid : Int) {
        var x = 0.0
        var y = 0.0
        server.newGet("GameInstance/GameFor" + uid + "/goal","x").addOnSuccessListener { ls ->
            x = ls
        }
        server.newGet("GameInstance/GameFor" + uid + "/goal","y").addOnSuccessListener { ls ->
            y = ls
        }

        game = GameVersus(Point(x,y),3,0,3,0.1)
    }

    fun verify(test: Coordinate) : Int{
        if(game.verify(test)){
            return 0
        }
        if(game.nbTry >= game.nbTryMax) {
            return 2
        }
        game = GameVersus(game.goal,game.Photo,(game.nbTry + 1),game.nbTryMax,game.threshold)
        return 1
    }
}