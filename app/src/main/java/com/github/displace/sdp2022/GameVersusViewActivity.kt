package com.github.displace.sdp2022

import android.annotation.SuppressLint
import com.github.displace.sdp2022.gameComponents.GameEvent
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.gameVersus.GameVersusViewModel
import android.content.Intent
import android.os.Bundle
import android.view.View
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.res.Configuration
import android.os.Parcelable
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.github.displace.sdp2022.map.MapViewManager
import com.github.displace.sdp2022.util.gps.GPSPositionManager
import com.github.displace.sdp2022.util.gps.GPSPositionUpdater
import com.github.displace.sdp2022.util.gps.GeoPointListener
import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import java.util.ArrayList


const val EXTRA_STATS = "com.github.displace.sdp2022.GAMESTAT"
const val EXTRA_RESULT = "com.github.displace.sdp2022.GAMERESULT"
const val EXTRA_MODE = "com.github.displace.sdp2022.GAMEMODE"
const val EXTRA_SCORE_P1 = "com.github.displace.sdp2022.SCOREP1"
const val EXTRA_SCORE_P2 = "com.github.displace.sdp2022.SCOREP2"

class GameVersusViewActivity : AppCompatActivity() {

    val statsList: ArrayList<String> = arrayListOf()

    var goal = Point(46.52048,6.56782)
    val game = GameVersusViewModel()
    val extras: Bundle = Bundle()
    private val ZOOM = 16.0
    private val EPFL_POS = GeoPoint(46.52048,6.56782)

    private lateinit var mapView: MapView
    private lateinit var mapViewManager: MapViewManager
    private lateinit var gpsPositionUpdater: GPSPositionUpdater
    private lateinit var gpsPositionManager: GPSPositionManager
    private lateinit var markerListener: GeoPointListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game_versus)

        game.handleEvent(GameEvent.OnStart(goal,0))


        mapView = findViewById<MapView>(R.id.map)
        mapViewManager = MapViewManager(mapView)
        markerListener = GeoPointListener.markerPlacer(mapView)
        gpsPositionManager = GPSPositionManager(this)
        gpsPositionUpdater = GPSPositionUpdater(this,gpsPositionManager)
        gpsPositionUpdater.listenersManager.addCall(markerListener)

        mapViewManager.addCallOnLongClick(markerListener)

        mapViewManager.addCallOnLongClick(GeoPointListener { geoPoint -> run {
                val res = game.handleEvent(
                    GameEvent.OnPointSelected(
                        0,
                        Point(0.0, 0.0)
                    )
                )//geoPoint.latitude as Long,geoPoint.longitude as Long)))
                print("ok")
                if (res == 1) {
                    val tryTextView = findViewById<TextView>(R.id.TryText).apply {
                        text =
                            "fail"
                    }
                } else {
                    if (res == 2) {
                        val tryTextView = findViewById<TextView>(R.id.TryText).apply {
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
            }})

        findViewById<TextView>(R.id.TryText).apply { text =
            "neutral"
        }
    }


    //close the screen
    fun closeButton(view: View) {
        game.handleEvent(GameEvent.OnSurrend(0))
        val intent = Intent(this, GameListActivity::class.java)
        startActivity(intent)
    }

    //close the screen
    fun triButtonFail(view: View) {
        val res = game.handleEvent(GameEvent.OnPointSelected(0,Point(13.0,14.0)))
        if(res == 1){
            val tryTextView =  findViewById<TextView>(R.id.TryText).apply { text =
                "fail"
            }
        }else{
            if(res == 2){
                val tryTextView =  findViewById<TextView>(R.id.TryText).apply { text =
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

    //close the screen
    fun triButtonWin(view: View) {
        val res = game.handleEvent(GameEvent.OnPointSelected(0,goal))
        if(res == 0){
            val tryTextView =  findViewById<TextView>(R.id.TryText).apply { text =
                "win"
            }
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