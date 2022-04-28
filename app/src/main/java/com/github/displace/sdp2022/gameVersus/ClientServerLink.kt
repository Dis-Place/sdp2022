package com.github.displace.sdp2022.gameVersus

import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.gameComponents.Coordinates
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.model.GameVersus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseError

class ClientServerLink {
    private val db = RealTimeDatabase().noCacheInstantiate(
        "https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",
        false
    ) as RealTimeDatabase
    private val goal = Point(3.0, 5.0)
    private var gid = ""
    private var other = ""
    private var playerId = ""
    var game = GameVersus(goal, 0, 3, 0.0001, 2)
    private lateinit var posXListener : ValueEventListener
    private lateinit var posYListener : ValueEventListener

    //send all the important data to the database, the fact that the game is not finished and the actual position of the player.
    //Mostly used at the start of the game
    fun SendDataToOther(goal: Coordinates) {
        db.update("GameInstance/Game" + gid + "/id:" + playerId, "finish", 0)
        db.update("GameInstance/Game" + gid + "/id:" + playerId, "x", goal.pos.first)
        db.update("GameInstance/Game" + gid + "/id:" + playerId, "y", goal.pos.second)
    }

    //get the position of the other player.
    fun GetData(playerId: String, gid: String, other: String) {
        this.playerId = playerId
        this.gid = gid
        this.other = other

        posXListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val x = dataSnapshot.getValue()
                if(x is Double) {
                    setX(x)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        posYListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val y = dataSnapshot.getValue()
                if(y is Double) {
                    setY(y)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        db.addList("GameInstance/Game" + gid + "/id:" + other, "x", posXListener)
        db.addList("GameInstance/Game" + gid + "/id:" + other, "y", posYListener)
    }

    //set a new x coordinate to the game goal
    private fun setX(x: Double) {
        game = GameVersus(
            Point(x, game.goal.pos.second),
            game.nbTry,
            game.nbTryMax,
            game.threshold,
            game.nbPlayer
        )
    }

    //set a new y coordinate to the game goal
    private fun setY(y: Double) {
        game = GameVersus(
            Point(game.goal.pos.first, y),
            game.nbTry,
            game.nbTryMax,
            game.threshold,
            game.nbPlayer
        )
    }

    //verify if the coordinate pointed by the player is the position of the other player.
    // 3 possibility : 0 => yes it's the position of the other player, 1 => no it's not the position of the other player
    // and 2 => we tried the max number of time possible and failed to find the position of the other player.
    fun verify(test: Coordinates): Int {
        if (game.verify(test)) {
            endGame(1)
            return 0
        } else {
            if (game.nbTry >= game.nbTryMax) {
                endGame(-1)
                return 2
            } else {
                game = GameVersus(
                    game.goal,
                    game.nbTry + 1,
                    game.nbTryMax,
                    game.threshold,
                    game.nbPlayer
                )
                return 1
            }
        }

    }

    //remove all the listener
    fun endGame(winOrLose: Int) {
        db.removeList("GameInstance/Game" + gid + "/id:" + other, "x", posXListener)
        db.removeList("GameInstance/Game" + gid + "/id:" + other, "y", posYListener)
        db.update("GameInstance/Game" + gid + "/id:" + playerId,"finish",winOrLose)
    }
}