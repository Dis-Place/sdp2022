package com.github.displace.sdp2022.map

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MarkerManager(private val mapView: MapView) {

    fun putMarker(pos: GeoPoint) {
        val marker = Marker(mapView)
        marker.position = pos
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        mapView.overlayManager.add(marker)
        marker.setOnMarkerClickListener { mkr, mapV ->
            mapV.overlayManager.remove(mkr)
            mapV.invalidate()
            false
        }
    }
}