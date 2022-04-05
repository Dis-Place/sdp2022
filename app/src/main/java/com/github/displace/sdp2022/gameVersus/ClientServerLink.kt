package com.github.displace.sdp2022.gameVersus

import android.annotation.TargetApi
import android.os.Build
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.gameComponents.Coordinates
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.model.GameVersus


class ClientServerLink {
    private val server = RealTimeDatabase().instantiate(
        "https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",
        false
    ) as RealTimeDatabase
    private var game = GameVersus(Point(0.0, 0.0), 3, 0, 3, 0.1)

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    fun SendDataToOther(goal: Coordinates, photo: Int, uid: Int) {
        server.referenceGet("GameInstance/GameFor" + uid, "other")
            .addOnSuccessListener { ls: Any? ->
                val ido = ls as Int
                server.update("GameInstance/GameFor" + ido + "/goal", "x", goal.pos.first)
                server.update("GameInstance/GameFor" + ido + "/goal", "y", goal.pos.second)
                server.update("GameInstance/GameFor" + ido + "/photo", "photo", photo)
            }
    }

    fun GetData(uid: Int) {
        var x = 0.0
        var y = 0.0
        server.referenceGet("GameInstance/GameFor" + uid + "/goal", "x")
            .addOnSuccessListener { ls: Any? ->
                x = ls as Double
            }
        server.referenceGet("GameInstance/GameFor" + uid + "/goal", "y")
            .addOnSuccessListener { ls: Any? ->
                y = ls as Double
            }

        game = GameVersus(Point(x, y), 3, 0, 3, 0.1)
    }

    fun verify(test: Coordinates): Int {
        if (game.verify(test)) {
            return 0
        }
        if (game.nbTry >= game.nbTryMax) {
            return 2
        }
        game = GameVersus(game.goal, game.Photo, (game.nbTry + 1), game.nbTryMax, game.threshold)
        return 1
    }
}