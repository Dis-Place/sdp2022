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
class ConditionalGoalPlacer(private val mapView: MapView, private var gameInstance: GameVersus, private var playerPos: GeoPoint) {
    private var goalPositionMarkers = listOf<GoalPositionMarker>()

    /**
     * places/remove marker depending on wether or not the player position is in GameArea
     * @param playerPos new player position
     */
    fun update(playerPos: GeoPoint) {
        var i = 0
        print("\n no inside nok nok \n")
        goalPositionMarkers.forEach { goalPositionMarker ->
            if (!gameInstance.isInGameArea(CoordinatesUtil.coordinates(playerPos),i)) {
                print("X\n D ca morche pas heeh \n")
                goalPositionMarker.add()
            } else {
                goalPositionMarker.remove()
            }
            i += 1
        }
    }

    /**
     * places/removes goal marker depending on wether or not the player position is in GameArea
     * @param gameInstance new GameVersus instance
     */
    fun update(gameInstance: GameVersus) {
        this.gameInstance = gameInstance
        goalPositionMarkers.forEach { goal ->
            goal.remove()
        }
        goalPositionMarkers = listOf()
        gameInstance.goals.forEach { goal ->
            print("\n ${goal.pos} \n")
            goalPositionMarkers = goalPositionMarkers.plus(GoalPositionMarker(mapView,CoordinatesUtil.geoPoint(goal)))
        }
        update(playerPos)
    }
}