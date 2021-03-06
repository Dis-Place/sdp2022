package com.github.displace.sdp2022

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.gameComponents.Player
import com.github.displace.sdp2022.map.GPSLocationMarker
import com.github.displace.sdp2022.map.MapViewManager
import com.github.displace.sdp2022.map.MapViewManager.Companion.DEFAULT_CENTER
import com.github.displace.sdp2022.map.PinpointsManager
import com.github.displace.sdp2022.map.PinpointsDBHandler
import com.github.displace.sdp2022.util.PreferencesUtil
import com.github.displace.sdp2022.util.ThemeManager
import com.github.displace.sdp2022.util.gps.GPSPositionManager
import com.github.displace.sdp2022.util.gps.GPSPositionUpdater
import com.github.displace.sdp2022.util.listeners.Listener
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

/**
 * dummy Activity mostly used for (manual) testing
 * of maps functionalities
 */
class DemoMapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var mapViewManager: MapViewManager
    private lateinit var gpsPositionUpdater: GPSPositionUpdater
    private lateinit var gpsPositionManager: GPSPositionManager
    private lateinit var gpsLocationMarker: GPSLocationMarker
    private lateinit var markerListener: Listener<GeoPoint>
    private lateinit var posToastListener: Listener<GeoPoint>
    private lateinit var pinpointsManager: PinpointsManager
    lateinit var mockPinpointsRef: PinpointsManager.PinpointsRef
    lateinit var remoteMockPinpointsRef: PinpointsManager.PinpointsRef
    private lateinit var dbHandler: PinpointsDBHandler

    /**
     * @param savedInstanceState
     * creates a map view with a marker, centered on EPFL position
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyChosenTheme(this)
        super.onCreate(savedInstanceState)
        PreferencesUtil.initOsmdroidPref(this)

        setContentView(R.layout.activity_demo_map)
        mapView = findViewById<MapView>(R.id.map)
        mapViewManager = MapViewManager(mapView)
        val clickSoundPlayer = MediaPlayer.create(this, R.raw.zapsplat_sound_design_hit_punchy_bright_71725)
        pinpointsManager = PinpointsManager(mapView,clickSoundPlayer)
        markerListener = Listener<GeoPoint> {p -> pinpointsManager.putMarker(p)}
        posToastListener = Listener<GeoPoint> { geoPoint -> Toast.makeText(this,String.format("( %.4f ; %.4f )",geoPoint.latitude,geoPoint.longitude),Toast.LENGTH_SHORT).show() }
        gpsPositionManager = GPSPositionManager(this)
        gpsPositionUpdater = GPSPositionUpdater(this,gpsPositionManager)
        gpsLocationMarker = GPSLocationMarker(mapView,gpsPositionManager)
        gpsLocationMarker.add()

        mockPinpointsRef = pinpointsManager.PinpointsRef()
        remoteMockPinpointsRef = pinpointsManager.PinpointsRef()

        // | HOW TO GET A DATABASE INSTANCE
        // v
        val db = DatabaseFactory.getDB(intent)

        dbHandler = PinpointsDBHandler(db,MOCK_GAME_INSTANCE_NAME, this)
        dbHandler.initializePinpoints(MOCK_PLAYER.id)
        dbHandler.enableAutoupdateLocalPinpoints(MOCK_PLAYER.id,remoteMockPinpointsRef)
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
        gpsPositionManager.listenersManager.addCallOnce{ geoPoint ->
            mapViewManager.center(geoPoint)
        }
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

    private fun listenerToggle(toggleButton: ToggleButton, listener: Listener<GeoPoint>) {
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

    fun mapViewListeners() : List<Listener<GeoPoint>>{
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
        dbHandler.updateDBPinpoints(MOCK_PLAYER.id,mockPinpointsRef)
    }

    fun toggleMock(view : View) {
        val toggleButton = view as ToggleButton
        if(!toggleButton.isChecked) {
            gpsPositionManager.mockProvider(DEFAULT_CENTER)
        } else {
            gpsPositionManager.unmockProvider()
        }
    }

    companion object {
        val MOCK_MARKERS_POSITIONS = listOf(DEFAULT_CENTER, GeoPoint(6.5,-4.0), GeoPoint(6.7,47.0))
        const val MOCK_GAME_INSTANCE_NAME = "demoMapMock"
        val MOCK_PLAYER = Player(0.0,0.0,"dummy_A")
    }


}