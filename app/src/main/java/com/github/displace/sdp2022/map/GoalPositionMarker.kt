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
 * @author LeoLgdr
 */
class GoalPositionMarker(private val mapView: MapView, private val pos: GeoPoint) {

    private val marker : Marker = Marker(mapView)

    init {
        marker.icon = AppCompatResources.getDrawable(mapView.context, R.drawable.goal_position_icon)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        marker.setOnMarkerClickListener { _, _ -> false }
    }

    /**
     * place the marker on the mapView
     */
    fun add() {
        mapView.overlayManager.add(marker)
        mapView.invalidate()
    }

    /**
     * remove marker from mapView
     */
    fun remove() {
        if(mapView.overlayManager.remove(marker)){
            mapView.invalidate()
        }
    }
}