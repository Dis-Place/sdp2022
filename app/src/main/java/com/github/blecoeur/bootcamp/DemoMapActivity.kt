package com.github.blecoeur.bootcamp

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker




class DemoMapActivity : AppCompatActivity() {

    private val ZOOM = 16.0
    private val EPFL_POS = GeoPoint(46.52,6.57)

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userLocation: Location

    /**
     * @param savedInstanceState
     * creates a map view with a marker, centered on EPFL position
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if(location != null){
                        userLocation = location
                    }
                }
        }


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