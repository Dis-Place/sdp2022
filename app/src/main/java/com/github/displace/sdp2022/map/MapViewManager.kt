package com.github.displace.sdp2022.map

import com.github.displace.sdp2022.util.gps.GeoPointListener
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay

/**
 * class that handles mapViews and provides a lightened API for Activities
 * @param mapView
 * @author LeoLgdr
 */
class MapViewManager(val mapView: MapView) {
    private val listeners = mutableListOf<GeoPointListener>()

    /**
     * @return current listeners called on long click
     */
    fun currentOnLongClickListeners() : List<GeoPointListener>{
        return listeners.toList()
    }

    /**
     * add listeners to be called on longClick on mapView
     * @param listeners added to the listeners
     */
    fun addCallOnLongClick(vararg listeners : GeoPointListener){
        this.listeners.addAll(listeners)
    }

    /**
     * removes listeners to be called on longClick on mapView
     * @param listeners removed from listeners
     */
    fun removeCallOnLongClick(vararg listeners : GeoPointListener){
        this.listeners.removeAll(listeners)
    }

    /**
     * clears all listeners calls
     * @param listeners removed from listeners
     */
    fun clearOnLongClickCalls(){
        listeners.clear()
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

            override fun longPressHelper(p: GeoPoint?): Boolean {
                if (p != null) {
                    for(l in listeners){
                        //calling all the listeners on long click
                        l.invoke(p)
                    }
                    mapView.invalidate()
                }
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
        mapView.controller.setZoom(DEFAULT_ZOOM)
    }

    companion object {
        const val DEFAULT_ZOOM = 16.0
        val DEFAULT_CENTER = GeoPoint(46.52048, 6.56782)
    }
}