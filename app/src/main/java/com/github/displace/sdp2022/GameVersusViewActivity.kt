package com.github.displace.sdp2022

import com.github.displace.sdp2022.gameComponents.GameEvent
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.gameVersus.GameVersusViewModel
import android.content.Intent
import android.os.Bundle
import android.view.View
import org.osmdroid.views.MapView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.map.MapViewManager
import com.github.displace.sdp2022.util.PreferencesUtil
import com.github.displace.sdp2022.util.gps.GPSPositionManager
import com.github.displace.sdp2022.util.gps.GeoPointListener
import org.osmdroid.config.Configuration.*
import java.util.ArrayList
import android.widget.Toast
import android.widget.ToggleButton
import org.osmdroid.util.GeoPoint

const val EXTRA_STATS = "com.github.displace.sdp2022.GAMESTAT"
const val EXTRA_RESULT = "com.github.displace.sdp2022.GAMERESULT"
const val EXTRA_MODE = "com.github.displace.sdp2022.GAMEMODE"
const val EXTRA_SCORE_P1 = "com.github.displace.sdp2022.SCOREP1"
const val EXTRA_SCORE_P2 = "com.github.displace.sdp2022.SCOREP2"

class GameVersusViewActivity : AppCompatActivity() {

    val game = GameVersusViewModel()

    private lateinit var mapView: MapView
    private lateinit var mapViewManager: MapViewManager
    private lateinit var gpsPositionManager: GPSPositionManager
    private lateinit var markerListener: GeoPointListener
    private lateinit var tryListener: GeoPointListener

    val statsList: ArrayList<String> = arrayListOf()
    val extras: Bundle = Bundle()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        game.handleEvent(GameEvent.OnStart(Point(46.52048, 6.56782),3,0))

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
                extras.putBoolean(EXTRA_RESULT, true)
                extras.putInt(EXTRA_SCORE_P1, 1)
                extras.putInt(EXTRA_SCORE_P2, 0)
                statsList.add("18:43")      // Example Time
                showGameSummaryActivity()
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
                        extras.putBoolean(EXTRA_RESULT, false)
                        extras.putInt(EXTRA_SCORE_P1, 0)
                        extras.putInt(EXTRA_SCORE_P2, 1)
                        statsList.add("15:04")      // Example Time
                        showGameSummaryActivity()
                    }
                }
            }
            }
        }

        gpsPositionManager = GPSPositionManager(this)

        mapViewManager.addCallOnLongClick(markerListener)
        mapViewManager.addCallOnLongClick(tryListener)

        findViewById<TextView>(R.id.TryText).apply { text =
            "neutral"
        }
    }

    //close the screen
    @Suppress("UNUSED_PARAMETER")
    fun closeButton(view: View) {
        val res = game.handleEvent(GameEvent.OnSurrend(0))
        if(res == 3) {
            val intent = Intent(this, GameListActivity::class.java)
            startActivity(intent)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun winButton(view: View) {
        val res = game.handleEvent(GameEvent.OnPointSelected(Point(46.52048, 6.56782)))
        if(res == 0) {
            extras.putBoolean(EXTRA_RESULT, true)
            extras.putInt(EXTRA_SCORE_P1, 1)
            extras.putInt(EXTRA_SCORE_P2, 0)
            statsList.add("18:43")      // Example Time
            showGameSummaryActivity()
        }
    }

    fun showGameSummaryActivity() {
        val intent = Intent(this, GameSummaryActivity::class.java)
        extras.putStringArrayList(EXTRA_STATS, statsList)
        extras.putString(EXTRA_MODE, "Versus")
        intent.putExtras(extras)
        startActivity(intent)
    }

}