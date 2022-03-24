package com.github.displace.sdp2022.gameVersus

import android.annotation.TargetApi
import android.os.Build
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.gameComponents.Coordinates
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.model.GameVersus



class ClientServerLink {
    private val server = RealTimeDatabase().instantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/") as RealTimeDatabase
    private var game = GameVersus(Point(0.0,0.0),3,0,3,0.1)

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    fun SendDataToOther(goal: Coordinates, photo: Int, uid: Int) {
        val ls = server.get("GameInstance/GameFor" + uid,"other")
        server.update("GameInstance/GameFor" + ls + "/goal","x",goal.pos.first)
        server.update("GameInstance/GameFor" + ls + "/goal","y",goal.pos.second)
        server.update("GameInstance/GameFor" + ls + "/photo","photo",photo)

    }

    fun GetData(uid : Int) {
        val x = server.get("GameInstance/GameFor" + uid + "/goal","x") as Double
        val y = server.get("GameInstance/GameFor" + uid + "/goal","y") as Double

        game = GameVersus(Point(x,y),3,0,3,0.1)
    }

    fun verify(test: Coordinates) : Int{
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