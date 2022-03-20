package com.github.displace.sdp2022.map

import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView

/**
 * interface that handles mapViews and provides a lightened API for Activities
 */
interface MapViewManager {

    /**
     * @return MapView managed by the MapViewManager
     */
    fun mapView() : MapView

    /**
     * initializes the mapView (sets initial center & zoom)
     */
    fun initMapView() {
        mapView().zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        mapView().setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        center(DEFAULT_CENTER)
        zoom(DEFAULT_ZOOM)
        mapView().isTilesScaledToDpi = true
        mapView().setMultiTouchControls(true)
    }

    /**
     * centers the mapView on the specified point
     * @param geoPoint new center
     */
    fun center(geoPoint: GeoPoint) {
        mapView().controller.setCenter(geoPoint)
    }

    /**
     * sets the zooming factor
     * @param zoom new zooming factor
     */
    fun zoom(zoom: Double) {
        mapView().controller.setZoom(DEFAULT_ZOOM)
    }

    companion object {
        const val DEFAULT_ZOOM = 16.0
        val DEFAULT_CENTER =  GeoPoint(46.52048,6.56782)
    }
}