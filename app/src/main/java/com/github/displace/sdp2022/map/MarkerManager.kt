package com.github.displace.sdp2022.map

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/**
 * Handling (creating,removing) Markers (ie, pinpoints) on a map (mapView) should always
 * be done through this class, which provides several methods
 * and abstracts over OSMdroid's API
 * @param mapView on which we put the markers
 * @author LeoLgdr
 */
class MarkerManager(private val mapView: MapView) {
    private var pinPointsMap = mutableMapOf<PinpointsRef,List<Marker>>()
    private val playerPinPointsRef = PinpointsRef()

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

    private fun newMarker(pos: GeoPoint, isRemovedOnClick: Boolean, pinpointsRef: PinpointsRef): Marker{
        val marker = Marker(mapView)
        marker.position = pos.clone()
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        mapView.overlayManager.add(marker)
        if(isRemovedOnClick){
            removeOnClick(marker, pinpointsRef)
        } else {
            marker.setOnMarkerClickListener {_,_ -> false}
        }
        return marker
    }

    private fun removeOnClick(marker: Marker, pinpointsRef: PinpointsRef){
        marker.setOnMarkerClickListener { _, _ ->
            pinpointsRef.remove(marker)
            false
        }
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
         * changes the set of associated markers to a new one.
         * the old markers are removed from the map,
         * new ones are put, corresponding to the new positions
         *
         * The new markers are NOT removed on click
         * @param newPositions
         */
        fun set(newPositions: List<GeoPoint>){
            clear()
            pinPointsMap[this] = newPositions.map{ p -> newMarker(p,false,this)}
            mapView.invalidate()
        }

        /**
         * adds a Marker
         * @param position of the marker
         * @param isRemovedOnClick true iff the player can remove the marker by click
         */
        fun add(position: GeoPoint,isRemovedOnClick: Boolean){
            val pinPoints = pinPointsMap[this]?.toMutableList() ?: mutableListOf()
            pinPoints.add(newMarker(position,isRemovedOnClick,this))
            pinPointsMap[this] = pinPoints.toList()
            mapView.invalidate()
        }

        /**
         * @return markers positions
         */
        fun get(): List<GeoPoint> {
            return pinPointsMap[this]?.map{ m -> m.position.clone()} ?:  listOf()
        }

        /**
         * remove all associated markers from the map
         */
        fun clear(){
            pinPointsMap[this]?.map { m -> mapView.overlayManager.remove(m) }
            mapView.invalidate()
        }

        /**
         * remove the specified marker
         */
        fun remove(marker: Marker){
            if(pinPointsMap[this]?.contains(marker) == true){
                pinPointsMap[this] = pinPointsMap[this]!!.filter { m -> m != marker }
                mapView.overlayManager.remove(marker)
                mapView.invalidate()
            }
        }
    }
}