package com.github.displace.sdp2022.map

import android.app.Activity
import com.github.displace.sdp2022.util.RandomColor
import com.github.displace.sdp2022.util.math.Constants.THRESHOLD
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

/**
 * Handling (creating,removing) Markers (ie, pinpoints) on a map (mapView) should always
 * be done through this class, which provides several methods
 * and abstracts over OSMdroid's API
 * @param mapView on which we put the markers
 * @author LeoLgdr
 */
class MarkerManager(private val mapView: MapView) {
    private var pinPointsMap = mutableMapOf<PinpointsRef,List<Pinpoint>>()
    val playerPinPointsRef = PinpointsRef()

    /**
     * adds a marker to a position. the marker is removable by click
     * this method is intended to be used for the player's guesses of the other player's position
     *
     * for putting the opponent's guesses, use addPinPoints
     * @param pos coordinates of the Marker
     */
    fun putMarker(pos: GeoPoint) {
        playerPinPointsRef.add(pos,true)
        mapView.invalidate()
    }

    /**
     * @return the Player's current pinpoints of the opponent's location
     */
    fun playerPinPoints(): List<GeoPoint>{
        return playerPinPointsRef.get()
    }

    /**
     * for ease of Marker handling
     */
    inner class PinpointsRef {

        /**
         * associated color on the map, with which all the Pinpoints of this ref will be drawn
         */
        private val color = RandomColor.next()

        /**
         * changes the set of associated markers to a new one.
         * the old markers are removed from the map,
         * new ones are put, corresponding to the new positions
         *
         * The new markers are NOT removed on click
         * @param newPositions
         */
        fun set(newPositions: List<GeoPoint>){
            clear()
            pinPointsMap[this] = newPositions.map { p ->
                val pinpoint = Pinpoint(mapView,p,THRESHOLD,color)
                pinpoint.display() /* call to display done here so that we do a single pass through the positions */
                pinpoint
            }
            mapView.invalidate()
        }

        /**
         * adds a Marker
         * @param position of the marker
         * @param isRemovedOnClick true iff the player can remove the marker by click
         */
        fun add(position: GeoPoint,isRemovedOnClick: Boolean){
            val pinPoints = pinPointsMap[this@PinpointsRef]?.toMutableList() ?: mutableListOf()
            val newPinpoint = Pinpoint(mapView,position,THRESHOLD,color)
            pinPoints.add(newPinpoint)
            if(isRemovedOnClick){
                newPinpoint.removeOnClick(this@PinpointsRef)
            }
            newPinpoint.display()
            pinPointsMap[this@PinpointsRef] = pinPoints.toList()
            mapView.invalidate()
        }

        /**
         * @return markers positions
         */
        fun get(): List<GeoPoint> {
            return pinPointsMap[this]?.map{ p -> p.getGeoPos()} ?:  listOf()
        }

        /**
         * remove all associated markers from the map
         */
        fun clear(){
            pinPointsMap[this@PinpointsRef]?.map { p -> p.remove() }
            pinPointsMap[this@PinpointsRef] = listOf()
        }

        /**
         * remove the specified marker
         */
        fun remove(pinpoint: Pinpoint){
            if(pinPointsMap[this@PinpointsRef]?.contains(pinpoint) == true){
                pinPointsMap[this@PinpointsRef] = pinPointsMap[this@PinpointsRef]!!.filter { p -> p != pinpoint }
                pinpoint.remove()
            }
        }
    }
}