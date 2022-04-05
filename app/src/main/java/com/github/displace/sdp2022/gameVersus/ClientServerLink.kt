package com.github.displace.sdp2022.gameVersus

import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.gameComponents.Coordinates
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.model.GameVersus
import java.lang.Error

class ClientServerLink {
    private val db = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase
    private val goal = Point(3.0, 5.0)
    var game = GameVersus(goal,0,3,0.0001,2)

    fun SendDataToOther(goal: Coordinates, playerId: Int) {
        db.update("GameInstance/GameTest/id:" + playerId,"x",goal.pos.first)
        db.update("GameInstance/GameTest/id:" + playerId,"y",goal.pos.second)
    }

    fun GetData(uid: Int) {
        db.referenceGet("GameInstance/GameTest/id:" + uid,"other").addOnSuccessListener { other ->
            db.referenceGet("GameInstance/GameTest/id:" + other.value, "x").addOnSuccessListener { ls ->
                val x = ls.value as Double
                setX(x)
            }
            db.referenceGet("GameInstance/GameTest/id:" + other.value, "y").addOnSuccessListener { ls ->
                val y = ls.value as Double
                setY(y)
            }
        }
    }

    private fun setX(x: Double){
        game = GameVersus(Point(x,game.goal.pos.second), game.nbTry, game.nbTryMax, game.threshold, game.nbPlayer)
    }

    private fun setY(y: Double){
        game = GameVersus(Point(game.goal.pos.first,y), game.nbTry, game.nbTryMax, game.threshold, game.nbPlayer)
    }

    fun verify(test: Coordinates): Int {
        if(game.verify(test)){
            return 0
        }else {
            if (game.nbTry >= game.nbTryMax) {
                return 2
            }else{
                game = GameVersus(game.goal, game.nbTry + 1, game.nbTryMax, game.threshold, game.nbPlayer)
                return 1
            }
        }

    }
}