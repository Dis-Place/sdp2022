package com.github.displace.sdp2022

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.map.MapViewManager
import com.github.displace.sdp2022.map.MarkerPlacerMapViewManager
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import com.github.displace.sdp2022.util.PreferencesUtil
import com.github.displace.sdp2022.util.gps.GPSPositionManager
import displace.sdp2022.R


class DemoMapActivity : AppCompatActivity() {

    private val ZOOM = 16.0
    private val EPFL_POS = GeoPoint(46.52048,6.56782)
    private lateinit var mapView : MapView
    private lateinit var mapViewManager : MapViewManager
    private lateinit var gpsPositionManager: GPSPositionManager

    /**
     * @param savedInstanceState
     * creates a com.github.displace.sdp2022.map view with a marker, centered on EPFL position
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
     * centers the com.github.displace.sdp2022.map on the GPS location of the user
     */
    fun centerGPS(view : View) {
        val gpsPos = gpsPositionManager.getPosition()
        if(gpsPos != null)
            mapViewManager.center(gpsPos)
    }
}