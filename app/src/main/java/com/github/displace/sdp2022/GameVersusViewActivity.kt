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
import com.github.displace.sdp2022.map.PinpointsManager
import com.github.displace.sdp2022.map.PinpointsDBHandler
import com.github.displace.sdp2022.util.DateTimeUtil
import com.github.displace.sdp2022.util.PreferencesUtil
import com.github.displace.sdp2022.util.gps.GPSPositionManager
import com.github.displace.sdp2022.util.gps.GPSPositionUpdater
import com.github.displace.sdp2022.util.gps.GeoPointListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.util.*
import kotlin.collections.ArrayList


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
    private lateinit var other : Map<String,Any>
    private lateinit var pinpointsDBHandler: PinpointsDBHandler
    private lateinit var pinpointsManager: PinpointsManager
    private lateinit var opponentPinpoints: PinpointsManager.PinpointsRef
    private val calendar = Calendar.getInstance()

    private val db = RealTimeDatabase().noCacheInstantiate(
        "https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",
        false
    ) as RealTimeDatabase

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferencesUtil.initOsmdroidPref(this)
        setContentView(R.layout.activity_game_versus)

        mapView = findViewById<MapView>(R.id.map)
        mapViewManager = MapViewManager(mapView)
        markerListener = GeoPointListener.markerPlacer(mapView)
        gpsPositionManager = GPSPositionManager(this)
        gpsPositionUpdater = GPSPositionUpdater(this,gpsPositionManager)
        pinpointsManager = PinpointsManager(mapView)
        opponentPinpoints = pinpointsManager.PinpointsRef()
        gpsPositionManager.listenersManager.addCall(GeoPointListener { geoPoint ->
            game.handleEvent(GameEvent.OnUpdate(intent.getStringExtra("uid")!!,
                Point(geoPoint.latitude,geoPoint.longitude)))})

        centerButton(mapView) // to initialise the gps position
        //centerButton(mapView) // to set the center of the screen

        val endListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val x = dataSnapshot.getValue()
                if (x == 1L || x == -1L) {
                    db.delete("GameInstance", "Game" + intent.getStringExtra("gid")!!)
                    gpsPositionManager.listenersManager.clearAllCalls()
                    statsList.clear()
                    statsList.add(DateTimeUtil.currentTime())
                    if (x == 1L) {
                        findViewById<TextView>(R.id.TryText).apply {
                            text =
                                "status : end of game"
                        }
                        extras.putBoolean(EXTRA_RESULT, false)
                        extras.putInt(EXTRA_SCORE_P1, 0)
                        extras.putInt(EXTRA_SCORE_P2, 1)
                        showGameSummaryActivity()
                    } else {
                        findViewById<TextView>(R.id.TryText).apply { text = "win" }
                        extras.putBoolean(EXTRA_RESULT, true)
                        extras.putInt(EXTRA_SCORE_P1, 1)
                        extras.putInt(EXTRA_SCORE_P2, 0)
                        showGameSummaryActivity()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        
        pinpointsDBHandler = PinpointsDBHandler(db,"Game" + intent.getStringExtra("gid")!!, this)
        pinpointsDBHandler.initializePinpoints(intent.getStringExtra("uid")!!)
        pinpointsDBHandler.initializePinpoints(intent.getStringExtra("uid")!!)

        mapViewManager.addCallOnLongClick(GeoPointListener { geoPoint -> run {
                pinpointsManager.putMarker(geoPoint)
                val res = game.handleEvent(
                    GameEvent.OnPointSelected(
                        intent.getStringExtra("uid")!!,
                        Point(geoPoint.latitude ,geoPoint.longitude )
                    )
                )
                if(res == 0){
                    gpsPositionManager.listenersManager.clearAllCalls()
                    findViewById<TextView>(R.id.TryText).apply { text = "win" }
                    extras.putBoolean(EXTRA_RESULT, true)
                    extras.putInt(EXTRA_SCORE_P1, 1)
                    extras.putInt(EXTRA_SCORE_P2, 0)
                    statsList.clear()
                    statsList.add(DateTimeUtil.currentTime())
                    showGameSummaryActivity()
                }else {
                    if (res == 1) {
                        findViewById<TextView>(R.id.TryText).apply {
                            text =
                                "status : fail, nombre d'essais restant : " + (4 - game.getNbEssai()) + " True : x=" + game.getGoal().pos.first + " y=" + game.getGoal().pos.second
                        }
                        pinpointsDBHandler.updateDBPinpoints(intent.getStringExtra("uid")!!,pinpointsManager.playerPinPointsRef)
                    } else {
                        if (res == 2) {
                            gpsPositionManager.listenersManager.clearAllCalls()
                            findViewById<TextView>(R.id.TryText).apply {
                                text =
                                    "status : end of game"
                            }
                            extras.putBoolean(EXTRA_RESULT, false)
                            extras.putInt(EXTRA_SCORE_P1, 0)
                            extras.putInt(EXTRA_SCORE_P2, 1)
                            statsList.clear()
                            statsList.add(DateTimeUtil.currentTime())
                            showGameSummaryActivity()
                        }
                    }
                }
            }})

        db.referenceGet("GameInstance" , "Game" + intent.getStringExtra("gid")!!).addOnSuccessListener { gi ->
            other = ((gi as DataSnapshot).value as MutableMap<String,Any>).filter { id -> id.key != "id:" + intent.getStringExtra("uid")!! }
            other.toList().map { t ->

             }

            val t = other.toList()[0]

            val other = t.first.replace("id:","")
            db.addList("GameInstance/Game" + intent.getStringExtra("gid")!! + "/id:" + other,"finish",endListener)
            pinpointsDBHandler.enableAutoupdateLocalPinpoints(other,opponentPinpoints)
            game.handleEvent(GameEvent.OnStart(goal,intent.getStringExtra("uid")!!, intent.getStringExtra("gid")!!, other))
        }

        findViewById<TextView>(R.id.TryText).apply { text =
            "status : neutral, nombre d'essais restant : " + (4 - game.getNbEssai())
        }
    }


    //close the screen
    @Suppress("UNUSED_PARAMETER")
    fun closeButton(view: View) {
        game.handleEvent(GameEvent.OnSurrend(intent.getStringExtra("uid")!!))
        gpsPositionManager.listenersManager.clearAllCalls()
        val intent = Intent(this, GameListActivity::class.java)
        startActivity(intent)
    }

    @Suppress("UNUSED_PARAMETER")
    fun centerButton(view: View) {

        val centerListener = object : GeoPointListener {
            override fun invoke(geoPoint: GeoPoint) {
                mapViewManager.center(geoPoint)
                gpsPositionManager.listenersManager.removeCall(this)
            }
        }

        gpsPositionManager.listenersManager.addCall(centerListener)
        gpsPositionManager.updateLocation()
    }

    private fun showGameSummaryActivity() {
        val intent = Intent(this, GameSummaryActivity::class.java)
        extras.putStringArrayList(EXTRA_STATS, statsList)
        extras.putString(EXTRA_MODE, "Versus")
        intent.putExtras(extras)
        startActivity(intent)
    }

}