package com.github.displace.sdp2022.map

import androidx.appcompat.content.res.AppCompatResources
import com.github.displace.sdp2022.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/**
 * represents a goal position on a mapView as a special marker
 * @param mapView to place the marker on
 * @param pos where to place the marker
 */
class GoalPositionMarker(private val mapView: MapView, private val pos: GeoPoint) {

    // marker should only be set and placed if mapView is in a correct state,
    // i.e., when there is no exception when instantiating the marker
    private val marker : Marker? = try {Marker(mapView)} catch (e: Exception) {null}

    init {
        if(marker!=null) {
            marker.icon = AppCompatResources.getDrawable(mapView.context, R.drawable.goal_position_icon)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            marker.setOnMarkerClickListener { _, _ -> false }
            marker.position = pos
        }
    }

    /**
     * place the marker on the mapView
     */
    fun add() {
        if(marker!=null && !mapView.overlays.contains(marker)) {
            mapView.overlayManager.add(marker)
            mapView.invalidate()
        }
    }

    /**
     * move the marker to specified position
     * @param position new marker position
     */
    fun set(position: GeoPoint) {
        if(marker!= null) {
            marker.position = position
        }
    }

    /**
     * remove marker from mapView
     */
    fun remove() {
        if(marker!= null) {
            mapView.overlayManager.remove(marker)
            mapView.invalidate()
        }
    }
}