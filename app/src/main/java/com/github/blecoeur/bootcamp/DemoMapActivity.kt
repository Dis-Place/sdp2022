package com.github.blecoeur.bootcamp

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker




class DemoMapActivity : AppCompatActivity() {

    private val ZOOM = 16.0
    private val EPFL_POS = GeoPoint(46.52,6.57)

    /**
     * @param savedInstanceState
     * creates a map view with a marker, centered on EPFL position
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_demo_map)
        val map = findViewById<MapView>(R.id.map)
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);

        //to identify our app when downloading the map tiles (ie. pieces of the map)
        getInstance().setUserAgentValue(this.getPackageName())


        //adding marker
        val startMarker = Marker(map)
        startMarker.position = EPFL_POS
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        map.overlays.add(startMarker)

        map.controller.setCenter(EPFL_POS)
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map.setTilesScaledToDpi(true) //scaling tiles in order to see them well at any zoom scale

        //setting zoom
        map.getController().setZoom(ZOOM)
    }

}