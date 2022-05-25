package com.github.displace.sdp2022

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.GoodDB
import com.github.displace.sdp2022.gameComponents.GameEvent
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.gameVersus.Chat
import com.github.displace.sdp2022.gameVersus.ClientServerLink
import com.github.displace.sdp2022.gameVersus.GameVersusViewModel
import com.github.displace.sdp2022.map.*
import com.github.displace.sdp2022.util.DateTimeUtil
import com.github.displace.sdp2022.util.PreferencesUtil
import com.github.displace.sdp2022.util.gps.GPSPositionManager
import com.github.displace.sdp2022.util.gps.GPSPositionUpdater
import com.github.displace.sdp2022.util.gps.GeoPointListener
import com.github.displace.sdp2022.util.gps.MockGPS
import com.github.displace.sdp2022.util.math.Constants
import com.github.displace.sdp2022.util.math.CoordinatesUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.io.Serializable
import kotlin.random.Random


const val EXTRA_STATS = "com.github.displace.sdp2022.GAMESTAT"
const val EXTRA_RESULT = "com.github.displace.sdp2022.GAMERESULT"
const val EXTRA_MODE = "com.github.displace.sdp2022.GAMEMODE"

class GameVersusViewActivity : AppCompatActivity() {

    val statsList: ArrayList<String> = arrayListOf()

    private lateinit var db: RealTimeDatabase
    private lateinit var dbGood: GoodDB

    private lateinit var game: GameVersusViewModel
    private val extras: Bundle = Bundle()

    private lateinit var mapView: MapView
    private lateinit var mapViewManager: MapViewManager
    private lateinit var gpsPositionUpdater: GPSPositionUpdater
    private lateinit var gpsPositionManager: GPSPositionManager
    private lateinit var other: Map<String, Any>
    private var others = listOf<List<String>>()
    private lateinit var pinpointsDBHandler: PinpointsDBHandler
    private lateinit var pinpointsManager: PinpointsManager
    private var otherPlayersPinpoints = listOf<PinpointsManager.PinpointsRef>()
    private lateinit var conditionalGoalPlacer: ConditionalGoalPlacer
    private lateinit var clientServerLink: ClientServerLink
    private var clickableArea = Constants.CLICKABLE_AREA_RADIUS

    private var nbPlayer = 1L
    private lateinit var gameMode: String
    private var order = 0.0

    private var gid = ""
    private var uid = ""

    private var oldPos = GeoPoint(0.0,0.0)
    private var totalDist = 0.0
    private var totalTime = 0

    //CHAT
    private lateinit var chat: Chat

    //MEDIA PLAYER
    private lateinit var musicPlayer: MediaPlayer
    private lateinit var endPlayer: MediaPlayer


    //listener to initialise the goal
    private val initGoalPlacer = object : GeoPointListener {
        override fun invoke(geoPoint: GeoPoint) {
            conditionalGoalPlacer = ConditionalGoalPlacer(mapView, game.getGameInstance(), geoPoint)
            gpsPositionManager.listenersManager.removeCall(this)
        }
    }

    //listener that verify if the player found or missed the other player.
    // 3 possibility : win => guess == position of the goal, continue => guess != position and lost => you missed the max number of time and lost
    private val guessListener = GeoPointListener { geoPoint ->
        run {
            if (CoordinatesUtil.distance(
                    game.getPos(),
                    CoordinatesUtil.coordinates(geoPoint)
                ) <= clickableArea
            ) {
                pinpointsManager.putMarker(geoPoint)
                val res = game.handleEvent(
                    GameEvent.OnPointSelected(
                        intent.getStringExtra("uid")!!,
                        Point(geoPoint.latitude, geoPoint.longitude)
                    )
                )
                if (res == GameVersusViewModel.WIN && nbPlayer == 2L) {
                    gpsPositionManager.listenersManager.clearAllCalls()
                    clientServerLink.listenerManager.clearAllCalls()
                    findViewById<TextView>(R.id.TryText).apply { text = "win" }
                    extras.putBoolean(EXTRA_RESULT, true)
                    statsList.clear()
                    statsList.add(DateTimeUtil.currentTime())
                    showGameSummaryActivity()
                } else if (res == GameVersusViewModel.WIN) {
                    findViewById<TextView>(R.id.TryText).apply {
                        text = "correct guess, remaining tries : ${4 - game.getNbEssai()}"
                    }
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
                    clientServerLink.listenerManager.clearAllCalls()
                    findViewById<TextView>(R.id.TryText).apply {
                        text =
                            "status : end of game"
                    }
                    extras.putBoolean(EXTRA_RESULT, false)
                    statsList.clear()
                    statsList.add(DateTimeUtil.currentTime())
                    showGameSummaryActivity()
                }
            }
        }
    }

    //verify if the other has already finish by : winnig, losing or leaving
    private val endListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val x = dataSnapshot.value

            if (x == GameVersusViewModel.LOSE) {
                nbPlayer -= 1
            }
            if ((x == GameVersusViewModel.LOSE && nbPlayer < 2) || x == order) {
                gpsPositionManager.listenersManager.clearAllCalls()
                clientServerLink.listenerManager.clearAllCalls()
                statsList.clear()
                statsList.add(DateTimeUtil.currentTime())
                if (x == order) {
                    db.update("GameInstance/Game$gid/id:$uid", "pos", listOf(0.0, 0.0, order))
                    db.update("GameInstance/Game$gid/id:$uid", "finish", GameVersusViewModel.LOSE)
                    findViewById<TextView>(R.id.TryText).apply {
                        text =
                            "status : end of game"
                    }
                    extras.putBoolean(EXTRA_RESULT, false)
                    showGameSummaryActivity()
                } else {
                    db.delete("GameInstance", "Game" + intent.getStringExtra("gid")!!)
                    findViewById<TextView>(R.id.TryText).apply { text = "win" }
                    extras.putBoolean(EXTRA_RESULT, true)
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

        uid = intent.getStringExtra("uid")!!
        gid = intent.getStringExtra("gid")!!
        gameMode = intent.getStringExtra("gameMode")!!
        clickableArea = intent.getIntExtra("dist", Constants.CLICKABLE_AREA_RADIUS)
        db = RealTimeDatabase().noCacheInstantiate(
            "https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",
            false
        ) as RealTimeDatabase
        dbGood = DatabaseFactory.getDB(intent)
        order = Random.nextDouble(0.0, 1000000000000000000000000.0)
        clientServerLink = ClientServerLink(db, order)
        game = GameVersusViewModel(clientServerLink)
        nbPlayer = intent.getLongExtra("nbPlayer", 1)

        //initialise all the viewer and manager.
        mapView = findViewById(R.id.map)
        mapViewManager = MapViewManager(mapView)
        pinpointsDBHandler = PinpointsDBHandler(db, "Game" + intent.getStringExtra("gid")!!, this)
        pinpointsDBHandler.initializePinpoints(intent.getStringExtra("uid")!!)
        gpsPositionManager = GPSPositionManager(this)
        gpsPositionManager.listenersManager.addCall(initGoalPlacer)
        gpsPositionUpdater = GPSPositionUpdater(this, gpsPositionManager)

        val clickSoundPlayer = if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(SFX_SETTINGS_SWITCH, true)
        )
            MediaPlayer.create(this, R.raw.zapsplat_sound_design_hit_punchy_bright_71725)
        else null

        pinpointsManager = PinpointsManager(mapView, clickSoundPlayer)
        for(i in nbPlayer downTo 0){

            otherPlayersPinpoints = otherPlayersPinpoints.plus(pinpointsManager.PinpointsRef())
        }
        MockGPS.mockIfNeeded(intent, gpsPositionManager)

        //update the actual position of the player on the database
        gpsPositionManager.listenersManager.addCall(GeoPointListener { geoPoint ->
            game.handleEvent(
                GameEvent.OnUpdate(
                    intent.getStringExtra("uid")!!,
                    CoordinatesUtil.coordinates(geoPoint)
                )
            )
        })

        gpsPositionManager.listenersManager.addCall(GeoPointListener { geoPoint ->
            addTotals(geoPoint)
        })

        gpsPositionManager.updateLocation()
        GPSLocationMarker(mapView, gpsPositionManager).add()

        //add the listener on the map and database
        mapViewManager.addCallOnLongClick(guessListener)
        db.referenceGet("GameInstance", "Game${intent.getStringExtra("gid")!!}")
            .addOnSuccessListener { gi -> initGame(gi) }

        findViewById<TextView>(R.id.TryText).apply {
            text =
                "remaining tries : ${4 - game.getNbEssai()}"
        }

        gpsPositionManager.listenersManager.addCall(GeoPointListener { gp ->
            if (this::conditionalGoalPlacer.isInitialized) {
                conditionalGoalPlacer.update(gp)
            }

        })

        clientServerLink.listenerManager.addCall { gameInstance ->
            if (this::conditionalGoalPlacer.isInitialized) {
                gpsPositionManager.updateLocation()
                conditionalGoalPlacer.update(gameInstance)
            }
        }

        //initialise the chat
        val chatPath = "/GameInstance/Game" + intent.getStringExtra("gid")!! + "/Chat"
        chat = Chat(
            chatPath,
            dbGood,
            findViewById<View?>(android.R.id.content).rootView,
            applicationContext
        )

        //music and sound effects
        musicPlayer = MediaPlayer.create(this, R.raw.music_zapsplat_electric_drum_and_bass)
        musicPlayer.setLooping(true)

        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(MUSIC_SETTINGS_SWITCH, true)
        ) {
            musicPlayer.start()
        }

        endPlayer = MediaPlayer.create(
            this,
            R.raw.zapsplat_sound_design_designed_metal_hit_ring_chime_80212
        )
        musicPlayer.isLooping = false
    }

    private fun addTotals(point : GeoPoint){
        if(point.latitude != 0.0 && point.longitude != 0.0 && oldPos.longitude == 0.0 && oldPos.latitude == 0.0){
            oldPos = point
        }
        totalDist += CoordinatesUtil.distance(oldPos,point)
        oldPos = point
        totalTime += 5
    }

    //close the screen
    @Suppress("UNUSED_PARAMETER")
    fun closeButton(view: View) {
        game.handleEvent(GameEvent.OnSurrend(intent.getStringExtra("uid")!!))
        gpsPositionManager.listenersManager.clearAllCalls()
        clientServerLink.listenerManager.clearAllCalls()
        val intent = Intent(this, GameListActivity::class.java)
        startActivity(intent)
    }

    //center the screen around the player position
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

    //show the game summary by launching a new activity
    private fun showGameSummaryActivity() {

        others.forEach { x ->
            db.removeList(
                "GameInstance/Game${intent.getStringExtra("gid")!!}/id:${x[1]}",
                "finish",
                endListener
            )
        }
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(SFX_SETTINGS_SWITCH, true)
        ) {
            endPlayer.start()
        }
        val intent = Intent(this, GameSummaryActivity::class.java)
        extras.putStringArrayList(EXTRA_STATS, statsList)
        extras.putString(EXTRA_MODE, gameMode)
        intent.putExtras(extras)

        intent.putExtra("others",others as Serializable)
        intent.putExtra("totalDist",totalDist)
        intent.putExtra("totalTime",totalTime)

        startActivity(intent)
    }

    //initialise the game
    private fun initGame(gi: DataSnapshot) {
        other = (gi.value as MutableMap<String, Any>).filter { id ->
            id.key != "id:${
                intent.getStringExtra("uid")!!
            }" && id.key != "Chat"
        }

        var i = 0
        other.toList().forEach { x ->
            val otherPlayerId = x.first.removePrefix("id:")
            db.addList(
                "GameInstance/Game${intent.getStringExtra("gid")!!}/id:${otherPlayerId}",
                "finish",
                endListener
            )

            if(!intent.getStringExtra("uid")!!.contains("guest")) {
                db.referenceGet(
                    "CompleteUsers/${otherPlayerId}/CompleteUser/partialUser",
                    "username"
                ).addOnSuccessListener { snapshot ->
                    try {
                        val name = snapshot.value as String
                        val list = listOf(listOf(name, otherPlayerId))
                        others = others.plus(list)
                    } catch (e: Exception) {
                    }
                }
            }

            try{
            pinpointsDBHandler.enableAutoupdateLocalPinpoints(
                otherPlayerId,
                otherPlayersPinpoints[i]
            )
            }catch(e: Exception){ throw error(otherPlayerId + " i = $i")}

            game.handleEvent(
                GameEvent.OnStart(
                    DEFAULT_GOAL,
                    uid,
                    gid,
                    otherPlayerId,
                    nbPlayer
                )
            )
            i += 1
        }
    }

    override fun onResume() {
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(MUSIC_SETTINGS_SWITCH, true)
        ) {
            musicPlayer.start()
        }
        super.onResume()
    }

    override fun onDestroy() {
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(MUSIC_SETTINGS_SWITCH, true)
        ) {
            musicPlayer.stop()
        }
        super.onDestroy()
    }


    companion object {
        private val DEFAULT_GOAL = CoordinatesUtil.coordinates(MapViewManager.DEFAULT_CENTER)
    }

    /**
     * CHAT SECTION OF THE ACTIVITY
     */

    fun addToChat(view: View) {
        chat.addToChat()
    }

    fun showChatButton(view: View) {
        chat.showChat()
    }

    fun closeChatButton(view: View) {
        chat.hideChat()
    }

    override fun onPause() {
        //CHAT
        chat.removeListener()

        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(MUSIC_SETTINGS_SWITCH, true)
        ) {
            musicPlayer.pause()
        }
        super.onPause()
    }


}