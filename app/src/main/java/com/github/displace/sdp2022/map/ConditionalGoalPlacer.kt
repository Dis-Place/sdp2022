package com.github.displace.sdp2022.map

import com.github.displace.sdp2022.gameComponents.Coordinates
import com.github.displace.sdp2022.model.GameVersus
import com.github.displace.sdp2022.util.math.CoordinatesUtil
import org.osmdroid.views.MapView

/**
 * to show the goal position if it is not in the game area
 * @param mapView to place the goal marker
 * @param gameInstance current GameVersus Instance
 * @author LeoLgdr
 */
class ConditionalGoalPlacer(mapView: MapView, private val gameInstance: GameVersus) {
    private var isPlaced = false
    private val goalPositionMarker = GoalPositionMarker(mapView, CoordinatesUtil.geoPoint(gameInstance.goal))

    /**
     * places/remove marker depending on wether or not the player position is in GameArea
     * @param playerPos new player position
     */
    fun update(playerPos: Coordinates) {
        if(gameInstance.isInGameArea(playerPos) && !isPlaced){
            goalPositionMarker.add()
            isPlaced = true
        } else if(isPlaced) {
            goalPositionMarker.remove()
            isPlaced = false
        }
    }

    /**
     * places/removes goal marker depending on wether or not the player position is in GameArea
     * @param gameInstance new GameVersus instance
     * @param playerPos new player position
     */
    fun update(gameInstance: GameVersus, playerPos: Coordinates) {
        goalPositionMarker.set(CoordinatesUtil.geoPoint(playerPos))
        update(playerPos)
    }
}