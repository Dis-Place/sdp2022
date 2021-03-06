package com.github.displace.sdp2022.map

import com.github.displace.sdp2022.util.listeners.Listener
import com.github.displace.sdp2022.util.listeners.ListenerManager
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay

/**
 * class that handles a mapView and provides a lightened API for Activities
 * @param mapView
 */
class MapViewManager(val mapView: MapView) {
    val listenersManager = ListenerManager<GeoPoint>()

    /**
     * @return current listeners called on long click
     */
    fun currentOnLongClickListeners() : List<Listener<GeoPoint>>{
        return listenersManager.listeners()
    }

    /**
     * add listener to be called on longClick on mapView
     * @param listener added to the listeners
     */
    fun addCallOnLongClick(listener: Listener<GeoPoint>){
        listenersManager.addCall(listener)
    }

    /**
     * removes listeners to be called on longClick on mapView
     * @param listeners removed from listeners
     */
    fun removeCallOnLongClick(listener: Listener<GeoPoint>){
        listenersManager.removeCall(listener)
    }

    /**
     * clears all listeners calls
     * @param listeners removed from listeners
     */
    fun clearOnLongClickCalls(){
        listenersManager.clearAllCalls()
    }

    init {
        mapView.overlayManager.clear()
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        center(DEFAULT_CENTER)
        zoom(DEFAULT_ZOOM)
        mapView.isTilesScaledToDpi = true
        mapView.setMultiTouchControls(true)

        val listenerHandler = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                return false
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                listenersManager.invokeAll(p)
                return false
            }
        }
        mapView.overlays.add(MapEventsOverlay(listenerHandler))
    }

    /**
     * centers the mapView on the specified point
     * @param geoPoint new center
     */
    fun center(geoPoint: GeoPoint) {
        mapView.controller.setCenter(geoPoint)
    }

    /**
     * sets the zooming factor
     * @param zoom new zooming factor
     */
    fun zoom(zoom: Double) {
        mapView.controller.setZoom(zoom)
    }

    companion object {
        const val DEFAULT_ZOOM = 16.0
        val DEFAULT_CENTER = GeoPoint(46.52048, 6.56782)
    }
}