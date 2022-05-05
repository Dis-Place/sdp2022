package com.github.displace.sdp2022.model

import com.github.displace.sdp2022.gameComponents.Coordinates
import com.github.displace.sdp2022.util.math.Constants
import com.github.displace.sdp2022.util.math.CoordinatesUtil

//com.github.displace.sdp2022.model of a gameversus

class GameVersus(
    val goals: List<Coordinates>,
    val nbTry: Int,
    val nbTryMax: Int,
    val threshold: Double,
    val nbPlayer : Int
) : Game(goals,nbPlayer,threshold) {

    //verify if the test coordinates is on the goal or not
    override fun verify(test: Coordinates): Int {
        var i = 0
        goals.forEach { goal ->
            print("\n ${test} \n")
            print("\n ${goal.pos} \n")
            print("\n $i \n")
            print("\n ${CoordinatesUtil.distance(goal,test)} \n")
            if(CoordinatesUtil.distance(goal,test) < threshold){
                return i
            }else{
                i += 1
            }
        }
        return -1


    }

    fun isInGameArea(pos: Coordinates, goal : Int): Boolean {
        return CoordinatesUtil.distance(goals[goal], pos) < Constants.GAME_AREA_RADIUS
    }

}