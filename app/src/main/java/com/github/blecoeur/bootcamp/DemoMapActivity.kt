package com.github.blecoeur.bootcamp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import map.MapViewManager
import map.MarkerPlacerMapViewManager
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import util.PreferencesUtil
import util.gps.CoordinatesConversionUtil
import util.gps.GPSPositionManager


class DemoMapActivity : AppCompatActivity() {

    private val ZOOM = 16.0
    private val EPFL_POS = GeoPoint(46.52048,6.56782)
    private lateinit var mapView : MapView
    private lateinit var mapViewManager : MapViewManager
    private lateinit var gpsPositionManager: GPSPositionManager

    /**
     * @param savedInstanceState
     * creates a map view with a marker, centered on EPFL position
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferencesUtil.initOsmdroidPref(this)

        setContentView(R.layout.activity_demo_map)
        mapView = findViewById<MapView>(R.id.map)
        mapViewManager = MarkerPlacerMapViewManager(mapView)
        mapViewManager.initMapView()
        gpsPositionManager = GPSPositionManager(this)
    }


    /**
     * centers the map on the GPS location of the user
     */
    fun centerGPS(view : View) {
        val gpsPos = gpsPositionManager.getPosition()
        if(gpsPos != null)
            mapViewManager.center(gpsPos)
    }
}