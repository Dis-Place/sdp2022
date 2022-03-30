package com.github.displace.sdp2022.gameVersus

import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.gameComponents.Coordinates
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.model.GameVersus

class ClientServerLink {
    private val db = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/") as RealTimeDatabase
    private val goal = Point(3.0, 5.0)
    var game = GameVersus(goal,0,3,0.0001)

    fun SendDataToOther(goal: Coordinates, playerId: Int) {
        db.update("GameInstance/GameTest/id:" + playerId,"x",goal.pos.first)
        db.update("GameInstance/GameTest/id:" + playerId,"y",goal.pos.second)
        game = GameVersus(goal, game.nbTry, game.nbTryMax, game.threshold)
    }

    @Suppress("UNUSED_PARAMETER")
    fun GetData(oid: Int) {
        var x = 0.0
        var y = 0.0
        db.referenceGet("GameInstance/GameTest/id:" + oid,"x").addOnSuccessListener { ls ->
            x = ls.value as Double
        }
        db.referenceGet("GameInstance/GameTest/id:" + oid,"y").addOnSuccessListener { ls ->
            y = ls.value as Double
        }
        game =  GameVersus(Point(x,y), game.nbTry, game.nbTryMax, game.threshold)
    }

    fun verify(test: Coordinates): Int {
        if(game.verify(test)){
            return 0
        }else {
            if (game.nbTry >= game.nbTryMax) {
                return 2
            }else{
                game = GameVersus(game.goal, game.nbTry + 1, game.nbTryMax, game.threshold)
                return 1
            }
        }

    }
}