package com.github.displace.sdp2022.gameVersus

import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.gameComponents.Coordinates
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.gameVersus.GameVersusViewModel.Companion.CONTINUE
import com.github.displace.sdp2022.gameVersus.GameVersusViewModel.Companion.LOSE
import com.github.displace.sdp2022.gameVersus.GameVersusViewModel.Companion.WIN
import com.github.displace.sdp2022.model.GameVersus
import com.github.displace.sdp2022.util.math.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseError

class ClientServerLink(private val db : RealTimeDatabase) {

    val listenerManager: GameVersusListenerManager = GameVersusListenerManager()

    private val initGoal = Point(3.0, 5.0)
    private var gid = ""
    private var other = ""
    private var playerId = ""
    var game = GameVersus(initGoal, 0, 3, Constants.THRESHOLD.toDouble(), 2)

    private val posListener = object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.value!=null) {
                val coordsList = (dataSnapshot.value as List<Double>)
                if(coordsList.size >= 2) {
                    val coords = Point(coordsList[0],coordsList[1])
                    set(coords)
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    fun SendDataToOther(goal: Coordinates) {
        db.update("GameInstance/Game$gid/id:$playerId", "finish", CONTINUE)
        db.update("GameInstance/Game$gid/id:$playerId", "pos", listOf(goal.pos.first,goal.pos.second))
    }

    fun GetData(playerId: String, gid: String, other: String) {
        this.playerId = playerId
        this.gid = gid
        this.other = other

        db.addList("GameInstance/Game$gid/id:$other", "pos", posListener)
    }

    private fun set(goal: Coordinates) {
        game = GameVersus(
            goal,
            game.nbTry,
            game.nbTryMax,
            game.threshold,
            game.nbPlayer
        )
        listenerManager.invokeAll(game)
    }

    fun verify(test: Coordinates): Long {
        if (game.verify(test)) {
            endGame(WIN)
            return WIN
        } else {
            if (game.nbTry >= game.nbTryMax) {
                endGame(LOSE)
                return LOSE
            } else {
                game = GameVersus(
                    game.goal,
                    game.nbTry + 1,
                    game.nbTryMax,
                    game.threshold,
                    game.nbPlayer
                )
                return CONTINUE
            }
        }

    }

    fun endGame(winOrLose: Long) {
        db.removeList("GameInstance/Game$gid/id:$other", "pose", posListener)
        db.update("GameInstance/Game$gid/id:$playerId","finish",winOrLose)
    }
}