package com.github.displace.sdp2022

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.gameComponents.GameEvent
import com.github.displace.sdp2022.gameComponents.Player
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.gameVersus.GameVersusViewModel
import com.github.displace.sdp2022.map.MapViewManager
import com.github.displace.sdp2022.map.MarkerManager
import com.github.displace.sdp2022.map.PinpointsDBCommunicationHandler
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.profile.messages.MsgViewAdapter
import com.github.displace.sdp2022.users.PartialUser
import com.github.displace.sdp2022.util.PreferencesUtil
import com.github.displace.sdp2022.util.gps.GPSPositionManager
import com.github.displace.sdp2022.util.gps.GPSPositionUpdater
import com.github.displace.sdp2022.util.gps.GeoPointListener
import com.google.firebase.database.*
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

        mapView = findViewById<MapView>(R.id.map)
        mapViewManager = MapViewManager(mapView)
        markerListener = GeoPointListener.markerPlacer(mapView)
        gpsPositionManager = GPSPositionManager(this)
        gpsPositionUpdater = GPSPositionUpdater(this,gpsPositionManager)
        marker = MarkerManager(mapView)
        markerOther = MarkerManager(mapView)
        gpsPositionUpdater.listenersManager.addCall(GeoPointListener { geoPoint ->
            game.handleEvent(GameEvent.OnUpdate(intent.getStringExtra("uid")!!,
                Point(geoPoint.latitude,geoPoint.longitude)))})

        centerButton(mapView) // to initialise the gps position
        //centerButton(mapView) // to set the center of the screen

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

        val endListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val x = dataSnapshot.getValue()
                if (x == 1L || x == -1L) {
                    db.delete("GameInstance", "Game" + intent.getStringExtra("gid")!!)
                    gpsPositionUpdater.listenersManager.clearAllCalls()
                    statsList.clear()
                    val current = LocalDateTime.now()
                    val formatted = current.format(formatter)
                    statsList.add(formatted)
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
        
        pinpointHandler = PinpointsDBCommunicationHandler(db,"Game" + intent.getStringExtra("gid")!!)
        pinpointHandler.start(intent.getStringExtra("uid")!!)

        mapViewManager.addCallOnLongClick(GeoPointListener { geoPoint -> run {
                marker.putMarker(geoPoint)
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

        db.referenceGet("GameInstance" , "Game" + intent.getStringExtra("gid")!!).addOnSuccessListener { gi ->
            other = ((gi as DataSnapshot).value as MutableMap<String,Any>).filter { id -> id.key != "id:" + intent.getStringExtra("uid")!! }
            other.forEach { t, u ->
                val other = t.replace("id:","")
                db.addList("GameInstance/Game" + intent.getStringExtra("gid")!! + "/id:" + other,"finish",endListener)
                pinpointHandler.updateLocalPinpoints(other,markerOther.playerPinPointsRef)
                game.handleEvent(GameEvent.OnStart(goal,intent.getStringExtra("uid")!!, intent.getStringExtra("gid")!!, other))
             }
        }

        findViewById<TextView>(R.id.TryText).apply { text =
            "status : neutral, nombre d'essais restant : " + (4 - game.getNbEssai())
        }

        db.getDbReference("GameInstance/Game/"+ intent.getStringExtra("gid")!! + "/Chat").addValueEventListener(chatListener())
        closeChatButton( findViewById<Group>(R.id.ChatActiveGroup) )

    }


    /**
     * GAME VERSUS BASIC USES
     */

    //close the screen
    fun closeButton(view: View) {
        game.handleEvent(GameEvent.OnSurrend(intent.getStringExtra("uid")!!))
        gpsPositionUpdater.listenersManager.clearAllCalls()

        //CHAT
        db.getDbReference("GameInstance/Game/"+ intent.getStringExtra("gid")!! + "/Chat").removeEventListener(chatListener())

        val intent = Intent(this, GameListActivity::class.java)
        startActivity(intent)
    }

    fun centerButton(view: View) {
        val gpsPos = gpsPositionManager.getPosition()
        if (gpsPos != null)
            mapViewManager.center(gpsPos)
    }


    /**
     * CHAT SECTION OF THE ACTIVITY
     */

    private fun chatListener() = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            val messageRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
            val list = mutableListOf<Message>()

            val ls = snapshot.value as ArrayList<HashMap<String,Any>>?
            if(ls != null){
                for( map in ls ){
                    val sender = map["sender"] as HashMap<String,Any>
                    val m = Message(map["message"] as String,map["date"] as String, PartialUser(sender["username"] as String,sender["uid"] as String) )
                    list.add(m)
                }
            }

            val messageAdapter = MsgViewAdapter(
                applicationContext,
                list,
                1
            )
            messageRecyclerView.adapter = messageAdapter
            messageRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    }


    fun addToChat(view : View){
        val msg : String = findViewById<EditText>(R.id.chatEditText).text.toString()
        val partialUser : PartialUser = (applicationContext as MyApplication).getActiveUser()?.getPartialUser()!!
        val date : String = (applicationContext as MyApplication).getCurrentTime()
        if(msg.isEmpty()){
            return
        }
        db.getDbReference("GameInstance/Game/"+ intent.getStringExtra("gid")!! + "/Chat")
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    var ls = currentData.value as ArrayList<HashMap<String,Any>>?
                    val map = HashMap<String,Any>()
                    map["message"] = msg
                    map["date"] = date
                    map["sender"] = partialUser
                    val msgLs = arrayListOf(map)
                    if(ls != null) {
                        ls.addAll(msgLs)
                        if(ls.size >= 6){
                            ls = ls.takeLast(5) as ArrayList<HashMap<String, Any>>
                        }
                        currentData.value = ls
                    }else {
                        currentData.value = msgLs
                    }
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    //  TODO("Not yet implemented")
                }

            })
    }

    fun showChatButton(view : View){
        val chatGroup = findViewById<ConstraintLayout>(R.id.chatLayout)
        chatGroup.visibility = View.VISIBLE
        db.getDbReference("GameInstance/Game/"+ intent.getStringExtra("gid")!! + "/Chat").addListenerForSingleValueEvent(chatListener())
    }

    fun closeChatButton(view : View){
        val chatGroup = findViewById<ConstraintLayout>(R.id.chatLayout)
        chatGroup.visibility = View.GONE
    }


    /**
     * TRANSITION TO GAME SUMMARY
     */
    private fun showGameSummaryActivity() {

        //CHAT
        db.getDbReference("GameInstance/Game/"+ intent.getStringExtra("gid")!! + "/Chat").removeEventListener(chatListener())

        val intent = Intent(this, GameSummaryActivity::class.java)
        extras.putStringArrayList(EXTRA_STATS, statsList)
        extras.putString(EXTRA_MODE, "Versus")
        intent.putExtras(extras)
        startActivity(intent)
    }

}