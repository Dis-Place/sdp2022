package com.github.displace.sdp2022.gameVersus

import com.github.displace.sdp2022.gameComponents.Coordinates
import com.github.displace.sdp2022.gameComponents.GameEvent
import com.github.displace.sdp2022.model.GameVersus

class GameVersusViewModel {

    //Test class until we got a real server side

    private val reseau = ClientServerLink()

    //handle the 3 different possibility
    fun handleEvent(event: GameEvent): Int {
        when (event) {
            is GameEvent.OnStart -> return SetGoal(event.Goal, event.PlayerId)
            is GameEvent.OnPointSelected -> return TryLocation(event.PlayerId, event.test)
            is GameEvent.OnSurrend -> return OnSurrend(event.PlayerId)
        }
    }

    //Set the goal at the start of the game (each player set the goal of the other)
    fun SetGoal(goal: Coordinates, playerId: Int): Int {
        reseau.GetData(playerId)
        reseau.SendDataToOther(goal, playerId)
        return 0
    }

    //When someone tap on the screen the location he think the goal is
    fun TryLocation(UserId: Int, test: Coordinates): Int {
        return reseau.verify(test)
    }

    //End the game and add a lose the the one who surrended
    fun OnSurrend(UserId: Int): Int {
        println("user : " + UserId + " Surrended")
        println("fin du match")

        return 0
    }

    fun getGoal(): Coordinates {
        return reseau.game.goal
    }

    fun getNbEssai() : Int {
        return reseau.game.nbTry
    }

}