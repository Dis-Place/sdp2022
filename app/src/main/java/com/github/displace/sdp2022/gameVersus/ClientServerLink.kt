package com.github.displace.sdp2022.gameVersus

import android.annotation.TargetApi
import android.os.Build
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.gameComponents.Coordinates
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.model.GameVersus
import com.google.firebase.database.*
import java.util.*

class ClientServerLink {
    private val server = RealTimeDatabase().newInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/") as RealTimeDatabase
    private var game = GameVersus(Point(0.0,0.0),3,0,3,0.1)

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    fun SendDataToOther(goal: Coordinates, photo: Int, uid: Int) {
        server.newGet("GameInstance/GameFor" + uid,"other").addOnSuccessListener { ls:Int ->
            server.update("GameInstance/GameFor" + ls + "/goal","x",goal.pos.first)
            server.update("GameInstance/GameFor" + ls + "/goal","y",goal.pos.second)
            server.update("GameInstance/GameFor" + ls + "/photo","photo",photo)
        }
    }

    fun GetData(uid : Int) {
        var x = 0.0
        var y = 0.0
        server.newGet("GameInstance/GameFor" + uid + "/goal","x").addOnSuccessListener { ls: Double ->
            x = ls
        }
        server.newGet("GameInstance/GameFor" + uid + "/goal","y").addOnSuccessListener { ls: Double ->
            y = ls
        }

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