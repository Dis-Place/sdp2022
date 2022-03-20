package com.github.displace.sdp2022.map

import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay


/**
 * handles a mapView where you can place a Marker by clicking on the com.github.displace.sdp2022.map,
 * and remove a Marker bw clicking on it
 */
class MarkerPlacerMapViewManager(private val mapView: MapView) : MapViewManager {
    val markerManager = MarkerManager(mapView)

    override fun mapView(): MapView {
        return mapView
    }

    override fun initMapView() {
        super.initMapView()

        val markerPlacerEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                return false
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                if(p!=null) {
                    markerManager.putMarker(p)
                    mapView.invalidate()
                }
                return false
            }
        }
        mapView.overlays.add(MapEventsOverlay(markerPlacerEventsReceiver))
    }
}