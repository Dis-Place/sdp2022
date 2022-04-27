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

    private val goal = Point(3.0, 5.0)
    private var gid = ""
    private var other = ""
    private var playerId = ""
    var game = GameVersus(goal, 0, 3, Constants.THRESHOLD.toDouble(), 2)
    private lateinit var posXListener : ValueEventListener
    private lateinit var posYListener : ValueEventListener

    fun SendDataToOther(goal: Coordinates) {
        db.update("GameInstance/Game" + gid + "/id:" + playerId, "finish", CONTINUE)
        db.update("GameInstance/Game" + gid + "/id:" + playerId, "x", goal.pos.first)
        db.update("GameInstance/Game" + gid + "/id:" + playerId, "y", goal.pos.second)
    }

    fun GetData(playerId: String, gid: String, other: String) {
        this.playerId = playerId
        this.gid = gid
        this.other = other

        posXListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value!=null && dataSnapshot.value is Double) {
                    setX(dataSnapshot.value as Double)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        posYListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value!=null && dataSnapshot.value is Double) {
                    setY(dataSnapshot.value as Double)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        db.addList("GameInstance/Game" + gid + "/id:" + other, "x", posXListener)
        db.addList("GameInstance/Game" + gid + "/id:" + other, "y", posYListener)
    }

    private fun setX(x: Double) {
        game = GameVersus(
            Point(x, game.goal.pos.second),
            game.nbTry,
            game.nbTryMax,
            game.threshold,
            game.nbPlayer
        )
    }

    private fun setY(y: Double) {
        game = GameVersus(
            Point(game.goal.pos.first, y),
            game.nbTry,
            game.nbTryMax,
            game.threshold,
            game.nbPlayer
        )
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
        db.removeList("GameInstance/Game" + gid + "/id:" + other, "x", posXListener)
        db.removeList("GameInstance/Game" + gid + "/id:" + other, "y", posYListener)
        db.update("GameInstance/Game" + gid + "/id:" + playerId,"finish",winOrLose)
    }
}