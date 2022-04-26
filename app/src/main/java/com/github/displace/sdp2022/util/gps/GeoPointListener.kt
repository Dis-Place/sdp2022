package com.github.displace.sdp2022.util.gps

import com.github.displace.sdp2022.map.PinpointsManager
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

/**
 * The intented use is to bound some code execution to changes in a GeoPoint
 * @author LeoLgdr
 */
fun interface GeoPointListener {
    fun invoke(geoPoint: GeoPoint)

    companion object {
        fun markerPlacer(mapView: MapView) : GeoPointListener {
            val pinpointsManager = PinpointsManager(mapView)
            return GeoPointListener { geoPoint -> pinpointsManager.putMarker(geoPoint) }
        }
    }
}