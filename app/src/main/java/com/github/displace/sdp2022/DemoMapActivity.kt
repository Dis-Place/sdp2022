package com.github.displace.sdp2022

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.map.MapViewManager
import com.github.displace.sdp2022.util.PreferencesUtil
import com.github.displace.sdp2022.util.gps.GPSPositionManager
import com.github.displace.sdp2022.util.gps.GeoPointListener
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView


class DemoMapActivity : AppCompatActivity() {

    private val ZOOM = 16.0
    private val EPFL_POS = GeoPoint(46.52048, 6.56782)
    private lateinit var mapView: MapView
    private lateinit var mapViewManager: MapViewManager
    private lateinit var gpsPositionManager: GPSPositionManager
    private lateinit var markerListener: GeoPointListener
    private lateinit var posToastListener: GeoPointListener

    /**
     * @param savedInstanceState
     * creates a map view with a marker, centered on EPFL position
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferencesUtil.initOsmdroidPref(this)

        setContentView(R.layout.activity_demo_map)
        mapView = findViewById<MapView>(R.id.map)
        mapViewManager = MapViewManager(mapView)
        markerListener = GeoPointListener.markerPlacer(mapView)
        posToastListener = GeoPointListener { geoPoint -> Toast.makeText(this,String.format("( %.4f ; %.4f )",geoPoint.latitude,geoPoint.longitude),Toast.LENGTH_SHORT).show() }
        gpsPositionManager = GPSPositionManager(this)
    }


    /**
     * centers the map on the GPS location of the user
     */

    @Suppress("UNUSED_PARAMETER")
    fun centerGPS(view: View) {
        val gpsPos = gpsPositionManager.getPosition()
        if (gpsPos != null)
            mapViewManager.center(gpsPos)
    }

    @Suppress("UNUSED_PARAMETER")
    fun toggleMarkers(view: View) {
        listenerToggle(findViewById(R.id.markersToggleButton),markerListener)
    }

    @Suppress("UNUSED_PARAMETER")
    fun toggleToastPos(view: View) {
        listenerToggle(findViewById(R.id.toastPosToggleButton),posToastListener)
    }

    @Suppress("UNUSED_PARAMETER")
    fun disableAllListeners(view: View) {
        toggleOff(findViewById(R.id.markersToggleButton))
        toggleOff(findViewById(R.id.toastPosToggleButton))
        mapViewManager.clearOnLongClickCalls()
    }

    private fun listenerToggle(toggleButton: ToggleButton, listener: GeoPointListener) {
        if(toggleButton.isChecked){
            mapViewManager.addCallOnLongClick(listener)
        } else {
            mapViewManager.removeCallOnLongClick(listener)
        }
    }

    private fun toggleOff(toggleButton: ToggleButton) {
        if(toggleButton.isChecked) {
            toggleButton.toggle()
        }
    }


}