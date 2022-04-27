package com.github.displace.sdp2022.map

import com.github.displace.sdp2022.gameComponents.Coordinates
import com.github.displace.sdp2022.model.GameVersus
import com.github.displace.sdp2022.util.math.CoordinatesUtil
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

/**
 * to show the goal position if it is not in the game area
 * @param mapView to place the goal marker
 * @param gameInstance current GameVersus Instance
 * @author LeoLgdr
 */
class ConditionalGoalPlacer(mapView: MapView, private var gameInstance: GameVersus, private var playerPos: GeoPoint) {
    private val goalPositionMarker = GoalPositionMarker(mapView, CoordinatesUtil.geoPoint(gameInstance.goal))

    /**
     * places/remove marker depending on wether or not the player position is in GameArea
     * @param playerPos new player position
     */
    fun update(playerPos: GeoPoint) {
        if(!gameInstance.isInGameArea(CoordinatesUtil.coordinates(playerPos))){
            goalPositionMarker.add()
        } else {
           goalPositionMarker.remove()
        }
    }

    /**
     * places/removes goal marker depending on wether or not the player position is in GameArea
     * @param gameInstance new GameVersus instance
     */
    fun update(gameInstance: GameVersus) {
        this.gameInstance = gameInstance
        goalPositionMarker.set(CoordinatesUtil.geoPoint(gameInstance.goal))
        update(playerPos)
    }
}