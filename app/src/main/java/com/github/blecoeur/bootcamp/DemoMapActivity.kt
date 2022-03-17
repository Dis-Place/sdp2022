package com.github.blecoeur.bootcamp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import map.MapViewManager
import map.MarkerPlacerMapViewManager
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import util.PreferencesUtil


class DemoMapActivity : AppCompatActivity() {

    private val ZOOM = 16.0
    private val EPFL_POS = GeoPoint(46.52048,6.56782)
    private lateinit var mapViewManager : MapViewManager

    /**
     * @param savedInstanceState
     * creates a map view with a marker, centered on EPFL position
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferencesUtil.initOsmdroidPref(this)

        setContentView(R.layout.activity_demo_map)
        val mapView = findViewById<MapView>(R.id.map)
        mapViewManager = MarkerPlacerMapViewManager(mapView)
        mapViewManager.initMapView()
    }

    fun mapViewManager() : MapViewManager {
        return mapViewManager
    }
}