package com.github.displace.sdp2022.gameVersus

import com.github.displace.sdp2022.gameComponents.Coordinate
import com.github.displace.sdp2022.gameComponents.GameEvent
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.model.GameVersus

class GameVersusViewModel {

    //Test class until we got a real server side
    private val reseau = ClientServerLink()

    //handle the 3 different possibility
    fun handleEvent(event: GameEvent): Int {
        when (event) {
            is GameEvent.OnStart -> return SetGoal(event.Goal,event.Photo,event.PlayerId)
            is GameEvent.OnPointSelected -> return TryLocation(event.test)
            is GameEvent.OnSurrend -> return OnSurrend(event.PlayerId)
        }
    }

    //Set the goal at the start of the game (each player set the goal of the other)
    fun SetGoal(goal : Coordinate, photo: Int, playerId : Int) : Int {
        reseau.SendDataToOther(goal,photo,playerId)
        reseau.GetData(playerId)

        return 0
    }

    //When someone tap on the screen the location he think the goal is
    fun TryLocation(test : Coordinate) : Int {
        return reseau.verify(test)
    }

    //End the game and add a lose the the one who surrended
    fun OnSurrend(UserId: Int) : Int {
        return 3

}