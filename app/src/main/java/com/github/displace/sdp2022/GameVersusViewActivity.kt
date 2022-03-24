package com.github.displace.sdp2022

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.gameComponents.GameEvent
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.gameVersus.GameVersusViewModel
import com.github.displace.sdp2022.map.MapViewManager
import com.github.displace.sdp2022.util.PreferencesUtil
import com.github.displace.sdp2022.util.gps.GPSPositionManager
import com.github.displace.sdp2022.util.gps.GeoPointListener
import org.osmdroid.views.MapView


class GameVersusViewActivity : AppCompatActivity() {

    val game = GameVersusViewModel()

    private lateinit var mapView: MapView
    private lateinit var mapViewManager: MapViewManager
    private lateinit var gpsPositionManager: GPSPositionManager
    private lateinit var markerListener: GeoPointListener
    private lateinit var tryListener: GeoPointListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PreferencesUtil.initOsmdroidPref(this)

        setContentView(R.layout.activity_demo_map)
        mapView = findViewById<MapView>(R.id.map)
        mapViewManager = MapViewManager(mapView)
        markerListener = GeoPointListener.markerPlacer(mapView)
        tryListener = GeoPointListener { geoPoint -> run {
            val res = game.handleEvent(GameEvent.OnPointSelected(Point(geoPoint.latitude,geoPoint.longitude)))

            if(res == 0){
                findViewById<TextView>(R.id.TryText).apply {
                    text =
                            "win"
                }
            }else{
                if(res == 1){ //failed
                    findViewById<TextView>(R.id.TryText).apply {
                        text =
                                "fail"
                    }
                }else{
                    if(res == 2){ //more than 3 tri
                        findViewById<TextView>(R.id.TryText).apply {
                            text =
                                    "end of game"
                        }
                    }
                }
            }
            }
        }

        gpsPositionManager = GPSPositionManager(this)

        mapViewManager.addCallOnLongClick(markerListener)
        mapViewManager.addCallOnLongClick(tryListener)

        game.SetGoal(Point(46.52048, 6.56782),3,0)

        val tryTextView =  findViewById<TextView>(R.id.TryText).apply { text =
            "neutral"
        }
    }

    //close the screen
    @Suppress("UNUSED_PARAMETER")
    fun closeButton(view: View) {
        val res = game.handleEvent(GameEvent.OnSurrend(3))
        if(res == 3) {
            val intent = Intent(this, GameListActivity::class.java)
            startActivity(intent)
        }
    }

}