package com.github.displace.sdp2022

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.gameComponents.GameEvent
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.gameVersus.ClientServerLink
import com.github.displace.sdp2022.gameVersus.GameVersusViewModel
import com.github.displace.sdp2022.map.GPSLocationMarker
import com.github.displace.sdp2022.map.MapViewManager
import com.github.displace.sdp2022.map.PinpointsManager
import com.github.displace.sdp2022.map.PinpointsDBHandler
import com.github.displace.sdp2022.util.DateTimeUtil
import com.github.displace.sdp2022.util.PreferencesUtil
import com.github.displace.sdp2022.util.gps.GPSPositionManager
import com.github.displace.sdp2022.util.gps.GPSPositionUpdater
import com.github.displace.sdp2022.util.gps.GeoPointListener
import com.github.displace.sdp2022.util.math.CoordinatesUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import kotlin.collections.ArrayList


const val EXTRA_STATS = "com.github.displace.sdp2022.GAMESTAT"
const val EXTRA_RESULT = "com.github.displace.sdp2022.GAMERESULT"
const val EXTRA_MODE = "com.github.displace.sdp2022.GAMEMODE"
const val EXTRA_SCORE_P1 = "com.github.displace.sdp2022.SCOREP1"
const val EXTRA_SCORE_P2 = "com.github.displace.sdp2022.SCOREP2"

class GameVersusViewActivity : AppCompatActivity() {

    val statsList: ArrayList<String> = arrayListOf()

    private lateinit var db: RealTimeDatabase

    private lateinit var game: GameVersusViewModel
    private val extras: Bundle = Bundle()

    private lateinit var mapView: MapView
    private lateinit var mapViewManager: MapViewManager
    private lateinit var gpsPositionUpdater: GPSPositionUpdater
    private lateinit var gpsPositionManager: GPSPositionManager
    private lateinit var other: Map<String, Any>
    private lateinit var pinpointsDBHandler: PinpointsDBHandler
    private lateinit var pinpointsManager: PinpointsManager
    private lateinit var otherPlayerPinpoints: PinpointsManager.PinpointsRef

    private val guessListener = GeoPointListener { geoPoint ->
        run {
            pinpointsManager.putMarker(geoPoint)
            val res = game.handleEvent(
                GameEvent.OnPointSelected(
                    intent.getStringExtra("uid")!!,
                    Point(geoPoint.latitude, geoPoint.longitude)
                )
            )
            if (res == GameVersusViewModel.WIN) {
                gpsPositionManager.listenersManager.clearAllCalls()
                findViewById<TextView>(R.id.TryText).apply { text = "win" }
                extras.putBoolean(EXTRA_RESULT, true)
                extras.putInt(EXTRA_SCORE_P1, 1)
                extras.putInt(EXTRA_SCORE_P2, 0)
                statsList.clear()
                statsList.add(DateTimeUtil.currentTime())
                showGameSummaryActivity()
            } else if (res == GameVersusViewModel.CONTINUE) {
                findViewById<TextView>(R.id.TryText).apply {
                    text = "wrong guess, remaining tries : ${4 - game.getNbEssai()}"
                }
                pinpointsDBHandler.updateDBPinpoints(
                    intent.getStringExtra("uid")!!,
                    pinpointsManager.playerPinPointsRef
                )
            } else if (res == GameVersusViewModel.LOSE) {
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

    private val endListener = object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val x = dataSnapshot.value
            if (x == GameVersusViewModel.LOSE || x == GameVersusViewModel.WIN) {
                db.delete("GameInstance", "Game" + intent.getStringExtra("gid")!!)
                gpsPositionManager.listenersManager.clearAllCalls()
                statsList.clear()
                statsList.add(DateTimeUtil.currentTime())
                if (x == GameVersusViewModel.WIN) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferencesUtil.initOsmdroidPref(this)
        setContentView(R.layout.activity_game_versus)

        db = RealTimeDatabase().noCacheInstantiate(
            "https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",
            false
        ) as RealTimeDatabase
        game = GameVersusViewModel(ClientServerLink(db))

        mapView = findViewById(R.id.map)
        mapViewManager = MapViewManager(mapView)
        pinpointsDBHandler = PinpointsDBHandler(db, "Game" + intent.getStringExtra("gid")!!, this)
        pinpointsDBHandler.initializePinpoints(intent.getStringExtra("uid")!!)
        gpsPositionManager = GPSPositionManager(this)
        gpsPositionUpdater = GPSPositionUpdater(this, gpsPositionManager)
        pinpointsManager = PinpointsManager(mapView)
        otherPlayerPinpoints = pinpointsManager.PinpointsRef()
        gpsPositionManager.listenersManager.addCall(GeoPointListener { geoPoint ->
            game.handleEvent(
                GameEvent.OnUpdate(
                    intent.getStringExtra("uid")!!,
                    CoordinatesUtil.coordinates(geoPoint)
                )
            )
        })
        GPSLocationMarker(mapView, gpsPositionManager).add()

        mapViewManager.addCallOnLongClick(guessListener)

        db.referenceGet("GameInstance", "Game${intent.getStringExtra("gid")!!}")
            .addOnSuccessListener { gi -> initGame(gi) }

        findViewById<TextView>(R.id.TryText).apply {
            text =
                "remaining tries : ${4 - game.getNbEssai()}"
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

    private fun initGame(gi: DataSnapshot) {
        other = (gi.value as MutableMap<String, Any>).filter { id ->
            id.key != "id:${
                intent.getStringExtra("uid")!!
            }"
        }

        val otherPlayerId =
            other.toList()[0].first.removePrefix("id:") // currently supporting only 2 players
        db.addList(
            "GameInstance/Game${intent.getStringExtra("gid")!!}/id:${otherPlayerId}",
            "finish",
            endListener
        )
        pinpointsDBHandler.enableAutoupdateLocalPinpoints(
            otherPlayerId,
            otherPlayerPinpoints
        )
        game.handleEvent(
            GameEvent.OnStart(
                DEFAULT_GOAL,
                intent.getStringExtra("uid")!!,
                intent.getStringExtra("gid")!!,
                otherPlayerId
            )
        )
    }

    companion object {
        private val DEFAULT_GOAL = CoordinatesUtil.coordinates(MapViewManager.DEFAULT_CENTER)
    }

}