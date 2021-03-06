package com.github.displace.sdp2022.map

import android.graphics.Paint
import androidx.appcompat.content.res.AppCompatResources
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.util.gps.GPSPositionManager
import com.github.displace.sdp2022.util.listeners.Listener
import com.github.displace.sdp2022.util.math.Constants
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.constants.GeoConstants
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleDiskOverlay

/**
 * marks the user's gps position
 * @param gpsPositionManager to attach the Marker updates
 * @param mapView used to display the Marker
 */
class GPSLocationMarker(private val mapView: MapView, private val gpsPositionManager: GPSPositionManager) {

    private var marker: Marker = Marker(mapView)
    private var areaDisk: ScaleDiskOverlay = ScaleDiskOverlay(mapView.context, marker.position, Constants.CLICKABLE_AREA_RADIUS, GeoConstants.UnitOfMeasure.meter)

    private val updateMarker = Listener<GeoPoint> { geoPoint ->
        if(!mapView.overlayManager.contains(marker)) {
            mapView.overlayManager.add(marker)
        }
        marker.position = geoPoint
        mapView.overlayManager.remove(areaDisk)
        areaDisk = ScaleDiskOverlay(mapView.context, geoPoint, Constants.CLICKABLE_AREA_RADIUS, GeoConstants.UnitOfMeasure.meter)
        val diskPaint = Paint()
        diskPaint.alpha = DISK_ALPHA
        areaDisk.setCirclePaint1(diskPaint)
        mapView.overlayManager.add(areaDisk)
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

    companion object {
        private const val DISK_ALPHA = 40 // opacity between 0 (invisible) and 255 (opaque)
    }

}