package com.github.displace.sdp2022

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.gameComponents.GameEvent
import com.github.displace.sdp2022.gameComponents.Player
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.gameVersus.GameVersusViewModel
import com.github.displace.sdp2022.map.MapViewManager
import com.github.displace.sdp2022.map.MarkerManager
import com.github.displace.sdp2022.map.PinpointsDBCommunicationHandler
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
    private lateinit var other : Map<String,Any>
    private lateinit var pinpointHandler: PinpointsDBCommunicationHandler
    private lateinit var marker: MarkerManager
    private lateinit var markerOther: MarkerManager

    private val db = RealTimeDatabase().noCacheInstantiate(
        "https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",
        false
    ) as RealTimeDatabase

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferencesUtil.initOsmdroidPref(this)
        setContentView(R.layout.activity_game_versus)

        //initialisation of the viewer and manager.
        mapView = findViewById<MapView>(R.id.map)
        mapViewManager = MapViewManager(mapView)
        markerListener = GeoPointListener.markerPlacer(mapView)
        gpsPositionManager = GPSPositionManager(this)
        gpsPositionUpdater = GPSPositionUpdater(this,gpsPositionManager)
        marker = MarkerManager(mapView)
        markerOther = MarkerManager(mapView)

        //add a listener that update the position of the player regulary
        gpsPositionUpdater.listenersManager.addCall(GeoPointListener { geoPoint ->
            game.handleEvent(GameEvent.OnUpdate(intent.getStringExtra("uid")!!,
                Point(geoPoint.latitude,geoPoint.longitude)))})

        centerButton(mapView) // to initialise the gps position
        //centerButton(mapView) // to set the center of the screen

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

        //listener that check if the game is finish either because the other lost, quit or win.
        val endListener = object : ValueEventListener {

            //only happen when the finish value of the other player change
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val x = dataSnapshot.getValue()
                //two possibility : 1 the other player won, -1: the other player lost or quit.
                if (x == 1L || x == -1L) {
                    //same part for both, we destroy the game instance, clear the listener and add the date to the summary.
                    db.delete("GameInstance", "Game" + intent.getStringExtra("gid")!!)
                    gpsPositionUpdater.listenersManager.clearAllCalls()
                    statsList.clear()
                    val current = LocalDateTime.now()
                    val formatted = current.format(formatter)
                    statsList.add(formatted)

                    //show the right summary in function of if we lose or win.
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

        //start the handler of the pinpoint
        pinpointHandler = PinpointsDBCommunicationHandler(db,"Game" + intent.getStringExtra("gid")!!)
        pinpointHandler.start(intent.getStringExtra("uid")!!)

        //add a listener that check if the point we tried is on the other player or not.
        mapViewManager.addCallOnLongClick(GeoPointListener { geoPoint -> run {
                marker.putMarker(geoPoint)
                val res = game.handleEvent(
                    GameEvent.OnPointSelected(
                        intent.getStringExtra("uid")!!,
                        Point(geoPoint.latitude ,geoPoint.longitude )
                    )
                )

                // 3 possibility : 0 => we found the other player and won, 1 => we missed the other player, 2 => we tried 4 time without success and lost.
                if(res == 0){
                    gpsPositionUpdater.listenersManager.clearAllCalls()
                    findViewById<TextView>(R.id.TryText).apply { text = "win" }
                    extras.putBoolean(EXTRA_RESULT, true)
                    extras.putInt(EXTRA_SCORE_P1, 1)
                    extras.putInt(EXTRA_SCORE_P2, 0)
                    val current = LocalDateTime.now()
                    val formatted = current.format(formatter)
                    statsList.clear()
                    statsList.add(formatted)
                    showGameSummaryActivity()
                }else {
                    if (res == 1) {
                        findViewById<TextView>(R.id.TryText).apply {
                            text =
                                "status : fail, nombre d'essais restant : " + (4 - game.getNbEssai()) + " True : x=" + game.getGoal().pos.first + " y=" + game.getGoal().pos.second
                        }
                        pinpointHandler.updateDBPinpoints(intent.getStringExtra("uid")!!,marker.playerPinPointsRef)
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
                            statsList.clear()
                            statsList.add(formatted)
                            showGameSummaryActivity()
                        }
                    }
                }
            }})

        //add all the needed listener on the database.
        db.referenceGet("GameInstance" , "Game" + intent.getStringExtra("gid")!!).addOnSuccessListener { gi ->
            other = ((gi as DataSnapshot).value as MutableMap<String,Any>).filter { id -> id.key != "id:" + intent.getStringExtra("uid")!! }
            other.forEach { t, u ->
                val other = t.replace("id:","")
                db.addList("GameInstance/Game" + intent.getStringExtra("gid")!! + "/id:" + other,"finish",endListener)
                pinpointHandler.updateLocalPinpoints(other,markerOther.playerPinPointsRef)
                game.handleEvent(GameEvent.OnStart(goal,intent.getStringExtra("uid")!!, intent.getStringExtra("gid")!!, other))
             }
        }

        //initialise the on screen text
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

    //center the screen on the player position
    fun centerButton(view: View) {
        val gpsPos = gpsPositionManager.getPosition()
        if (gpsPos != null)
            mapViewManager.center(gpsPos)
    }

    //show the summary of the game by launching another activity.
    private fun showGameSummaryActivity() {
        val intent = Intent(this, GameSummaryActivity::class.java)
        extras.putStringArrayList(EXTRA_STATS, statsList)
        extras.putString(EXTRA_MODE, "Versus")
        intent.putExtras(extras)
        startActivity(intent)
    }

}