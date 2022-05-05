package com.github.displace.sdp2022.gameVersus

import com.github.displace.sdp2022.gameComponents.Coordinates
import com.github.displace.sdp2022.gameComponents.GameEvent
import com.github.displace.sdp2022.gameComponents.Player
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.model.GameVersus
import kotlin.random.Random


class GameVersusViewModel(private val clientServerLink: ClientServerLink) {

    //Test class until we got a real server side
    private var gid = ""
    private var order = 0.0
    private lateinit var actualPos : Coordinates

    //handle the 3 different possibility
    fun handleEvent(event: GameEvent): Long {
        when (event) {
            is GameEvent.OnStart -> return SetGoal(event.Goal, event.PlayerId, event.gid, event.other, event.nbPlayer)
            is GameEvent.OnPointSelected -> return TryLocation(event.PlayerId, event.test)
            is GameEvent.OnSurrend -> return OnSurrend(event.PlayerId)
            is GameEvent.OnUpdate -> return UpdatePos(event.goal,event.PlayerId)
        }
    }

    //Set the goal at the start of the game (each player set the goal of the other)
    fun SetGoal(goal: Coordinates, playerId: String, gid: String, other: String, nbPlayer : Long): Long {
        this.gid = gid
        clientServerLink.GetData(playerId, gid, other, nbPlayer)
        clientServerLink.SendDataToOther(goal)
        return CONTINUE
    }

    //When someone tap on the screen the location he think the goal is
    fun TryLocation(UserId: String, test: Coordinates): Long {
        return clientServerLink.verify(test)
    }

    //End the game and add a lose the the one who surrended
    fun OnSurrend(UserId: String): Long {
        clientServerLink.endGame(LOSE)

        return LOSE
    }

    //update the actual position of the player
    fun UpdatePos(pos : Coordinates, id: String): Long {
        actualPos = pos
        clientServerLink.SendDataToOther(pos)

        return CONTINUE
    }

    fun getNbEssai() : Int {
        return clientServerLink.game.nbTry
    }

    fun getPos() : Coordinates {
        return actualPos
    }

    fun getGameInstance() : GameVersus {
        return clientServerLink.game
    }

    companion object {
        const val CONTINUE = 0L
        const val LOSE = -1L
        const val WIN = 1L
    }

}