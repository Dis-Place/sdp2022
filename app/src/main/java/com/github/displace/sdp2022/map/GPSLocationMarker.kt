package com.github.displace.sdp2022.map

import androidx.appcompat.content.res.AppCompatResources
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.util.gps.GPSPositionManager
import com.github.displace.sdp2022.util.gps.GeoPointListener
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/**
 * marks the user's gps position
 * @param gpsPositionManager to attach the Marker updates
 * @param mapView used to display the Marker
 * @author LeoLgdr
 */
class GPSLocationMarker(private val mapView: MapView, private val gpsPositionManager: GPSPositionManager) {

    private var marker: Marker = Marker(mapView)

    private val updateMarker = GeoPointListener { geoPoint ->
        if(!marker.isDisplayed) {
            mapView.overlayManager.add(marker)
        }
        marker.position = geoPoint
        mapView.invalidate()
    }

    init {
        marker.icon = AppCompatResources.getDrawable(mapView.context, R.drawable.gps_location_icon)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        marker.setOnMarkerClickListener { _, _ -> false }
    }


    /**
     * schedule marker position updates each time the gps position of gpsPositionManager is updated
     */
    fun add() {
        gpsPositionManager.listenersManager.addCall(updateMarker)
    }

}