@file:Suppress("UNCHECKED_CAST")

package com.github.displace.sdp2022.matchMaking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.GameVersusViewActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.profile.friends.Friend
import com.github.displace.sdp2022.profile.friends.FriendViewAdapter
import com.github.displace.sdp2022.users.PartialUser
import com.github.displace.sdp2022.util.gps.GPSPositionManager
import com.github.displace.sdp2022.util.gps.GPSPositionUpdater
import com.github.displace.sdp2022.util.gps.GeoPointListener
import com.github.displace.sdp2022.util.math.Constants
import com.github.displace.sdp2022.util.math.CoordinatesUtil
import com.google.firebase.database.*
import org.osmdroid.util.GeoPoint
import kotlin.random.Random

/**
 * make matches with other players using the Database (DB)
 * DB structure :
 * -> MM
 *      -> gamemode
 *          -> map (will be removed afterwards)
 *              -> type (private or public)
 *                   -> freeList : list of lobby IDs, only those that should be found
 *                   -> freeLobbies : set of lobbies, only those that should be found
 *                      -> lobbyID
 *                          -> lobby data
 *                   -> launchLobbies   : set of lobbies, those who should not be found anymore and are launching into the game
 *                      -> lobbyID
 *                          -> lobby data
 */
@Suppress("UNUSED_PARAMETER")
class MatchMakingActivity : AppCompatActivity() {

    //Database
    private lateinit var db: RealTimeDatabase
    private var currentLobbyId = ""

    private var gamemode = "Versus"
    private val map = "Map2"

    //This has to be chaged to the real active user
    private var activeUser = PartialUser("active","0")
    //indicates if the lobby is public or private
    private var lobbyType: String = "private"
    //keeps a map of the lobby : just how the DB stores it
    private var lobbyMap : MutableMap<String,Any> = HashMap<String,Any>()
    private lateinit var app : MyApplication
    private var nbPlayer = 2L

    private lateinit var gpsPositionManager : GPSPositionManager
    private lateinit var gpsPositionUpdater : GPSPositionUpdater

    private var debug: Boolean = false

    /**
     * Listener for the lobby while it is still setting up : players can find it and join it
     * It takes care of :
     * - updating the UI : list of players
     * - mantaining the lobbyMap variable
     * - check if the lobby is ready to be launched (only by the leader)
     */
    private val lobbyListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (isGroupVisible(R.id.setupGroup)) {    //nothing should be done if the player is not in a lobby
                return
            }
            val lobby = snapshot.value as MutableMap<String, Any>? ?: return
            lobbyMap = lobby
            updateUI()
            if(lobbyMap["lobbyCount"] as Long == lobbyMap["lobbyMax"] as Long  ){
                lobbyMap["lobbyLaunch"] = true
                setupLaunchListener()
                findViewById<Button>(R.id.MMCancelButton).visibility = View.INVISIBLE
                if (lobbyMap["lobbyLeader"] as String == activeUser.uid) {    //This client is the leader : perform the checks and launch if needed
                    db.referenceGet("MM/$gamemode/$map/$lobbyType", "freeList")
                        .addOnSuccessListener { free ->
                            val ls = free.value as ArrayList<String>?
                            if (ls != null) {
                                ls.remove(currentLobbyId) //remove this lobby from the free list
                                db.update("MM/$gamemode/$map/$lobbyType", "freeList", ls)
                            }
                            db.delete("MM/$gamemode/$map/$lobbyType/freeLobbies", currentLobbyId)
                            db.update(
                                "MM/$gamemode/$map/$lobbyType/launchLobbies",
                                currentLobbyId,
                                lobbyMap
                            )    //add this lobby to the launching lobbies
                        }
                }
            }
        }


        override fun onCancelled(error: DatabaseError) {
        }
    }


    /**
     * Listener for the launching lobby : this lobby is leaking players into the game mode
     * It takes care of :
     * - Mantaining the lobbyMap variable
     * - Making the transition to the game screen
     */
    private val launchLobbyListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val lobby = snapshot.value as MutableMap<String, Any>? ?: return
            lobbyMap = lobby
            if(lobbyMap["lobbyLeader"] as String == activeUser.uid ){
                leaveMM(true)
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }

    /**
     * On creation of the activity we must :
     * - setup the friend list for a potential private lobby : the 1 indicates that there are no message buttons
     * - show the right group of UI elements : the ones for setup
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_making)


        gpsPositionManager = GPSPositionManager(this)
        gpsPositionUpdater = GPSPositionUpdater(this,gpsPositionManager)
        gpsPositionUpdater.stopUpdates()


        nbPlayer = intent.getLongExtra("nbPlayer",2L)
        try {
            gamemode = intent.getStringExtra("gameMode")!!
        }catch (e: Exception){
            gamemode = "Versus"
        }

        debug = intent.getBooleanExtra("DEBUG", false)
        db = RealTimeDatabase().noCacheInstantiate(
            "https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",
            debug
        ) as RealTimeDatabase
        if (debug) {
            gpsPositionManager.mockProvider(GeoPoint(0.00001,0.00001))

            db.insert("MM/$gamemode/$map/private", "freeList", listOf("head"))
            db.insert(
                "MM/$gamemode/$map/private/freeLobbies",
                "freeHead",
                Lobby("freeHead", 0, PartialUser("FREE", "FREE") , GeoPoint(0.0,0.0))
            )
            db.insert(
                "MM/$gamemode/$map/private/launchLobbies",
                "launchHead",
                Lobby("launchHead", 0, PartialUser("FREE", "FREE"),GeoPoint(0.0,0.0))
            )
            db.insert("MM/$gamemode/$map/public", "freeList", listOf("head"))
            db.insert(
                "MM/$gamemode/$map/public/freeLobbies",
                "freeHead",
                Lobby("freeHead", 0, PartialUser("FREE", "FREE"),GeoPoint(0.0,0.0))
            )
            db.insert(
                "MM/$gamemode/$map/public/launchLobbies",
                "launchHead",
                Lobby("launchHead", 0, PartialUser("FREE", "FREE"),GeoPoint(0.0,0.0))
            )
        }
        uiToSetup()

        app = applicationContext as MyApplication
        val activeUser = app.getActiveUser()
        if (activeUser != null) {
            this.activeUser = activeUser.getPartialUser()
        }
        /* Friends in private lobby */
        val friendRecyclerView = findViewById<RecyclerView>(R.id.friendsMMRecycler)
        val friends = activeUser?.getFriendsList() ?: mutableListOf<PartialUser>()

        val friendAdapter = FriendViewAdapter(
            applicationContext,
            friends,
            1
        )
        friendRecyclerView.adapter = friendAdapter
        friendRecyclerView.layoutManager = LinearLayoutManager(applicationContext)




    }

    /**
     * Search a public lobby
     * Always search the first lobby of the list, so that they fill in an orderly manner    TODO : will have to add the filter by position
     * If no public lobbies are available, create one and add it to the list
     *
     * This is done in a Transaction such as if another player searches for a lobby, creation is separated
     */
    private fun publicSearch( lastId : String = "head" ) {
        var toCreateId = ""
        var toSearchId = ""
        db.getDbReference("MM/$gamemode/$map/$lobbyType/freeList")
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    toCreateId = ""
                    toSearchId = ""
                    val ls = currentData.value as ArrayList<String>? ?: return Transaction.success(currentData)

                    val idx : Int = ls.indexOf(lastId)+1
                    if(idx == 0){
                        return Transaction.abort()
                    }
                    if (idx == ls.size) {
                        //only the head exists : add a new ID to the list and create a new lobby with that ID
                        //we reached the end of the list, create a new lobby
                        val nextId = if (!debug) {
                            Random.nextLong().toString()
                        } else {
                            "test"
                        }
                        ls.add(nextId)

                        toCreateId = nextId
                        currentData.value = ls
                    } else {
                        //more than only the head exists : check the first one out
                        toSearchId = ls[idx]
                    }
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    if (committed) {
                        if (toCreateId != "") {   //we need to create a lobby
                            createPublicLobby(toCreateId)
                        } else {  //we need to check out a lobby
                            checkOutLobby(toSearchId,false)
                        }
                    } else {
                        publicSearch()
                    }
                }

            })


    }

    /**
     * Check out the lobby with the specific ID
     *
     * Add yourself to the list of players of the lobby and increase the count, it also sets up the listeners
     * It is done in a transaction such as if another player tries to join the same lobby only one of you is successful
     */
    private fun checkOutLobby(toSearchId: String , private : Boolean) {

        val checkOutWithPos = object : GeoPointListener{
            override fun invoke(geoPoint: GeoPoint) {
                gpsPositionManager.listenersManager.removeCall(this)


                db.getDbReference("MM/$gamemode/$map/$lobbyType/freeLobbies/$toSearchId")
                    .runTransaction(object : Transaction.Handler {
                        override fun doTransaction(currentData: MutableData): Transaction.Result {
                            val lobby =
                                currentData.value as MutableMap<String, Any>? ?: return Transaction.success(currentData)
                            val positionMap = lobby["lobbyPosition"] as HashMap<String,Any>

                            if (lobby["lobbyCount"] as Long == lobby["lobbyMax"] as Long || lobby["lobbyLaunch"] as Boolean ||
                                CoordinatesUtil.distance(geoPoint, GeoPoint((positionMap["latitude"] as Double)/*.toDouble() */ , (positionMap["longitude"] as Double)/*.toDouble()*/ ) ) > Constants.GAME_AREA_RADIUS ) {   //the lobby is not available : will be deleted from the list soon
                                return Transaction.abort()
                            }
                            lobby["lobbyCount"] = lobby["lobbyCount"] as Long + 1
                            val ls = lobby["lobbyPlayers"] as ArrayList<MutableMap<String, Any>>
                            val userMap =
                                HashMap<String, Any>() //elements have to be added as maps into the DB
                            userMap["username"] = activeUser.username
                            userMap["uid"] = activeUser.uid
                            ls.add(userMap)
                            lobby["lobbyPlayers"] = ls
                            currentData.value = lobby
                            return Transaction.success(currentData)
                        }

                        override fun onComplete(
                            error: DatabaseError?,
                            committed: Boolean,
                            currentData: DataSnapshot?
                        ) {
                            if (committed) {  //setups the listeners and makes the UI transition
                                if(private){
                                    app.setLobbyID(toSearchId)
                                }
                                currentLobbyId = toSearchId
                                setupLobbyListener()
                                uiToSearch()

                                gpsPositionUpdater.initTimer()
                                gpsPositionManager.listenersManager.addCall( { gp ->
                                    db.referenceGet("MM/$gamemode/$map/$lobbyType/freeLobbies",toSearchId ).addOnSuccessListener { snapshot ->
                                        val lobby = snapshot.value as HashMap<String,Any>? ?: return@addOnSuccessListener
                                        val positionMap = lobby["lobbyPosition"] as HashMap<String,Any>
                                        if( CoordinatesUtil.distance(geoPoint, GeoPoint((positionMap["latitude"] as Double)/*.toDouble()*/  , (positionMap["longitude"] as Double)/*.toDouble()*/ ) ) > Constants.GAME_AREA_RADIUS  ){
                                            leaveMM(false)
                                        }
                                    }
                                } )

                            } else {
                                if(!private){
                                    publicSearch(toSearchId)  //if the join failed, search a lobby again, this one will probably not exist anymore
                                }
                            }
                        }

                    })


            }

        }

        gpsPositionManager.listenersManager.addCall(checkOutWithPos)
        gpsPositionManager.updateLocation()

    }

    /**
     * Create a public lobby with the specified ID
     * Inserts the lobby data into the DB and setups the listeners and UI for search
     */
    private fun createPublicLobby(id: String) {

        val publicCreation = object : GeoPointListener{
            override fun invoke(geoPoint: GeoPoint) {
                gpsPositionManager.listenersManager.removeCall(this)

                val lobby = Lobby(id, nbPlayer, activeUser,geoPoint)
                currentLobbyId = id
                db.update("MM/$gamemode/$map/$lobbyType/freeLobbies", id, lobby)
                setupLobbyListener()
                uiToSearch()

                gpsPositionUpdater.initTimer()
                gpsPositionManager.listenersManager.addCall( { gp ->
                    db.referenceGet("MM/$gamemode/$map/$lobbyType/freeLobbies",id ).addOnSuccessListener { snapshot ->
                        val lobby = snapshot.value as HashMap<String,Any>? ?: return@addOnSuccessListener
                        val positionMap = lobby["lobbyPosition"] as HashMap<String,Any>
                        if( CoordinatesUtil.distance(geoPoint, GeoPoint((positionMap["latitude"] as Double)/*.toDouble()*/  , (positionMap["longitude"] as Double)/*.toDouble()*/ ) ) > Constants.GAME_AREA_RADIUS  ){
                            leaveMM(false)
                        }
                    }
                } )

            }

        }

        gpsPositionManager.listenersManager.addCall(publicCreation)
        gpsPositionManager.updateLocation()

    }

    /**
     * Create a private lobby using the ID in the TextField of the UI
     * Works the same way as the public lobby creation except for :
     * - has to insert the lobbyID in the freeList too
     * - has to make sure no other lobby with the same ID exists : no repetitions are allowed
     */
    fun createPrivateLobby(view: View) {
        lobbyType = "private"
        val id = findViewById<EditText>(R.id.lobbyIdInsert).text
        if (id.isEmpty()) {   //the ID can not be empty
            changeVisibility<TextView>(R.id.errorIdNonEmpty, View.VISIBLE)
            return
        }

        val privateCreation = object : GeoPointListener{
            override fun invoke(geoPoint: GeoPoint) {
                gpsPositionManager.listenersManager.removeCall(this)
                val lobby = Lobby(currentLobbyId, nbPlayer, activeUser, geoPoint)
                db.referenceGet("MM/$gamemode/$map/$lobbyType", "freeList").addOnSuccessListener { free ->
                    val ls = free.value as MutableList<String>
                    if (ls.contains(id.toString())) {
                        changeVisibility<TextView>(R.id.errorIdExists, View.VISIBLE)
                    } else {
                        currentLobbyId = id.toString()
                        app.setLobbyID(currentLobbyId)
                        ls.add(currentLobbyId)
                        db.update("MM/$gamemode/$map/$lobbyType", "freeList", ls)
                        db.update("MM/$gamemode/$map/$lobbyType/freeLobbies", currentLobbyId, lobby)
                        setupLobbyListener()
                        uiToSearch()

                        gpsPositionUpdater.initTimer()
                        gpsPositionManager.listenersManager.addCall( { gp ->
                            db.referenceGet("MM/$gamemode/$map/$lobbyType/freeLobbies",currentLobbyId ).addOnSuccessListener { snapshot ->
                                val lobby = snapshot.value as HashMap<String,Any>? ?: return@addOnSuccessListener
                                val positionMap = lobby["lobbyPosition"] as HashMap<String,Any>
                                if( CoordinatesUtil.distance(geoPoint, GeoPoint((positionMap["latitude"] as Double)/*.toDouble()*/  , (positionMap["longitude"] as Double)/*.toDouble()*/ ) ) > Constants.GAME_AREA_RADIUS  ){
                                    leaveMM(false)
                                }
                            }
                        } )


                    }
                }

            }

        }

        gpsPositionManager.listenersManager.addCall(privateCreation)
        gpsPositionManager.updateLocation()

    }

    /**
     * Join a private lobby : uses the same method as the public lobby except
     * - it checks that the lobby ID exists in the freeList
     */
    fun joinPrivateLobby(view: View) {
        lobbyType = "private"
        val id = findViewById<EditText>(R.id.lobbyIdInsert).text
        if (id.isEmpty()) {
            changeVisibility<TextView>(R.id.errorIdNonEmpty, View.VISIBLE)
            return
        }

        val privateJoining = object : GeoPointListener{
            override fun invoke(geoPoint: GeoPoint) {
                gpsPositionManager.listenersManager.removeCall(this)
                db.referenceGet("MM/$gamemode/$map/$lobbyType", "freeList").addOnSuccessListener { free ->
                    val ls = free.value as MutableList<String>
                    if (ls.contains(id.toString())) { //the lobby has to be searchable
                        checkOutLobby(id.toString(),true)
                    } else {
                        changeVisibility<TextView>(R.id.errorIdNotFound, View.VISIBLE)
                    }
                }
            }

        }


        gpsPositionManager.listenersManager.addCall(privateJoining)
        gpsPositionManager.updateLocation()

    }

    /**
     * Update the UI of the lobby search            TODO : show when the lobby is launching + the amount of players compared to the MAX
     * - updates the list of players in the lobby
     */
    private fun updateUI() {

        //REPLACE WITH PARTIAL USERS ONCE IT IS IMPLEMENTED
        //PARTIAL USER : should replace getFriendsList with the list of partials users (the players)
        val friendRecyclerView = findViewById<RecyclerView>(R.id.playersRecycler)
        val friendAdapter = FriendViewAdapter(  //reuses the friend list format without any buttons
            applicationContext,
            (lobbyMap["lobbyPlayers"] as ArrayList<MutableMap<String, Any>>).map { p ->
                PartialUser(p["username"] as String, p["uid"] as String)
            },
            2
        )
        friendRecyclerView.adapter = friendAdapter
        friendRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
    }

    /**
     * Transition to the game screen : leaving the match making
     */
    private fun gameScreenTransition(){
        val intent = Intent(applicationContext, GameVersusViewActivity::class.java)

        db.update("GameInstance/Game" + this.currentLobbyId + "/id:" + activeUser.uid, "finish", 0)

        intent.putExtra("gid",this.currentLobbyId)
        intent.putExtra("uid",activeUser.uid)
        intent.putExtra("nbPlayer",nbPlayer)
        intent.putExtra("gameMode",gamemode)

        startActivity(intent)
    }

    /**
     * Leave the MatchMaking system
     * It depends on multiple factors :
     * - the player is in a launching lobby, meaning he should be directed to the game screen
     * - the players is the leader of the lobby, another player has to selected
     * - the player is alone in the lobby , meaning the lobby has to be deleted
     */
    private fun leaveMM(toGame: Boolean) {
        if (isGroupVisible(R.id.setupGroup)) {    //nothing should be done if the player is not in a lobby
            return
        }
        db.getDbReference("MM/$gamemode/$map/$lobbyType")
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val lobbyTypeLevel =
                        currentData.value as MutableMap<String, Any>? ?: return Transaction.success(
                            currentData
                        )
                    val path = if (toGame) { //depending if the game is launching the path changes
                        "launchLobbies"
                    } else {
                        db.getDbReference("MM/$gamemode/$map/$lobbyType/freeLobbies/$currentLobbyId")
                            .removeEventListener(lobbyListener)
                        "freeLobbies"
                    }

                    val lobbyState = lobbyTypeLevel[path] as MutableMap<String, Any>?
                        ?: return Transaction.success(currentData)
                    var lobby = lobbyState[currentLobbyId] as MutableMap<String, Any>?
                        ?: return Transaction.success(currentData)

                    if (lobby["lobbyLeader"] as String == activeUser.uid) {
                        if (lobby["lobbyCount"] as Long == 1L) {
                            if (!toGame) {
                                val ls = lobbyTypeLevel["freeList"] as ArrayList<String>?
                                    ?: return Transaction.success(currentData)
                                ls.remove(currentLobbyId)
                                lobbyTypeLevel["freeList"] = ls
                            }
                            lobbyState.remove(currentLobbyId)
                        } else {
                            lobby = removePlayerFromList(lobby)
                            lobby["lobbyLeader"] =
                                (lobby["lobbyPlayers"] as ArrayList<MutableMap<String, Any>>)[0]["uid"] as String //use the next player as the new leader
                            lobbyState[currentLobbyId] = lobby
                        }
                    } else {
                        lobby = removePlayerFromList(lobby)
                        lobbyState[currentLobbyId] = lobby
                    }
                    lobbyTypeLevel[path] = lobbyState
                    currentData.value = lobbyTypeLevel
                    return Transaction.success(currentData)
                }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
               gpsPositionUpdater.stopUpdates()
               gpsPositionManager.listenersManager.clearAllCalls()
               if(toGame){
                   gameScreenTransition()
               }else{
                   uiToSetup()
               }
            }

            })

    }

    private fun removePlayerFromList(lobby: MutableMap<String, Any>): MutableMap<String, Any> {

        lobby["lobbyCount"] = lobby["lobbyCount"] as Long - 1
        val userMap = HashMap<String, Any>()
        userMap["username"] = activeUser.username
        userMap["uid"] = activeUser.uid
        (lobby["lobbyPlayers"] as ArrayList<MutableMap<String, Any>>).remove(userMap)
        return lobby

    }

    private fun isGroupVisible(id: Int): Boolean {
        return findViewById<Group>(id).visibility == View.VISIBLE
    }

    private fun <T : View> changeVisibility(id: Int, visibility: Int) {
        findViewById<T>(id).visibility = visibility
    }


    override fun onDestroy() {
        leaveMM(false)
        super.onDestroy()
    }

    override fun onPause() {
        leaveMM(false)
        uiToSetup()
        super.onPause()
    }


    fun onCancelButton(v: View) {
        leaveMM(false)
        uiToSetup()
    }

    fun onPublicLobbySearchButton(v: View) {
        lobbyType = "public"
        publicSearch()
    }

    /**
     * UI transition between multiple groups of UI elements : done to keep the same activity
     */
    private fun uiToSetup() {
        changeVisibility<Group>(R.id.setupGroup, View.VISIBLE)
        changeVisibility<Group>(R.id.waitGroup, View.INVISIBLE)
        if (lobbyType == "private") {
            changeVisibility<Group>(R.id.privateGroup, View.INVISIBLE)
        }
        changeVisibility<Group>(R.id.errorGroup, View.INVISIBLE)

        val app = applicationContext as MyApplication
        app.getMessageHandler().addListener()
    }

    private fun uiToSearch() {
        changeVisibility<Group>(R.id.setupGroup, View.INVISIBLE)
        changeVisibility<Group>(R.id.waitGroup, View.VISIBLE)
        if (lobbyType == "private") {
            changeVisibility<Group>(R.id.privateGroup, View.VISIBLE)
            findViewById<TextView>(R.id.lobbyIdWaitShowing).text = currentLobbyId
        }
        changeVisibility<Group>(R.id.errorGroup, View.INVISIBLE)

        val app = applicationContext as MyApplication
        app.getMessageHandler().removeListener()
    }

    /**
     * Setup the listener for the free lobby
     */
    private fun setupLobbyListener() {
        db.getDbReference("MM/$gamemode/$map/$lobbyType/freeLobbies/$currentLobbyId")
            .addValueEventListener(lobbyListener)
    }

    /**
     * Remove the listener for the free lobby and setup the one for the launching lobby
     */
    private fun setupLaunchListener() {
        db.getDbReference("MM/$gamemode/$map/$lobbyType/freeLobbies/$currentLobbyId")
            .removeEventListener(lobbyListener)
        db.getDbReference("MM/$gamemode/$map/$lobbyType/launchLobbies/$currentLobbyId")
            .addValueEventListener(launchLobbyListener)
    }

}

/**
 * A lobby : used exclusively to insert the data into the DB since it cannot be retrieved in such form later
 */
class Lobby(id: String, max_p: Long, leader: PartialUser , gp : GeoPoint) {
    val lobbyId = id
    val lobbyMax = max_p
    var lobbyCount = 1
    var lobbyLaunch = false
    var lobbyLeader = leader.uid
    var lobbyPlayers =
        mutableListOf<PartialUser>(leader)    //PARTIAL USER : should be a list of partial users
    var lobbyPosition : GeoPoint = gp
}