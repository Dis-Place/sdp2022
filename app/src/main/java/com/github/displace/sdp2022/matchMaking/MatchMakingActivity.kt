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
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.GoodDB
import com.github.displace.sdp2022.database.TransactionSpecification
import com.github.displace.sdp2022.profile.achievements.AchievementsLibrary
import com.github.displace.sdp2022.profile.friends.FriendViewAdapter
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.PartialUser
import com.github.displace.sdp2022.util.gps.GPSPositionManager
import com.github.displace.sdp2022.util.gps.GPSPositionUpdater
import com.github.displace.sdp2022.util.gps.GeoPointListener
import com.github.displace.sdp2022.util.listeners.Listener
import com.github.displace.sdp2022.util.math.Constants
import com.github.displace.sdp2022.util.math.CoordinatesUtil
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
    // private lateinit var db: RealTimeDatabase
    private lateinit  var db : GoodDB
    private var currentLobbyId = ""

    private var gamemode = "Versus"
    private val map = "Map2"

    //This has to be chaged to the real active user
    private var activePartialUser = PartialUser("active","0")
    //indicates if the lobby is public or private
    private var lobbyType: String = "private"
    //keeps a map of the lobby : just how the DB stores it
    private var lobbyMap : MutableMap<String,Any> = HashMap()
    private lateinit var app : MyApplication
    private var nbPlayer = 2L

    private lateinit var gpsPositionManager : GPSPositionManager
    private lateinit var gpsPositionUpdater : GPSPositionUpdater

    private var debug: Boolean = false

    /**
     * Listener for the lobby while it is still setting up : players can find it and join it
     * It takes care of :
     * - updating the UI : list of players
     * - maintaining the lobbyMap variable
     * - check if the lobby is ready to be launched (only by the leader)
     */
    private val lobbyListener = Listener<MutableMap<String, Any>?>{ lobby ->
        if(lobby == null)
            return@Listener
        lobbyMap = lobby
        updateUI()
        if(lobbyMap["lobbyCount"] as Long == lobbyMap["lobbyMax"] as Long  ){
            lobbyMap["lobbyLaunch"] = true
            setupLaunchListener()

            //UI
            findViewById<Button>(R.id.MMCancelButton).visibility = View.INVISIBLE

            if (lobbyMap["lobbyLeader"] as String == activePartialUser.uid) {    //This client is the leader : perform the checks and launch if needed
                db.getThenCall<ArrayList<String>?>("MM/$gamemode/$map/$lobbyType/freeList") { ls ->

                    if (ls != null) {
                        ls.remove(currentLobbyId) //remove this lobby from the free list
                        db.update("MM/$gamemode/$map/$lobbyType/freeList", ls)
                    }
                    db.delete("MM/$gamemode/$map/$lobbyType/freeLobbies/$currentLobbyId")
                    db.update(
                        "MM/$gamemode/$map/$lobbyType/launchLobbies/$currentLobbyId",
                        lobbyMap
                    )    //add this lobby to the launching lobbies
                }

            }
        }
    }


    /**
     * Listener for the launching lobby : this lobby is leaking players into the game mode
     * It takes care of :
     * - Maintaining the lobbyMap variable
     * - Making the transition to the game screen
     */
    private val launchLobbyListener = Listener<MutableMap<String, Any>?>{ lobby ->
        if(lobby == null)
            return@Listener

        lobbyMap = lobby
        if(lobbyMap["lobbyLeader"] as String == activePartialUser.uid ){
            leaveMM(true)
        }
    }

    /**
     * On creation of the activity we must :
     * - setup the friend list for a potential private lobby : the 1 indicates that there are no message buttons
     * - show the right group of UI elements : the ones for setup
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_making) //UI

        /*
        set up the database
         */
        db = DatabaseFactory.getDB(intent)
        /*
        set up the gps managers
         */
        gpsPositionManager = GPSPositionManager(this)
        gpsPositionUpdater = GPSPositionUpdater(this,gpsPositionManager)
        gpsPositionUpdater.stopUpdates()

        /*
        set up the user infromation
         */
        app = applicationContext as MyApplication
        val activeUser = app.getActiveUser()!!
        activePartialUser = activeUser.getPartialUser()

        /*
        set up the number of players and game mode
         */
        nbPlayer = intent.getLongExtra("nbPlayer",2L)
        gamemode = try {
            intent.getStringExtra("gameMode")!!
        }catch (e: Exception){
            "Versus"
        }

        /*
        set up the mock for the gps location
         */
        debug = intent.getBooleanExtra("DEBUG", false)
        if (debug) { //mock the position for everyone if testing is ongoing
            gpsPositionManager.mockProvider(GeoPoint(0.00001,0.00001))
        }

        /*
        set up the UI elements
         */
        setFriendList(activeUser) //UI
        uiToSetup() //UI
    }

    /**
     * Sets up the list of friends used to be able to invite them to a private lobby
     * @param activeUser : the active user, which is the source of the friends
     */
    private fun setFriendList( activeUser : CompleteUser){
        /* Friends in private lobby */
        val friendRecyclerView = findViewById<RecyclerView>(R.id.friendsMMRecycler)
        val friends = activeUser.getFriendsList()

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
     * Always search the first close enough lobby of the list, so that they fill in an orderly manner
     * If no public lobbies are available, create one and add it to the list
     *
     * This is done in a Transaction such as if another player searches for a lobby, creation is separated
     *
     * @param lastId : the id of the last lobby we checked out
     */
    private fun publicSearch( lastId : String = "head" ) {
        var toCreateId = ""
        var toSearchId = ""

        val publicSearchTransaction : TransactionSpecification<ArrayList<String>> =
            TransactionSpecification.Builder<ArrayList<String>> { ls ->
                toCreateId = ""
                toSearchId = ""

                val idx : Int = ls!!.indexOf(lastId)+1
                if (idx == ls.size) {
                    //only the head exists : add a new ID to the list and create a new lobby with that ID
                    //we reached the end of the list, create a new lobby
                    val nextId = Random.nextLong().toString()

                    ls.add(nextId)

                    toCreateId = nextId
                } else {
                    //more than only the head exists : check the first one out
                    toSearchId = ls[idx]
                }

                return@Builder ls
            }.preCheckChange { ls ->
                val idx : Int = ls!!.indexOf(lastId)+1
                idx != 0
            }.onCompleteChange { committed ->
                if (!committed) {
                    publicSearch()
                    return@onCompleteChange
                }
                if (toCreateId != "") {   //we need to create a lobby
                    createPublicLobby(toCreateId)
                    return@onCompleteChange
                }
                //we need to check out a lobby
                checkOutLobby(toSearchId,false)

            }.build()

        db.runTransaction("MM/$gamemode/$map/$lobbyType/freeList",publicSearchTransaction)

    }

    /**
     * Check out the lobby with the specific ID
     *
     * Add yourself to the list of players of the lobby and increase the count, it also sets up the listeners
     * It is done in a transaction such as if another player tries to join the same lobby only one of you is successful
     *
     * @param toSearchId : the id of the lobby to check out
     * @param private : signals if the lobby is private (value == true)
     */
    private fun checkOutLobby(toSearchId: String , private : Boolean) {


        val checkOutWithPos = object : GeoPointListener{
            override fun invoke(geoPoint: GeoPoint) {
                gpsPositionManager.listenersManager.removeCall(this)

                val checkOutTransaction : TransactionSpecification<MutableMap<String, Any>> =
                    TransactionSpecification.Builder<MutableMap<String, Any>> { lobby ->

                        lobby!!["lobbyCount"] = lobby["lobbyCount"] as Long + 1
                        val ls = lobby["lobbyPlayers"] as ArrayList<MutableMap<String, Any>>
                        val userMap = activePartialUser.asMap()

                        ls.add(userMap)
                        lobby["lobbyPlayers"] = ls

                        return@Builder lobby
                    }.preCheckChange { lobby ->

                        val positionMap = lobby!!["lobbyPosition"] as HashMap<String,Any>
                        positionCondition(geoPoint,positionMap)

                    }.onCompleteChange { committed ->
                        if (committed) {  //setups the listeners and makes the UI transition
                            currentLobbyId = toSearchId
                            app.setLobbyID(currentLobbyId)
                            setupLobbyListener()
                            uiToSearch() //UI

                            positionCheckOnTimer()

                        } else {
                            if(!private){
                                publicSearch(toSearchId)  //if the join failed, search a lobby again, this one will probably not exist anymore
                            }
                        }
                    }.build()

                db.runTransaction("MM/$gamemode/$map/$lobbyType/freeLobbies/$toSearchId",checkOutTransaction)

            }

        }

        gpsPositionManager.listenersManager.addCall(checkOutWithPos)
        gpsPositionManager.updateLocation()

    }

    /**
     * Create a public lobby with the specified ID
     * Inserts the lobby data into the DB and setups the listeners and UI for search
     *
     * @param id : the id of the lobby to create
     */
    private fun createPublicLobby(id: String) {

        val publicCreation = object : GeoPointListener{
            override fun invoke(geoPoint: GeoPoint) {
                gpsPositionManager.listenersManager.removeCall(this)

                val lobby = Lobby(id, nbPlayer, activePartialUser,geoPoint)
                currentLobbyId = id
                db.update("MM/$gamemode/$map/$lobbyType/freeLobbies/$id", lobby)
                setupLobbyListener()
                uiToSearch() //UI

                positionCheckOnTimer()

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
        val id = checkNonEmpty()
        if(id.isEmpty()){
            return
        }

        val privateCreation = object : GeoPointListener{
            override fun invoke(geoPoint: GeoPoint) {
                gpsPositionManager.listenersManager.removeCall(this)
                val lobby = Lobby(currentLobbyId, nbPlayer, activePartialUser, geoPoint)

                db.getThenCall<MutableList<String>>("MM/$gamemode/$map/$lobbyType/freeList"
                ) { ls ->
                    if (ls!!.contains(id)) {
                        changeVisibility<TextView>(R.id.errorIdExists, View.VISIBLE)
                    } else {
                        currentLobbyId = id
                        app.setLobbyID(currentLobbyId)
                        ls.add(currentLobbyId)
                        db.update("MM/$gamemode/$map/$lobbyType/freeList", ls)
                        db.update("MM/$gamemode/$map/$lobbyType/freeLobbies/$currentLobbyId", lobby)
                        setupLobbyListener()
                        uiToSearch() //UI

                        positionCheckOnTimer()

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

        val id = checkNonEmpty()
        if(id.isEmpty()){
            return
        }
        val privateJoining = object : GeoPointListener{
            override fun invoke(geoPoint: GeoPoint) {
                gpsPositionManager.listenersManager.removeCall(this)

                db.getThenCall<MutableList<String>>("MM/$gamemode/$map/$lobbyType/freeList") { ls ->
                    if (ls!!.contains(id)) { //the lobby has to be searchable
                        checkOutLobby(id, true)
                    } else {
                        changeVisibility<TextView>(R.id.errorIdNotFound, View.VISIBLE)
                    }
                }

            }

        }


        gpsPositionManager.listenersManager.addCall(privateJoining)
        gpsPositionManager.updateLocation()

    }

    private fun checkNonEmpty() : String {
        lobbyType = "private"
        val id = findViewById<EditText>(R.id.lobbyIdInsert).text.toString()
        if (id.isEmpty()) { //UI  //the ID can not be empty
            changeVisibility<TextView>(R.id.errorIdNonEmpty, View.VISIBLE)
        }
        return id
    }

    /**
     * Update the UI of the lobby search
     * - updates the list of players in the lobby
     */
    private fun updateUI() {

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

        db.update("GameInstance/Game" + this.currentLobbyId + "/id:" + activePartialUser.uid + "/finish", 0)

        intent.putExtra("gid",this.currentLobbyId)
        intent.putExtra("uid",activePartialUser.uid)
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
        } //UI

        val leaveMMTransaction : TransactionSpecification<MutableMap<String, Any>> =
            TransactionSpecification.Builder<MutableMap<String, Any>> { lobbyTypeLevel ->

                val path = getPath(toGame)
                val lobbyState = lobbyTypeLevel!![path] as MutableMap<String, Any>
                var lobby = lobbyState[currentLobbyId] as MutableMap<String, Any>

                lobby = removePlayerFromList(lobby)
                lobbyState[currentLobbyId] = lobby

                if (lobby["lobbyLeader"] as String == activePartialUser.uid && lobby["lobbyCount"] as Long == 1L) { //leader and alone?

                    val ls = lobbyTypeLevel["freeList"] as ArrayList<String>
                    ls.remove(currentLobbyId)
                    lobbyTypeLevel["freeList"] = ls
                    lobbyState.remove(currentLobbyId)

                }
                lobbyTypeLevel[path] = lobbyState

                return@Builder lobbyTypeLevel
            }.onCompleteChange { committed ->
                if(committed) {
                    gpsPositionUpdater.stopUpdates()
                    gpsPositionManager.listenersManager.clearAllCalls()
                    if(toGame){
                        gameScreenTransition()
                    }else{
                        uiToSetup() //UI
                    }
                }else{
                    leaveMM(toGame)
                }
            }.build()

        db.runTransaction("MM/$gamemode/$map/$lobbyType",leaveMMTransaction)


    }

    /**
     * Get the path for the database
     * @param toGame : if the user is leaving the lobby to the game or to the menu
     */
    private fun getPath(toGame : Boolean) : String {
        return if (toGame) { //depending if the game is launching the path changes
            db.removeListener("MM/$gamemode/$map/$lobbyType/launchLobbies/$currentLobbyId",launchLobbyListener)
            "launchLobbies"
        } else {
            db.removeListener("MM/$gamemode/$map/$lobbyType/freeLobbies/$currentLobbyId",lobbyListener)
            "freeLobbies"
        }
    }

    /**
     * Set up the check for position linked to a timer
     * If the position is too far from the lobby, leave the lobby
     */
    private fun positionCheckOnTimer(){
        gpsPositionUpdater.initTimer()
        gpsPositionManager.listenersManager.addCall( { gp ->
            val positionMap = lobbyMap["lobbyPosition"] as MutableMap<String,Any>
            if( !positionCondition(gp,positionMap) ){
                leaveMM(false)
            }

        } )
    }

    private fun positionCondition(gp : GeoPoint , positionMap : MutableMap<String,Any> ) : Boolean{
        return CoordinatesUtil.distance(gp, GeoPoint((positionMap["latitude"] as Double)  , (positionMap["longitude"] as Double) ) ) <= Constants.GAME_AREA_RADIUS
    }
    /**
     * Remove the current player from the lobby only if it is not the only player left and change the leader if needed
     * @param lobby : the lobby to modify, in the form of a map so that it can be used by the database
     * @return the modified lobby
     */
    private fun removePlayerFromList(lobby: MutableMap<String, Any>): MutableMap<String, Any> {

        if(lobby["lobbyCount"] as Long == 1L){
            return lobby
        }
        lobby["lobbyCount"] = lobby["lobbyCount"] as Long - 1
        val userMap = HashMap<String, Any>()
        userMap["username"] = activePartialUser.username
        userMap["uid"] = activePartialUser.uid
        (lobby["lobbyPlayers"] as ArrayList<MutableMap<String, Any>>).remove(userMap)

        if (lobby["lobbyLeader"] as String == activePartialUser.uid) { //leader?
            lobby["lobbyLeader"] = (lobby["lobbyPlayers"] as ArrayList<MutableMap<String, Any>>)[0]["uid"] as String //use the next player as the new leader
        }
        return lobby

    }

    /**
     * Checks if the Group (UI elements) is visible
     * @param id : id of the group
     * @return true if the given group is visible
     */
    private fun isGroupVisible(id: Int): Boolean { //UI
        return findViewById<Group>(id).visibility == View.VISIBLE
    }

    /**
     * Change the visibility of the UI element
     * @param T : the type of the UI element
     * @param id : id of the UI element
     * @param visibility : VISIBLE / INVISIBLE / GONE
     */
    private fun <T : View> changeVisibility(id: Int, visibility: Int) { //UI
        findViewById<T>(id).visibility = visibility
    }

    /**
     * When the activity is destroyed, leave the current lobby
     */
    override fun onDestroy() { //UI
        leaveMM(false)
        super.onDestroy()
    }

    /**
     * When the activity is paused, leave the current lobby
     */
    override fun onPause() { //UI
        leaveMM(false)
        uiToSetup()
        super.onPause()
    }

    /**
     * When the user cancels the search, leave the current lobby and return to the setup screen
     */
    fun onCancelButton(v: View) { //UI
        leaveMM(false)
        uiToSetup()
    }

    /**
     * When the user starts the search
     */
    fun onPublicLobbySearchButton(v: View) {
        lobbyType = "public"
        publicSearch()
    }

    /**
     * UI transition between multiple groups of UI elements : done to keep the same activity
     * Goes from Search to Setup
     */
    private fun uiToSetup() { //UI
        changeVisibility<Group>(R.id.setupGroup, View.VISIBLE)
        changeVisibility<Group>(R.id.waitGroup, View.INVISIBLE)
        if (lobbyType == "private") {
            changeVisibility<Group>(R.id.privateGroup, View.INVISIBLE)
        }
        changeVisibility<Group>(R.id.errorGroup, View.INVISIBLE)

        val app = applicationContext as MyApplication
        app.getMessageHandler().checkForNewMessages()

    }

    /**
     * UI transition between multiple groups of UI elements : done to keep the same activity
     * Goes from Setup to Search
     */
    private fun uiToSearch() { //UI
        changeVisibility<Group>(R.id.setupGroup, View.INVISIBLE)
        changeVisibility<Group>(R.id.waitGroup, View.VISIBLE)
        if (lobbyType == "private") {
            changeVisibility<Group>(R.id.privateGroup, View.VISIBLE)
            findViewById<TextView>(R.id.lobbyIdWaitShowing).text = currentLobbyId
        }
        changeVisibility<Group>(R.id.errorGroup, View.INVISIBLE)


        //check for match making achievements
        AchievementsLibrary.achievementCheck(app.getActiveUser()!!,lobbyType == "private",AchievementsLibrary.mmtLib)

    }

    /**
     * Setup the listener for the free lobby
     */
    private fun setupLobbyListener() {
        db.addListener("MM/$gamemode/$map/$lobbyType/freeLobbies/$currentLobbyId",lobbyListener)
    }

    /**
     * Remove the listener for the free lobby and setup the one for the launching lobby
     */
    private fun setupLaunchListener() {
        db.removeListener("MM/$gamemode/$map/$lobbyType/freeLobbies/$currentLobbyId",lobbyListener)
        db.addListener("MM/$gamemode/$map/$lobbyType/launchLobbies/$currentLobbyId",launchLobbyListener)
    }

}

/**
 * A lobby : used exclusively to insert the data into the DB since it cannot be retrieved in such form later
 *
 * @param id : id of the new lobby
 * @param max_p : the maximum amount of players in the lobby
 * @param leader : the id of the leader of the lobby
 * @param gp : the gps position of the lobby
 */
class Lobby(id: String, max_p: Long, leader: PartialUser , gp : GeoPoint) {
    val lobbyId = id
    val lobbyMax = max_p
    var lobbyCount = 1
    var lobbyLaunch = false
    var lobbyLeader = leader.uid
    var lobbyPlayers = mutableListOf(leader)    //PARTIAL USER : should be a list of partial users
    var lobbyPosition : GeoPoint = gp
}