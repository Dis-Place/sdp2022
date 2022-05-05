package com.github.displace.sdp2022.gameVersus

import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.gameComponents.Coordinates
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.gameVersus.GameVersusViewModel.Companion.CONTINUE
import com.github.displace.sdp2022.gameVersus.GameVersusViewModel.Companion.LOSE
import com.github.displace.sdp2022.gameVersus.GameVersusViewModel.Companion.WIN
import com.github.displace.sdp2022.model.GameVersus
import com.github.displace.sdp2022.util.math.Constants
import com.github.displace.sdp2022.util.math.CoordinatesUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseError

class ClientServerLink(private val db : RealTimeDatabase, private val order: Double) {

    val listenerManager: GameVersusListenerManager = GameVersusListenerManager()

    private val initGoal = listOf(Point(3.0, 5.0))
    private var gid = ""
    private var others = listOf("")
    private var playerId = ""
    var game = GameVersus(initGoal, 0, 3, Constants.THRESHOLD.toDouble(), 2)
    private var otherToInt = mapOf<String,Int>()
    private var pos = 0L
    private var nbPlayer = 2L
    private var orderToOrder = mapOf<Double,Int>()

    //listener that update the position of the goal.
    private val posListener = object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.value!=null) {
                val coordsList = (dataSnapshot.value as List<Double>)
                if(coordsList.size >= 2) {
                    val coords = Point(coordsList[0],coordsList[1])
                    set(coords, coordsList[2])
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    //send the actual position of the player and initialise finish at the continue value
    //mainly used at the start of the game
    fun SendDataToOther(goal: Coordinates) {
        db.update("GameInstance/Game$gid/id:$playerId", "pos", listOf(goal.pos.first,goal.pos.second, order))
    }

    //get the position of the other player
    fun GetData(playerId: String, gid: String, other: String, nbPlayer: Long) {
        this.playerId = playerId
        this.gid = gid
        this.others = this.others.plus(other)
        this.nbPlayer = nbPlayer

        db.addList("GameInstance/Game$gid/id:$other", "pos", posListener)
    }

    //set the new coordinate of the game goal
    private fun set(newGoal: Coordinates, newOrder: Double) {
        var goals = listOf<Coordinates>()

        if(pos < 2L){
            if(orderToOrder.keys.contains(newOrder)){
                return
            }
            orderToOrder = orderToOrder.plus(Pair(newOrder,pos.toInt()))
        }

        if(game.goals.size != (nbPlayer-1).toInt()) {
            for (i in pos downTo 0) {
                val currentPos = (pos - i)
                if (currentPos == pos) {
                    goals = goals.plus(newGoal)
                } else {
                    goals = goals.plus(game.goals[currentPos.toInt()])
                }
            }
            pos += 1
        }else{
            var i = 0
            game.goals.forEach { goal ->
                if (i == orderToOrder[newOrder]) {
                    goals = goals.plus(newGoal)
                } else {
                    goals = goals.plus(game.goals[i])
                }
                i += 1
            }
        }

        game = GameVersus(
            goals,
            game.nbTry,
            game.nbTryMax,
            game.threshold,
            nbPlayer.toInt()
        )
        listenerManager.invokeAll(game)
    }

    //verify if the test coordiantes is the position of the other player
    // 3 possibility : 0 => test == position of the goal, 1 => test != position and 2 => you missed the max number of time and lost
    fun verify(test: Coordinates): Long {
        val testResult = game.verify(test)

        print( "\n $testResult \n")
        if(testResult >=0) print( "\n ${orderToOrder.filter { (x, y) -> y == testResult }.keys.first()} \n")
        if (testResult >= 0) {
            val res = orderToOrder.filter { (x, y) -> y == testResult }.keys.first()
            db.update("GameInstance/Game$gid/id:$playerId", "finish", res)
            return WIN
        } else if (game.nbTry >= game.nbTryMax) {
            endGame(LOSE)
            return LOSE
        } else {
            game = GameVersus(
                game.goals,
                game.nbTry + 1,
                game.nbTryMax,
                game.threshold,
                game.nbPlayer
            )
            return CONTINUE
        }
    }

    //remove all the listener
    fun endGame(winOrLose: Long) {
        others.forEach{
            other -> db.removeList("GameInstance/Game$gid/id:$other", "pose", posListener)
        }
        db.update("GameInstance/Game$gid/id:$playerId","finish",winOrLose)
    }
}