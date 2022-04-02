@file:Suppress("UNCHECKED_CAST")

package com.github.displace.sdp2022.matchMaking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Range
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.*
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.profile.friends.FriendViewAdapter
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.*
import kotlin.random.Random
import kotlin.random.nextUInt


@Suppress("UNUSED_PARAMETER")
class MatchMakingActivity : AppCompatActivity() {

    //Database
    private var db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase
    private var currentLobbyNumber = "L_-1"
    private var currentLobbyId = ""
    private val gamemode = "Versus"
    private val map = "Map2"
    private var isLeader = false
    private var myId : Long = Random.nextLong() //PARTIAL USER : just use the current user Partial User, not the ID
    private val activeUser = PartialUser("active",myId.toString())
    private var lobbyType : String = "private"

    private var lobbyMap : MutableMap<String,Any> = HashMap<String,Any>()


    private val lobbyListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val lobby = snapshot.value as MutableMap<String,Any>? ?: return
            lobbyMap = lobby
            updateUI()
            if(lobbyMap["lobbyCount"] as Long == lobbyMap["lobbyMax"] as Long  ){
                lobbyMap["lobbyLaunch"] = true
                setupLaunchListener()
                if(lobbyMap["lobbyLeader"] as String == activeUser.uid){
                    db.referenceGet("MM/$gamemode/$map/$lobbyType","freeList").addOnSuccessListener { free ->
                        val ls = free.value as ArrayList<String>?
                        if(ls != null){
                            ls.remove(currentLobbyId)
                            db.update("MM/$gamemode/$map/$lobbyType","freeList",ls)
                        }
                        db.delete("MM/$gamemode/$map/$lobbyType/freeLobbies",currentLobbyId)
                        db.update("MM/$gamemode/$map/$lobbyType/launchLobbies",currentLobbyId, lobbyMap)
                    }
                }
            }
        }


        override fun onCancelled(error: DatabaseError) {
        }
    }




    private val launchLobbyListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val lobby = snapshot.value as MutableMap<String,Any>? ?: return
            lobbyMap = lobby
            if(lobbyMap["lobbyLeader"] as String == activeUser.uid ){
                gameScreenTransition()
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("tag","loadLaunch Failed" )
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_making)

   //     db.insert("MM/$gamemode/$map/private","freeList", listOf("head"))
  //      db.insert("MM/$gamemode/$map/private/freeLobbies","freeHead", Lobby("freeHead",0,PartialUser("FREE","FREE")))
  //      db.insert("MM/$gamemode/$map/private/launchLobbies","launchHead", Lobby("launchHead",0,PartialUser("FREE","FREE")))
        uiToSetup()

        val app = applicationContext as MyApplication
        val dbAccess = app.getProfileDb()
        val activeUser = app.getActiveUser()

        /* Friends in private lobby */  //PARTIAL USER : should be the friends list of the active user
        val friendRecyclerView = findViewById<RecyclerView>(R.id.friendsMMRecycler)
        val friends = if(activeUser != null) {
            activeUser.getFriendsList()
        }else {
            mutableListOf()
        }

        val friendAdapter = FriendViewAdapter(
            applicationContext,
            friends,
            dbAccess, 1
        )
        friendRecyclerView.adapter = friendAdapter
        friendRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun publicSearch(){
        var toCreateId = ""
        var toSearchId = ""
        db.getDbReference("MM/$gamemode/$map/$lobbyType/freeList").runTransaction( object : Transaction.Handler{
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                toCreateId = ""
                toSearchId = ""
                val ls = currentData.value as ArrayList<String>? ?: return Transaction.abort()
                if(ls.size == 1){
                    //only the head exists : add a new ID to the list and create a new lobby with that ID
                    val nextId = Random.nextLong().toString()
                    ls.add(nextId)
                    toCreateId = nextId
                    currentData.value = ls
                }else{
                    //more than only the head exists : check the first one out
                    toSearchId = ls[1]
                }
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if(committed){
                    if(toCreateId != ""){   //we need to create a lobby
                        createPublicLobby(toCreateId)
                    }else{  //we need to check out a lobby
                        checkOutLobby(toSearchId)
                    }
                }else{
                    publicSearch()
                }
            }

        })


    }

    private fun checkOutLobby(toSearchId: String) {
        db.getDbReference("MM/$gamemode/$map/$lobbyType/freeLobbies/$toSearchId").runTransaction( object : Transaction.Handler{
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val lobby = currentData.value as MutableMap<String,Any>? ?: return Transaction.abort()
                if(lobby["lobbyCount"] as Long == lobby["lobbyMax"] as Long || lobby["lobbyLaunch"] as Boolean ){
                    return Transaction.abort()
                }
                lobby["lobbyCount"] = lobby["lobbyCount"] as Long + 1
                val ls = lobby["lobbyPlayers"] as ArrayList<MutableMap<String,Any>>
                val userMap = HashMap<String,Any>()
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
                if(committed){
                    currentLobbyId = toSearchId
                    setupLobbyListener()
                    uiToSearch()
                }else{
                    publicSearch()
                }
            }

        })
    }

    private fun createPublicLobby(id : String){
        val lobby = Lobby(id,2,activeUser)
        currentLobbyId = id
        db.update("MM/$gamemode/$map/$lobbyType/freeLobbies", id,lobby)
        setupLobbyListener()
        uiToSearch()
    }

    fun createPrivateLobby(view : View){
        lobbyType = "private"
        val id = findViewById<EditText>(R.id.lobbyIdInsert).text
        if(id.isEmpty()){
            findViewById<TextView>(R.id.errorIdNonEmpty).visibility = View.VISIBLE
            return
        }


        val lobby = Lobby(currentLobbyId,2, activeUser)
        db.referenceGet("MM/$gamemode/$map/$lobbyType","freeList").addOnSuccessListener { free ->
            val ls = free.value as MutableList<String>
            if(ls.contains(id.toString())){
                findViewById<TextView>(R.id.errorIdExists).visibility = View.VISIBLE
            }else{
                currentLobbyId = id.toString()
                ls.add(currentLobbyId)
                db.update("MM/$gamemode/$map/$lobbyType", "freeList", ls)
                db.update("MM/$gamemode/$map/$lobbyType/freeLobbies", currentLobbyId, lobby)
                setupLobbyListener()
                uiToSearch()
            }
        }
    }

    fun joinPrivateLobby(view : View){
        lobbyType = "private"
        val id = findViewById<EditText>(R.id.lobbyIdInsert).text
        if(id.isEmpty()){
            findViewById<TextView>(R.id.errorIdNotFound).visibility = View.VISIBLE
            return
        }
        db.referenceGet("MM/$gamemode/$map/$lobbyType","freeList").addOnSuccessListener { free ->
            val ls = free.value as MutableList<String>
            if(ls.contains(id.toString())){
                checkOutLobby(id.toString())
            }else{
                findViewById<TextView>(R.id.errorIdNotFound).visibility = View.VISIBLE
            }
        }
    }

    private fun updateUI(){

        //REPLACE WITH PARTIAL USERS ONCE IT IS IMPLEMENTED
        //PARTIAL USER : should replace getFriendsList with the list of partials users (the players)
        val app = applicationContext as MyApplication
        val dbAccess = app.getProfileDb()
        val friendRecyclerView = findViewById<RecyclerView>(R.id.playersRecycler)
        val friendAdapter = FriendViewAdapter(
            applicationContext,
            (lobbyMap["lobbyPlayers"] as ArrayList<MutableMap<String,Any>>).map{    p ->
                 PartialUser(p["username"] as String , p["uid"] as String)
            },
            dbAccess, 2
        )
        friendRecyclerView.adapter = friendAdapter
        friendRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun gameScreenTransition(){
        leaveMM(true)
    }

    //when leaving the MM screen
    private fun leaveMM(toGame : Boolean){

        db.getDbReference("MM/$gamemode/$map/$lobbyType").runTransaction( object : Transaction.Handler{
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val lobbyTypeLevel = currentData.value as MutableMap<String,Any>? ?: return Transaction.success(currentData)
                val path = if(toGame){
                    "launchLobbies"
                }else{
                    "freeLobbies"
                }

                val lobbyState = lobbyTypeLevel[path] as MutableMap<String,Any>? ?: return Transaction.success(currentData)
                val lobby = lobbyState[currentLobbyId] as MutableMap<String,Any>? ?: return Transaction.success(currentData)
                if(!toGame && lobby["lobbyLeader"] as String == activeUser.uid){
                    val ls = lobbyTypeLevel["freeList"] as ArrayList<String>? ?: return Transaction.success(currentData)
                    ls.remove(currentLobbyId)
                    lobbyTypeLevel["freeList"] = ls
                }
                if(lobby["lobbyLeader"] as String == activeUser.uid){
                    if(lobby["lobbyCount"] as Long == 1L){
                        lobbyState.remove(currentLobbyId)
                    }else{
                        lobby["lobbyCount"] = lobby["lobbyCount"] as Long -1
                        val userMap = HashMap<String,Any>()
                        userMap["username"] = activeUser.username
                        userMap["uid"] = activeUser.uid
                        (lobby["lobbyPlayers"] as ArrayList<MutableMap<String,Any>>).remove(userMap)
                        lobby["lobbyLeader"] = (lobby["lobbyPlayers"] as ArrayList<MutableMap<String,Any>>)[0]["uid"] as String //PARTIAL USER : should be a list of partial users
                        lobbyState[currentLobbyId] = lobby
                    }
                }else{
                    lobby["lobbyCount"] = lobby["lobbyCount"] as Long -1
                    val userMap = HashMap<String,Any>()
                    userMap["username"] = activeUser.username
                    userMap["uid"] = activeUser.uid
                    (lobby["lobbyPlayers"] as ArrayList<String>).remove(activeUser.uid) //PARTIAL USER : should be a list of partial users
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
               if(toGame){
                   val intent = Intent(applicationContext, GameVersusViewActivity::class.java)
                   startActivity(intent)
               }
            }

        })

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


    fun onCancelButton(v : View){
        leaveMM(false)
        uiToSetup()
    }

    fun onPublicLobbySearchButton(v : View){
        lobbyType = "public"
        publicSearch()
    }

    /*
    Transition to the setup section of the activity
     */
    private fun uiToSetup(){
        findViewById<Group>(R.id.setupGroup).visibility = View.VISIBLE
        findViewById<Group>(R.id.waitGroup).visibility = View.INVISIBLE
        if(lobbyType == "private" ){
            findViewById<Group>(R.id.privateGroup).visibility = View.INVISIBLE
        }
        findViewById<Group>(R.id.errorGroup).visibility = View.INVISIBLE
    }

    /*
    Transition to the search section of the activity
    */
    private fun uiToSearch(){
        findViewById<Group>(R.id.setupGroup).visibility = View.INVISIBLE
        findViewById<Group>(R.id.waitGroup).visibility = View.VISIBLE
        if(lobbyType == "private" ){
            findViewById<Group>(R.id.privateGroup).visibility = View.VISIBLE
            findViewById<TextView>(R.id.lobbyIdWaitShowing).text = currentLobbyId
        }
        findViewById<Group>(R.id.errorGroup).visibility = View.INVISIBLE
    }

    /*
    Setup the listener for the necessary changes
     */
    private fun setupLobbyListener(){
        db.getDbReference("MM/$gamemode/$map/$lobbyType/freeLobbies/$currentLobbyId").addValueEventListener(lobbyListener)
    }

    private fun setupLaunchListener(){
        db.getDbReference("MM/$gamemode/$map/$lobbyType/freeLobbies/$currentLobbyId").removeEventListener(lobbyListener)
        db.getDbReference("MM/$gamemode/$map/$lobbyType/launchLobbies/$currentLobbyId").addValueEventListener(launchLobbyListener)
    }

}


class Lobby(id : String, max_p : Long, leader : PartialUser){
    val lobbyId = id
    val lobbyMax = max_p
    var lobbyCount = 1
    var lobbyLaunch = false
    var lobbyLeader = leader.uid
    var lobbyPlayers = mutableListOf<PartialUser>(leader)    //PARTIAL USER : should be a list of partial users
}