package com.github.displace.sdp2022.map

import android.graphics.Paint
import androidx.appcompat.content.res.AppCompatResources
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.gameComponents.Coordinates
import com.github.displace.sdp2022.util.math.CoordinatesUtil
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.constants.GeoConstants
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleDiskOverlay

/***
 * Composite of a Marker + disk marking the area covered by a position guess
 * @author LeoLgdr
 * @param mapView where the Pinpoint will be displayed
 * @param pos geographic coordinates at which to display the Pinpoint
 * @param radius radius of the covered area in meters
 * @param color to color the marker and disk accordingly
 */
class Pinpoint(private val mapView: MapView, private val pos: GeoPoint, private val radius: Int, private val color: Int) {

    private val marker = Marker(mapView)
    private val areaDisk = ScaleDiskOverlay(mapView.context,pos.clone(),radius,GeoConstants.UnitOfMeasure.meter)
    private var isDisplayed = false

    init {
        val diskPaint = Paint()
        diskPaint.color = color
        diskPaint.alpha = DISK_ALPHA
        areaDisk.setCirclePaint1(diskPaint)

        marker.icon = AppCompatResources.getDrawable(mapView.context,R.drawable.pinpoint_icon)
        marker.icon.setTint(color)
        marker.position = pos.clone()

        // the anchor y position is not exactly the bottom of the drawable because it has some transparent padding
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM * OFFSET_FACTOR_ANCHOR)
        marker.setOnMarkerClickListener { _, _ -> false }
    }

    fun removeOnClick(ref: MarkerManager.PinpointsRef) {
        marker.setOnMarkerClickListener { _, _ ->
            ref.remove(this)
            false
        }
    }

    /**
     * display on map
     */
    fun display() {
        if (!isDisplayed){
            mapView.overlayManager.add(areaDisk)
            mapView.overlayManager.add(marker)
            isDisplayed = true
            mapView.invalidate()
        }
    }

    /**
     * remove from map
     */
    fun remove() {
        if (isDisplayed){
            mapView.overlayManager.remove(marker)
            mapView.overlayManager.remove(areaDisk)
            isDisplayed = false
            mapView.invalidate()
        }
    }

    fun getGeoPos() : GeoPoint {
        return pos.clone()
    }

    fun get2DPos() : Coordinates {
        return CoordinatesUtil.coordinates(pos)
    }

    companion object {
        private const val DISK_ALPHA = 100 // opacity between 0 (invisible) and 255 (opaque)
        private const val OFFSET_FACTOR_ANCHOR = 0.90f // eyeballed
    }
}