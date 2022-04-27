package com.github.displace.sdp2022

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.gameComponents.Player
import com.github.displace.sdp2022.map.GPSLocationMarker
import com.github.displace.sdp2022.map.MapViewManager
import com.github.displace.sdp2022.map.MapViewManager.Companion.DEFAULT_CENTER
import com.github.displace.sdp2022.map.PinpointsManager
import com.github.displace.sdp2022.map.PinpointsDBHandler
import com.github.displace.sdp2022.util.PreferencesUtil
import com.github.displace.sdp2022.util.gps.GPSPositionManager
import com.github.displace.sdp2022.util.gps.GPSPositionUpdater
import com.github.displace.sdp2022.util.gps.GeoPointListener
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView


class DemoMapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var mapViewManager: MapViewManager
    private lateinit var gpsPositionUpdater: GPSPositionUpdater
    private lateinit var gpsPositionManager: GPSPositionManager
    private lateinit var gpsLocationMarker: GPSLocationMarker
    private lateinit var markerListener: GeoPointListener
    private lateinit var posToastListener: GeoPointListener
    private lateinit var pinpointsManager: PinpointsManager
    lateinit var mockPinpointsRef: PinpointsManager.PinpointsRef
    lateinit var remoteMockPinpointsRef: PinpointsManager.PinpointsRef
    private lateinit var dbHandler: PinpointsDBHandler
    private var useDB = false

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
        pinpointsManager = PinpointsManager(mapView)
        markerListener = GeoPointListener {p -> pinpointsManager.putMarker(p)}
        posToastListener = GeoPointListener { geoPoint -> Toast.makeText(this,String.format("( %.4f ; %.4f )",geoPoint.latitude,geoPoint.longitude),Toast.LENGTH_SHORT).show() }
        gpsPositionManager = GPSPositionManager(this)
        gpsPositionUpdater = GPSPositionUpdater(this,gpsPositionManager)
        gpsLocationMarker = GPSLocationMarker(mapView,gpsPositionManager)
        gpsLocationMarker.add()

        mockPinpointsRef = pinpointsManager.PinpointsRef()
        remoteMockPinpointsRef = pinpointsManager.PinpointsRef()
        val db = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false)
        dbHandler = PinpointsDBHandler(db as RealTimeDatabase,MOCK_GAME_INSTANCE_NAME, this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        gpsPositionUpdater.stopUpdates()
    }


    /**
     * centers the map on the GPS location of the user
     */

    @Suppress("UNUSED_PARAMETER")
    fun centerGPS(view: View) {

        val centerListener = object : GeoPointListener {
            override fun invoke(geoPoint: GeoPoint) {
                    mapViewManager.center(geoPoint)
                    gpsPositionManager.listenersManager.removeCall(this)
                }
        }

        gpsPositionManager.listenersManager.addCall(centerListener)
        gpsPositionManager.updateLocation()
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

    fun mapViewListeners() : List<GeoPointListener>{
        return mapViewManager.currentOnLongClickListeners()
    }

    fun markerManager(): PinpointsManager{
        return pinpointsManager
    }

    private fun displayMockMarkers(){
        mockPinpointsRef.set(MOCK_MARKERS_POSITIONS)
    }

    private fun removeMockMarkers(){
        mockPinpointsRef.clear()
    }

    fun toggleMockMarkers(view : View){
        val toggleButton = view as ToggleButton
        if(toggleButton.isChecked){
            displayMockMarkers()
        } else {
            removeMockMarkers()
        }
        if(useDB){
            dbHandler.updateDBPinpoints(MOCK_PLAYER.uid,mockPinpointsRef)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun updateRemoteMockPinPoints(view: View){
        if(useDB){
            dbHandler.enableAutoupdateLocalPinpoints(MOCK_PLAYER.uid,remoteMockPinpointsRef)
        }
    }

    fun toggleDB(view : View){
        val toggleButton = view as ToggleButton
        useDB = toggleButton.isChecked
    }

    companion object {
        val MOCK_MARKERS_POSITIONS = listOf(DEFAULT_CENTER, GeoPoint(6.5,-4.0), GeoPoint(6.7,47.0))
        const val MOCK_GAME_INSTANCE_NAME = "demoMapMock"
        val MOCK_PLAYER = Player(0.0,0.0,"dummy_A")
    }


}