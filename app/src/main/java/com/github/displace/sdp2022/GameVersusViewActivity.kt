package com.github.displace.sdp2022

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.EditText
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
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.users.PartialUser
import com.github.displace.sdp2022.util.DateTimeUtil
import com.github.displace.sdp2022.util.PreferencesUtil
import com.github.displace.sdp2022.util.ThemeManager
import com.github.displace.sdp2022.util.gps.GPSPositionManager
import com.github.displace.sdp2022.util.gps.GPSPositionUpdater
import com.github.displace.sdp2022.util.gps.MockGPS
import com.github.displace.sdp2022.util.listeners.Listener
import com.github.displace.sdp2022.util.math.Constants
import com.github.displace.sdp2022.util.math.CoordinatesUtil
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.io.Serializable
import kotlin.random.Random


const val EXTRA_STATS = "com.github.displace.sdp2022.GAMESTAT"
const val EXTRA_RESULT = "com.github.displace.sdp2022.GAMERESULT"
const val EXTRA_MODE = "com.github.displace.sdp2022.GAMEMODE"

class GameVersusViewActivity : AppCompatActivity() {

    val statsList: ArrayList<String> = arrayListOf()


    private lateinit var db: GoodDB


    private lateinit var game: GameVersusViewModel
    private val extras: Bundle = Bundle()

    private lateinit var mapView: MapView
    private lateinit var mapViewManager: MapViewManager
    private lateinit var gpsPositionUpdater: GPSPositionUpdater
    private lateinit var gpsPositionManager: GPSPositionManager
    private lateinit var other: Map<String, Any>
    private var others = listOf<List<String>>()
    private lateinit var pinpointsDBHandler: GoodPinpointsDBHandler
    private lateinit var pinpointsManager: PinpointsManager
    private var otherPlayersPinpoints = listOf<PinpointsManager.PinpointsRef>()
    private lateinit var conditionalGoalPlacer: ConditionalGoalPlacer
    private lateinit var clientServerLink: ClientServerLink
    private var clickableArea = Constants.CLICKABLE_AREA_RADIUS

    private var nbPlayer = 1L
    private lateinit var gameMode: String
    private var order = 0L

    private var gid = ""
    private var uid = ""

    private var oldPos = GeoPoint(0.0, 0.0)
    private var totalDist = 0.0

    //CHAT
    private lateinit var chat: Chat

    //MEDIA PLAYER
    private lateinit var musicPlayer: MediaPlayer
    private lateinit var endPlayer: MediaPlayer


    //listener to initialise the goal
    private val initGoalPlacer =
        Listener<GeoPoint> { geoPoint -> conditionalGoalPlacer = ConditionalGoalPlacer(mapView, game.getGameInstance(), geoPoint) }

    //listener that verify if the guess found or missed the other player.
    // 3 possibility : win => guess == position of the goal, continue => guess != position and lost => you missed the max number of time and lost
    private val guessListener = Listener<GeoPoint> { geoPoint ->
        run {
            if (CoordinatesUtil.distance(
                    game.getPos(),
                    CoordinatesUtil.coordinates(geoPoint)
                ) <= clickableArea // the maximum range in which you can click
            ) {
                pinpointsManager.putMarker(geoPoint)
                val res = game.handleEvent( // send a message to gameVersus with the localisation of your guess
                    GameEvent.OnPointSelected(
                        intent.getStringExtra("uid")!!,
                        Point(geoPoint.latitude, geoPoint.longitude)
                    )
                )
                if (res == GameVersusViewModel.WIN && nbPlayer == 2L) { // if you found the other player and you are the only 2 left, you win.
                    gpsPositionManager.listenersManager.clearAllCalls()
                    clientServerLink.listenerManager.clearAllCalls()
                    findViewById<TextView>(R.id.TryText).apply { text = "win" }
                    extras.putBoolean(EXTRA_RESULT, true)
                    statsList.clear()
                    statsList.add(DateTimeUtil.currentTime())
                    showGameSummaryActivity()
                } else if (res == GameVersusViewModel.WIN) { //if you found the other player but you aren't the only 2 left, you won't lose a try but the game go on.
                    findViewById<TextView>(R.id.TryText).apply {
                        text = "correct guess, remaining tries : ${4 - game.getNbEssai()}"
                    }
                } else if (res == GameVersusViewModel.CONTINUE) { // you missed the other but you still have some try left
                    findViewById<TextView>(R.id.TryText).apply {
                        text = "wrong guess, remaining tries : ${4 - game.getNbEssai()}"
                    }
                    pinpointsDBHandler.updateDBPinpoints(
                        intent.getStringExtra("uid")!!,
                        pinpointsManager.playerPinPointsRef
                    )
                } else if (res == GameVersusViewModel.LOSE) { // you missed more than the max number of try
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
    private val endListener = Listener<Long?> { dataSnapshot ->
            val x = dataSnapshot

            if (x == GameVersusViewModel.LOSE) { // if someone lose, the total number of player goes down by one.
                nbPlayer -= 1
            }
            if ((x == GameVersusViewModel.LOSE && nbPlayer < 2) || x == order) { // if you win and you are one of the 2 remaining player or if you are found (x == your order), the game end for you.
                gpsPositionManager.listenersManager.clearAllCalls()
                clientServerLink.listenerManager.clearAllCalls()
                statsList.clear()
                statsList.add(DateTimeUtil.currentTime())
                if (x == order) { // in case your found you just update you value to make clear to other that you were found
                    db.update("GameInstance/Game$gid/id:$uid/pos", listOf(0.0, 0.0, order))
                    db.update("GameInstance/Game$gid/id:$uid/finish", GameVersusViewModel.LOSE)
                    findViewById<TextView>(R.id.TryText).apply {
                        text =
                            "status : end of game"
                    }
                    extras.putBoolean(EXTRA_RESULT, false)
                    showGameSummaryActivity()
                } else { // if you win, then as you are the last player in the game, you need to destroy it before leaving.
                    db.delete("GameInstance/Game" + intent.getStringExtra("gid")!!)
                    findViewById<TextView>(R.id.TryText).apply { text = "win" }
                    extras.putBoolean(EXTRA_RESULT, true)
                    showGameSummaryActivity()
                }
            }
        }

    private fun initStart(){
        uid = intent.getStringExtra("uid")!!
        gid = intent.getStringExtra("gid")!!
        gameMode = intent.getStringExtra("gameMode")!!
        clickableArea = intent.getIntExtra("dist", Constants.CLICKABLE_AREA_RADIUS)

        db = DatabaseFactory.getDB(intent)
        order = Random.nextLong(-10000000000L, 10000000000L) // find a unique id for you and for this instance of the game

        clientServerLink = ClientServerLink(db, order)
        game = GameVersusViewModel(clientServerLink)
        nbPlayer = intent.getLongExtra("nbPlayer", 1)
    }

    private fun initMap(){
        mapView = findViewById(R.id.map)
        mapViewManager = MapViewManager(mapView)
        pinpointsDBHandler = GoodPinpointsDBHandler(db, "Game" + intent.getStringExtra("gid")!!, this)
        pinpointsDBHandler.initializePinpoints(intent.getStringExtra("uid")!!)
        gpsPositionManager = GPSPositionManager(this)
        gpsPositionManager.listenersManager.addCallOnce(initGoalPlacer)
        gpsPositionUpdater = GPSPositionUpdater(this, gpsPositionManager)
    }

    private fun listenerOnMap(){
        // add sound
        val clickSoundPlayer = if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.sfx), true)
        )
            MediaPlayer.create(this, R.raw.zapsplat_sound_design_hit_punchy_bright_71725)
        else null
        pinpointsManager = PinpointsManager(mapView, clickSoundPlayer)

        // add pinpoint manager for each other player. Make each player have a different color
        for(i in nbPlayer downTo 0){

            otherPlayersPinpoints = otherPlayersPinpoints.plus(pinpointsManager.PinpointsRef())
        }

        // add mocking
        MockGPS.mockIfNeeded(intent, gpsPositionManager)

        //update the actual position of the player on the database
        gpsPositionManager.listenersManager.addCall { geoPoint ->
            game.handleEvent(
                GameEvent.OnUpdate(
                    intent.getStringExtra("uid")!!,
                    CoordinatesUtil.coordinates(geoPoint)
                )
            )
        }

        gpsPositionManager.listenersManager.addCall { geoPoint ->
            addTotals(geoPoint) // update the distance you moved this game
        }

        gpsPositionManager.updateLocation() // update your position
        GPSLocationMarker(mapView, gpsPositionManager).add() // and add a maker that show were you are

        //add the listener on the map and database
        mapViewManager.addCallOnLongClick(guessListener) // add a listener on the guess to pinpoint your try
    }

    private fun initOnlineVal(){
        gpsPositionManager.listenersManager.addCall { gp ->
            if (this::conditionalGoalPlacer.isInitialized) {
                conditionalGoalPlacer.update(gp)
            }

        }

        clientServerLink.listenerManager.addCall { gameInstance ->
            if (this::conditionalGoalPlacer.isInitialized) {
                gpsPositionManager.updateLocation()
                conditionalGoalPlacer.update(gameInstance)
            }
        }
    }

    private fun initMusic(){
        //music and sound effects
        musicPlayer = MediaPlayer.create(this, R.raw.music_zapsplat_electric_drum_and_bass)
        musicPlayer.setLooping(true)

        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.music), true)
        ) {
            musicPlayer.start()
        }

        endPlayer = MediaPlayer.create(
            this,
            R.raw.zapsplat_sound_design_designed_metal_hit_ring_chime_80212
        )
        musicPlayer.isLooping = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyChosenTheme(this)
        super.onCreate(savedInstanceState)
        PreferencesUtil.initOsmdroidPref(this)
        setContentView(R.layout.activity_game_versus)

        //initialise the stating value
        initStart()

        //initialise all the viewer and manager.
        initMap()

        listenerOnMap()

        db.getThenCall<HashMap<String,Any>>("GameInstance/Game${intent.getStringExtra("gid")!!}"){  gi -> initGame(gi!!) } // initialise the game

        findViewById<TextView>(R.id.TryText).apply {
            text =
                "remaining tries : ${4 - game.getNbEssai()}"
        }

        initOnlineVal()

        //initialise the chat
        val chatPath = "/GameInstance/Game" + intent.getStringExtra("gid")!! + "/Chat"
        chat = Chat(
            chatPath,
            db,
            findViewById<View?>(android.R.id.content).rootView,
            applicationContext
        )

        initMusic()
    }

    // keep tract of the distance you moved this game
    private fun addTotals(point : GeoPoint){
        if(point.latitude != 0.0 && point.longitude != 0.0 && oldPos.longitude == 0.0 && oldPos.latitude == 0.0){
            oldPos = point
        }
        totalDist += CoordinatesUtil.distance(oldPos, point)
        oldPos = point
    }

    //close the screen
    @Suppress("UNUSED_PARAMETER")
    fun closeButton(view: View) {
        game.handleEvent(GameEvent.OnSurrend(intent.getStringExtra("uid")!!))
        gpsPositionManager.listenersManager.clearAllCalls()
        clientServerLink.listenerManager.clearAllCalls()
        val intent = Intent(this, GameListActivity::class.java)
        startActivity(intent)
        finish()
    }

    //center the screen around the player position
    @Suppress("UNUSED_PARAMETER")
    fun centerButton(view: View) {
        gpsPositionManager.listenersManager.addCallOnce{ geoPoint ->
            mapViewManager.center(geoPoint)
        }
        gpsPositionManager.updateLocation()
    }

    //show the game summary by launching a new activity
    private fun showGameSummaryActivity() {

        // clean all the listener and then launch the new activity
        others.forEach { x ->
            db.removeListener(
                "GameInstance/Game${intent.getStringExtra("gid")!!}/id:${x[1]}/finish",
                endListener
            )
        }
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.sfx), true)
        ) {
            endPlayer.start()
        }
        val intent = Intent(this, GameSummaryActivity::class.java)
        extras.putStringArrayList(EXTRA_STATS, statsList)
        extras.putString(EXTRA_MODE, gameMode)
        intent.putExtras(extras)

        intent.putExtra("others",others as Serializable)
        intent.putExtra("totalDist",totalDist)

        startActivity(intent)
        finish()
    }

    //initialise the game
    private fun initGame(gi: HashMap<String,Any>): Unit {
        other = (gi as MutableMap<String, Any>).filter { id -> // get the other player of the game (not you nor the chat).
            id.key != "id:${
                intent.getStringExtra("uid")!!
            }" && id.key != "Chat"
        }

        var i = 0
        other.toList().forEach { x -> // add all the listener for each of them
            val otherPlayerId = x.first.removePrefix("id:")
            db.addListener(
                "GameInstance/Game${intent.getStringExtra("gid")!!}/id:${otherPlayerId}/finish",
                endListener
            )

            if(!intent.getStringExtra("uid")!!.contains("guest")) {
                db.getThenCall<String?>(
                    "CompleteUsers/${otherPlayerId}/CompleteUser/partialUser/username"
                ){ snapshot ->
                    try {
                        val name = snapshot!!
                        val list = listOf(listOf(name, otherPlayerId))
                        others = others.plus(list)
                    } catch (e: Exception) {
                    }
                }
            }

            try{
            pinpointsDBHandler.enableAutoupdateLocalPinpoints( // auto update the map when someone try to pinpoint someone else
                otherPlayerId,
                otherPlayersPinpoints[i]
            )
            }catch(e: Exception){ throw error(otherPlayerId + " i = $i" + other.toList())}

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
                .getBoolean(getString(R.string.music), true)
        ) {
            musicPlayer.start()
        }
        super.onResume()
    }

    override fun onDestroy() {
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.music), true)
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
        val msg: String = findViewById<EditText>(R.id.chatEditText).text.toString()
        if (msg.isEmpty()) { //do not send an empty message
            return
        }
        val partialUser: PartialUser =
            (applicationContext as MyApplication).getActiveUser()?.getPartialUser()!!

        val date: String = DateTimeUtil.currentDate()

        chat.addToChat(Message(msg,date,partialUser))
    }

    fun showChatButton(view: View) {
       chat.showChat()
    }

    fun closeChatButton(view: View) {
       chat.hideChat()
    }

    override fun onBackPressed() {
    }

    override fun onPause() {
       //CHAT
       chat.removeListener()

       if (PreferenceManager.getDefaultSharedPreferences(this)
               .getBoolean(getString(R.string.music), true)
       ) {
           musicPlayer.pause()
       }
       super.onPause()
    }


}
