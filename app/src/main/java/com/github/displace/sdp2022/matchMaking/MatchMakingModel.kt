package com.github.displace.sdp2022.matchMaking

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.github.displace.sdp2022.GameVersusViewActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.GoodDB
import com.github.displace.sdp2022.database.TransactionSpecification
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

class MatchMakingModel( val activity: MMView ){

    //Database
    var db : GoodDB = DatabaseFactory.getDB(activity.intent)
    var currentLobbyId = ""

    private var gamemode = "Versus"
    private val map = "Map2"

    //This has to be chaged to the real active user
    private var activePartialUser = PartialUser("active","0")
    //indicates if the lobby is public or private
    var lobbyType: String = "private"
    //keeps a map of the lobby : just how the DB stores it
    var lobbyMap : MutableMap<String,Any> = HashMap()
    var app : MyApplication
    private var nbPlayer = 2L

    private  var gpsPositionManager : GPSPositionManager = GPSPositionManager(activity)
    private var gpsPositionUpdater : GPSPositionUpdater =
        GPSPositionUpdater(activity,gpsPositionManager)

    private var debug: Boolean = false
    var activeUser : CompleteUser


    init{
        /*
        set up the gps managers
         */
        gpsPositionUpdater.stopUpdates()

        /*
        set up the user infromation
         */
        app = activity.applicationContext as MyApplication
        activeUser = app.getActiveUser()!!
        activePartialUser = activeUser.getPartialUser()

        /*
        set up the number of players and game mode
         */
        nbPlayer = activity.intent.getLongExtra("nbPlayer",2L)
        gamemode = try {
            activity.intent.getStringExtra("gameMode")!!
        }catch (e: Exception){
            "Versus"
        }

        /*
        set up the mock for the gps location
         */
        debug = activity.intent.getBooleanExtra("DEBUG", false)
        if (debug) { //mock the position for everyone if testing is ongoing
            gpsPositionManager.mockProvider(GeoPoint(0.00001,0.00001))
        }


    }


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
        activity.updateUI()
        /*
         * The leader notifies all players that the lobby is launching
         */
        if(lobbyMap["lobbyCount"] as Long == lobbyMap["lobbyMax"] as Long && lobbyMap["lobbyLeader"] as String == activePartialUser.uid && lobbyMap["lobbyLaunch"] == false   ) {
            lobbyMap["lobbyLaunch"] = true
            db.update(
                "MM/$gamemode/$map/$lobbyType/freeLobbies/$currentLobbyId",
                lobbyMap
            )
            return@Listener
        }
        /*
         * The lobby is launching : the leader will remove the lobby as a "free" lobby and add it as a launching one
         * All the other players will wait until the launching lobby is present and they turn to leave has come
         */
        if( lobbyMap["lobbyLaunch"] == true) {
            setupLaunchListener()
            //UI
            activity.findViewById<Button>(R.id.MMCancelButton).visibility = View.INVISIBLE

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
     * Search a public lobby
     * Always search the first close enough lobby of the list, so that they fill in an orderly manner
     * If no public lobbies are available, create one and add it to the list
     *
     * This is done in a Transaction such as if another player searches for a lobby, creation is separated
     *
     * @param lastId : the id of the last lobby we checked out
     */
    fun publicSearch( lastId : String = "head" ) {
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


        val checkOutWithPos = object : GeoPointListener {
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

                            activity.uiToSearch() //UI

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

        val publicCreation = object : GeoPointListener {
            override fun invoke(geoPoint: GeoPoint) {
                gpsPositionManager.listenersManager.removeCall(this)

                val lobby = Lobby(id, nbPlayer, activePartialUser,geoPoint)
                currentLobbyId = id
                db.update("MM/$gamemode/$map/$lobbyType/freeLobbies/$id", lobby)
                setupLobbyListener()
                activity.uiToSearch() //UI

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
    fun createPrivateLobby() {
        val id = activity.checkNonEmpty()
        if(id.isEmpty()){
            return
        }

        val privateCreation = object : GeoPointListener {
            override fun invoke(geoPoint: GeoPoint) {
                gpsPositionManager.listenersManager.removeCall(this)
                val lobby = Lobby(currentLobbyId, nbPlayer, activePartialUser, geoPoint)

                db.getThenCall<MutableList<String>>("MM/$gamemode/$map/$lobbyType/freeList"
                ) { ls ->
                    if (ls!!.contains(id)) {
                        activity.changeVisibility<TextView>(R.id.errorIdExists, View.VISIBLE)
                    } else {
                        currentLobbyId = id
                        app.setLobbyID(currentLobbyId)
                        ls.add(currentLobbyId)
                        db.update("MM/$gamemode/$map/$lobbyType/freeList", ls)
                        db.update("MM/$gamemode/$map/$lobbyType/freeLobbies/$currentLobbyId", lobby)
                        setupLobbyListener()
                        activity.uiToSearch() //UI

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
    fun joinPrivateLobby() {

        val id = activity.checkNonEmpty()
        if(id.isEmpty()){
            return
        }
        val privateJoining = object : GeoPointListener {
            override fun invoke(geoPoint: GeoPoint) {
                gpsPositionManager.listenersManager.removeCall(this)

                db.getThenCall<MutableList<String>>("MM/$gamemode/$map/$lobbyType/freeList") { ls ->
                    if (ls!!.contains(id)) { //the lobby has to be searchable
                        checkOutLobby(id, true)
                    } else {
                        activity.changeVisibility<TextView>(R.id.errorIdNotFound, View.VISIBLE)
                    }
                }

            }

        }


        gpsPositionManager.listenersManager.addCall(privateJoining)
        gpsPositionManager.updateLocation()

    }

    /**
     * Leave the MatchMaking system
     * It depends on multiple factors :
     * - the player is in a launching lobby, meaning he should be directed to the game screen
     * - the players is the leader of the lobby, another player has to selected
     * - the player is alone in the lobby , meaning the lobby has to be deleted
     */
    fun leaveMM(toGame: Boolean) {
        if (activity.isGroupVisible(R.id.setupGroup)) {    //nothing should be done if the player is not in a lobby
            return
        } //UI

        val leaveMMTransaction : TransactionSpecification<MutableMap<String, Any>> =
            TransactionSpecification.Builder<MutableMap<String, Any>> { lobbyTypeLevel ->

                val path = getPath(toGame)
                val lobbyState = lobbyTypeLevel!![path] as MutableMap<String, Any>? ?: return@Builder lobbyTypeLevel
                var lobby = lobbyState[currentLobbyId] as MutableMap<String, Any>? ?: return@Builder lobbyTypeLevel


                val leader = lobby["lobbyLeader"] as String
                val count = lobby["lobbyCount"] as Long
                if(leader == activePartialUser.uid){
                    if(count == 1L){
                        if(!toGame){
                            val ls = lobbyTypeLevel["freeList"] as ArrayList<String>? ?:  return@Builder lobbyTypeLevel
                            ls.remove(currentLobbyId)
                            lobbyTypeLevel["freeList"] = ls
                        }
                        lobbyState.remove(currentLobbyId)
                    }else{
                        lobby = removePlayerFromList(lobby)
                        val players = lobby["lobbyPlayers"] as ArrayList<MutableMap<String, Any>>? ?:  return@Builder lobbyTypeLevel
                        lobby["lobbyLeader"] = players[0]["uid"] as String //use the next player as the new leader
                        lobbyState[currentLobbyId] = lobby
                    }
                }else{
                    lobby = removePlayerFromList(lobby)
                    lobbyState[currentLobbyId] = lobby
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
                        activity.uiToSetup() //UI
                    }
                }else{
                    leaveMM(toGame)
                }
            }.build()

        db.runTransaction("MM/$gamemode/$map/$lobbyType",leaveMMTransaction)


    }

    /**
     * Remove the current player from the lobby only if it is not the only player left and change the leader if needed
     * @param lobby : the lobby to modify, in the form of a map so that it can be used by the database
     * @return the modified lobby
     */
    private fun removePlayerFromList(lobby: MutableMap<String, Any>): MutableMap<String, Any> {

     /*   if(lobby["lobbyCount"] as Long == 1L){
            return lobby
        }*/
        lobby["lobbyCount"] = lobby["lobbyCount"] as Long - 1
        val userMap = activePartialUser.asMap()
        (lobby["lobbyPlayers"] as ArrayList<MutableMap<String, Any>>).remove(userMap)
     /*   if (lobby["lobbyLeader"] as String == activePartialUser.uid) { //leader?
            lobby["lobbyLeader"] = (players)[0]["uid"] as String //use the next player as the new leader
        }*/
        return lobby

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


    /**
     * Check the condition to see if the position of the player is close to the position of the lobby
     * @param gp : position of the player
     * @param positionMap : position fo the lobby as given by the database
     * @return true if the position of the player is close enough to the lobby
     */
    private fun positionCondition(gp : GeoPoint , positionMap : MutableMap<String,Any> ) : Boolean{
        return CoordinatesUtil.distance(gp, GeoPoint((positionMap["latitude"] as Double)  , (positionMap["longitude"] as Double) ) ) <= Constants.GAME_AREA_RADIUS
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

    /**
     * Transition to the game screen : leaving the match making
     */
    private fun gameScreenTransition(){
        val intent = Intent(activity.applicationContext, GameVersusViewActivity::class.java)

        db.update("GameInstance/Game" + currentLobbyId + "/id:" + activePartialUser.uid + "/finish", 0)

        intent.putExtra("gid",this.currentLobbyId)
        intent.putExtra("uid",activePartialUser.uid)
        intent.putExtra("nbPlayer",nbPlayer)
        intent.putExtra("gameMode",gamemode)

        activity.startActivity(intent)
    }


}