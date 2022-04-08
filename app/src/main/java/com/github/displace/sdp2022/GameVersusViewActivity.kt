package com.github.displace.sdp2022

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.gameComponents.GameEvent
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.gameVersus.GameVersusViewModel
import com.github.displace.sdp2022.map.MapViewManager
import com.github.displace.sdp2022.util.PreferencesUtil
import com.github.displace.sdp2022.util.gps.GPSPositionManager
import com.github.displace.sdp2022.util.gps.GPSPositionUpdater
import com.github.displace.sdp2022.util.gps.GeoPointListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.osmdroid.views.MapView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


const val EXTRA_STATS = "com.github.displace.sdp2022.GAMESTAT"
const val EXTRA_RESULT = "com.github.displace.sdp2022.GAMERESULT"
const val EXTRA_MODE = "com.github.displace.sdp2022.GAMEMODE"
const val EXTRA_SCORE_P1 = "com.github.displace.sdp2022.SCOREP1"
const val EXTRA_SCORE_P2 = "com.github.displace.sdp2022.SCOREP2"

class GameVersusViewActivity : AppCompatActivity() {

    val statsList: ArrayList<String> = arrayListOf()

    var goal = Point(46.52048,6.56782)
    val game = GameVersusViewModel()
    private val extras: Bundle = Bundle()

    private lateinit var mapView: MapView
    private lateinit var mapViewManager: MapViewManager
    private lateinit var gpsPositionUpdater: GPSPositionUpdater
    private lateinit var gpsPositionManager: GPSPositionManager
    private lateinit var markerListener: GeoPointListener

    private val db = RealTimeDatabase().noCacheInstantiate(
        "https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",
        false
    ) as RealTimeDatabase

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferencesUtil.initOsmdroidPref(this)
        setContentView(R.layout.activity_game_versus)

        game.handleEvent(GameEvent.OnStart(goal,intent.getStringExtra("uid")!!, intent.getStringExtra("gid")!!, intent.getStringExtra("other")!!))


        mapView = findViewById<MapView>(R.id.map)
        mapViewManager = MapViewManager(mapView)
        markerListener = GeoPointListener.markerPlacer(mapView)
        gpsPositionManager = GPSPositionManager(this)
        gpsPositionUpdater = GPSPositionUpdater(this,gpsPositionManager)
        gpsPositionUpdater.listenersManager.addCall(GeoPointListener { geoPoint ->  game.handleEvent(GameEvent.OnUpdate(intent.getStringExtra("uid")!!, Point(geoPoint.latitude,geoPoint.longitude)))})
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

        val endListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val x = dataSnapshot.getValue()
                if (x == 1L || x == -1L) {
                    db.delete("GameInstance", "Game" + intent.getStringExtra("gid")!!)
                    gpsPositionUpdater.listenersManager.clearAllCalls()
                    if (x == 1L) {
                        findViewById<TextView>(R.id.TryText).apply {
                            text =
                                "status : end of game"
                        }
                        extras.putBoolean(EXTRA_RESULT, false)
                        extras.putInt(EXTRA_SCORE_P1, 0)
                        extras.putInt(EXTRA_SCORE_P2, 1)
                        val current = LocalDateTime.now()
                        val formatted = current.format(formatter)
                        statsList.add(formatted)
                        showGameSummaryActivity()
                    } else {
                        findViewById<TextView>(R.id.TryText).apply { text = "win" }
                        extras.putBoolean(EXTRA_RESULT, true)
                        extras.putInt(EXTRA_SCORE_P1, 1)
                        extras.putInt(EXTRA_SCORE_P2, 0)
                        val current = LocalDateTime.now()
                        val formatted = current.format(formatter)
                        statsList.add(formatted)
                        showGameSummaryActivity()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        db.addList("GameInstance/Game" + intent.getStringExtra("gid")!! + "/id:" + intent.getStringExtra("other")!!,"finish",endListener)

        mapViewManager.addCallOnLongClick(markerListener)

        mapViewManager.addCallOnLongClick(GeoPointListener { geoPoint -> run {
                val res = game.handleEvent(
                    GameEvent.OnPointSelected(
                        intent.getStringExtra("uid")!!,
                        Point(geoPoint.latitude ,geoPoint.longitude )
                    )
                )
                if(res == 0){
                    gpsPositionUpdater.listenersManager.clearAllCalls()
                    findViewById<TextView>(R.id.TryText).apply { text = "win" }
                    extras.putBoolean(EXTRA_RESULT, true)
                    extras.putInt(EXTRA_SCORE_P1, 1)
                    extras.putInt(EXTRA_SCORE_P2, 0)
                    val current = LocalDateTime.now()
                    val formatted = current.format(formatter)
                    statsList.add(formatted)
                    showGameSummaryActivity()
                }else {
                    if (res == 1) {
                        findViewById<TextView>(R.id.TryText).apply {
                            text =
                                "status : fail, nombre d'essais restant : " + (4 - game.getNbEssai()) + " True : x=" + game.getGoal().pos.first + " y=" + game.getGoal().pos.second
                        }
                    } else {
                        if (res == 2) {
                            gpsPositionUpdater.listenersManager.clearAllCalls()
                            findViewById<TextView>(R.id.TryText).apply {
                                text =
                                    "status : end of game"
                            }
                            extras.putBoolean(EXTRA_RESULT, false)
                            extras.putInt(EXTRA_SCORE_P1, 0)
                            extras.putInt(EXTRA_SCORE_P2, 1)
                            val current = LocalDateTime.now()
                            val formatted = current.format(formatter)
                            statsList.add(formatted)
                            showGameSummaryActivity()
                        }
                    }
                }
            }})

        findViewById<TextView>(R.id.TryText).apply { text =
            "status : neutral, nombre d'essais restant : " + (4 - game.getNbEssai())
        }
    }


    //close the screen
    fun closeButton(view: View) {
        game.handleEvent(GameEvent.OnSurrend(intent.getStringExtra("uid")!!))
        gpsPositionUpdater.listenersManager.clearAllCalls()
        val intent = Intent(this, GameListActivity::class.java)
        startActivity(intent)
    }

    fun centerButton(view: View) {
        val gpsPos = gpsPositionManager.getPosition()
        if (gpsPos != null)
            mapViewManager.center(gpsPos)
    }

    private fun showGameSummaryActivity() {
        val intent = Intent(this, GameSummaryActivity::class.java)
        extras.putStringArrayList(EXTRA_STATS, statsList)
        extras.putString(EXTRA_MODE, "Versus")
        intent.putExtras(extras)
        startActivity(intent)
    }

}