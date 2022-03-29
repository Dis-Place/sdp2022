@file:Suppress("UNCHECKED_CAST")

package com.github.displace.sdp2022.matchMaking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.GameVersusViewActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.profile.friends.FriendViewAdapter
import com.google.firebase.database.*


@Suppress("UNUSED_PARAMETER")
class MatchMakingActivity : AppCompatActivity() {

    //Database
    private var db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase
    private var currentLobbyNumber = "L_-1"
    private val gamemode = "Versus"
    private val map = "Map1"
    private var isLeader = false
    private var pList : ArrayList<Int> = ArrayList()
    private var myId : Long = -1
    private var lobbyType : String = "private"


    private val counterListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            db.referenceGet("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber","p_list").addOnSuccessListener { ls ->
                val temp = ls.value as ArrayList<Int>?
                if(temp != null) {
                    pList = temp
                    updateUI()
                }
            }
            val counter = snapshot.value as Long?
            if(counter != null && isLeader){
                db.referenceGet("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber","max_p").addOnSuccessListener { m ->
                    val max = m.value as Long?
                    if(max != null && counter >= max){
                        db.update("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber","launch",true) //set launch for everyone
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("tag","loadCounter Failed" )
        }

    }

    private val launchListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            //current leader transitions to game screen : signals the start of the launch
            val launch = snapshot.value as Boolean?
            if(launch != null && launch == true && isLeader){
                //transition to game screen : fetch correct game Instance
                gameScreenTransition()
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("tag","loadLaunch Failed" )
        }

    }

    private val leaderListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            db.referenceGet("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber","launch").addOnSuccessListener { l ->
                val leader = snapshot.value as Long?
                if(leader != null && leader == myId){
                    isLeader = true
                }

                val launch = l.value as Boolean?
                if(launch != null && launch == true && isLeader){
                    //transition to game screen : fetch the correct gameInstance
                    gameScreenTransition()
                }
            }

        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("tag","loadLeader Failed" )
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_making)

        uiToSetup()

        val app = applicationContext as MyApplication
        val dbAccess = app.getProfileDb()

        /* Friends in private lobby */
        val friendRecyclerView = findViewById<RecyclerView>(R.id.friendsMMRecycler)
        val friendAdapter = FriendViewAdapter(
            applicationContext,
            dbAccess.getFriendsList(3, app.getActiveUser().ID),
            dbAccess, 1
        )
        friendRecyclerView.adapter = friendAdapter
        friendRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
    }


    private fun indexedSearch(idx : Long){

        val lobby = "L_$idx"
        db.referenceGet("MM/$gamemode/$map/$lobbyType/$lobby","p_count").addOnSuccessListener { c ->
            val count = c.value
            if(count == null){
                db.referenceGet("MM/$gamemode/$lobbyType/$map","last").addOnSuccessListener { last ->
                    val p = last.value as Long?
                    if(p != null){
                        if(idx > p){
                            createPublicLobby(idx)
                        }else{
                            indexedSearch(idx+1)
                        }
                    }else{
                        createPublicLobby(idx)
                    }
                }
            }else{
                db.referenceGet("MM/$gamemode/$map/$lobbyType/$lobby","max_p").addOnSuccessListener { m ->
                    val max = m.value
                    db.referenceGet("MM/$gamemode/$map/$lobbyType/$lobby", "launch").addOnSuccessListener { l ->
                        val launch = l.value as Boolean?
                        if ( launch == null || max == null || launch== true || count as Long >= (max as Long)) { //this lobby is full or launching : continue searching
                            indexedSearch(idx + 1)    //no need for transaction for this kind of search
                        } else {
                            //use transaction to insert yourself : modify counter AND player list
                            db.getDbReference("MM/$gamemode/$map/$lobbyType/$lobby")
                                    .runTransaction(object : Transaction.Handler {
                                        override fun doTransaction(currentData: MutableData): Transaction.Result {
                                            val p = currentData.value as MutableMap<String, Any>?
                                                    ?: return Transaction.success(currentData)
                                            p["p_count"] = p["p_count"] as Long + 1
                                            val ls = (p["p_list"] as ArrayList<Long>?) ?: return Transaction.success(currentData)
                                            ls.add(1)
                                            p["p_list"] = ls
                                            currentData.value = p
                                            return Transaction.success(currentData)
                                        }

                                        override fun onComplete(
                                                error: DatabaseError?,
                                                committed: Boolean,
                                                currentData: DataSnapshot?
                                        ) {

                                            currentLobbyNumber = lobby
                                            isLeader = false
                                            myId = 1
                                            setupListeners()
                  //                          db.getDbReference("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber/p_count")
                    //                                .addValueEventListener(counterListener)
                      //                      db.getDbReference("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber/launch")
                        //                            .addValueEventListener(launchListener)
                          //                  db.getDbReference("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber/leader")
                            //                        .addValueEventListener(leaderListener)
                                            uiToSearch()
                                        }
                                    })

                        }
                    }
                }
            }
        }

    }


    private fun createPublicLobby(id : Long ){
        isLeader = true
        myId = 0
        val lobby = "L_$id"


        db.referenceGet("MM/$gamemode/$map/$lobbyType","last").addOnSuccessListener { lastLobby ->
            val last = lastLobby.value
            val updates: MutableMap<String, Any> = HashMap()
            var inc : Long = 0
            if(last != null){
                if( id > last as Long){
                    inc = id-last
                }
            }else{
                inc = id
            }
            updates["MM/$gamemode/$map/$lobbyType/last"] = ServerValue.increment(inc)
//            db.getDbReference("").updateChildren(updates)

            db.referenceGet("MM/$gamemode/$map*$lobbyType","first").addOnSuccessListener {    first ->
                val f = first.value
                if(f != null){
                    if( id < f as Long){
        //                val updates: MutableMap<String, Any> = HashMap()
                        updates["MM/$gamemode/$map/$lobbyType/last"] = ServerValue.increment(id-f)
   //                     db.getDbReference("").updateChildren(updates)
                    }
                }else{
          //          val updates: MutableMap<String, Any> = HashMap()
                    updates["MM/$gamemode/$map/$lobbyType/first"] = ServerValue.increment(id)
//                    db.getDbReference("").updateChildren(updates)
                }

                db.getDbReference("").updateChildren(updates)

            }



            currentLobbyNumber = lobby
            setupLobby()
            /*
            db.insert("MM/$gamemode/$map/$lobbyType/$lobby", "p_count", 1)
            db.insert("MM/$gamemode/$map/$lobbyType/$lobby", "max_p", 2)
            db.insert("MM/$gamemode/$map/$lobbyType/$lobby", "p_list", ls)
            db.insert("MM/$gamemode/$map/$lobbyType/$lobby", "launch", false)
            db.insert("MM/$gamemode/$map/$lobbyType/$lobby", "leader", myId)
            */
            //currentLobbyNumber = lobby


        //    db.getDbReference("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber/p_count").addValueEventListener(counterListener)
        //    db.getDbReference("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber/launch").addValueEventListener(launchListener)
        //    db.getDbReference("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber/leader").addValueEventListener(leaderListener)
            setupListeners()
            uiToSearch()
        }

    }

    private fun updateUI(){

        //REPLACE WITH PARTIAL USERS ONCE IT IS IMPLEMENTED

        val app = applicationContext as MyApplication
        val dbAccess = app.getProfileDb()
        val friendRecyclerView = findViewById<RecyclerView>(R.id.playersRecycler)
        val friendAdapter = FriendViewAdapter(
            applicationContext,
            dbAccess.getFriendsList(pList.size, app.getActiveUser().ID),
            dbAccess, 2
        )
        friendRecyclerView.adapter = friendAdapter
        friendRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun gameScreenTransition(){
        val intent = Intent(this, GameVersusViewActivity::class.java)
        startActivity(intent)
    }

    //when leaving the MM screen
    private fun leaveMM(){

        db.getDbReference("MM/$gamemode/$map/$lobbyType")
                .runTransaction(object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        val p = currentData.value as MutableMap<String, Any>? ?: return Transaction.success(currentData)
                        val currLobby = p[currentLobbyNumber] as MutableMap<String,Any>? ?: return Transaction.success(currentData)

                        if(!isLeader){
                            val ls = currLobby["p_list"] as ArrayList<Long>? ?: return Transaction.success(currentData)
                            ls.remove(myId)
                            val count = currLobby["p_count"] as Long? ?: return Transaction.success(currentData)
                            currLobby["p_count"] = count-1

                            p[currentLobbyNumber] = currLobby

                        }else{
                            val count = currLobby["p_count"] as Long? ?: return Transaction.success(currentData)
                            if(count == 1L){
                                if(lobbyType == "public") {
                                    val first = p["first"] as Long
                                    val last = p["last"] as Long
                                    if (first != last) {
                                        if ("L_$first" == currentLobbyNumber) {
                                            p["first"] = first + 1L
                                        } else if ("L_$last" == currentLobbyNumber) {
                                            p["last"] = last - 1L
                                        }
                                    }
                                }
                                p.remove(currentLobbyNumber)
                                //delete the lobby here
                            }else{
                                val ls = currLobby["p_list"] as ArrayList<Long>? ?: return Transaction.success(currentData)
                                ls.remove(myId)
                                currLobby["p_list"] = ls
                                //assign a new leader
                                currLobby["leader"] = ls[0]
                            //    val count = currLobby["p_count"] as Long? ?: return Transaction.success(currentData)
                                currLobby["p_count"] = count-1

                                p[currentLobbyNumber] = currLobby
                            }
                        }
                        currentData.value = p
                        return Transaction.success(currentData)
                    }

                    override fun onComplete(
                            error: DatabaseError?,
                            committed: Boolean,
                            currentData: DataSnapshot?
                    ) {

                    }
                })
    }

    override fun onDestroy() {
        leaveMM()
        super.onDestroy()
    }

    override fun onPause() {
        leaveMM()
        super.onPause()
    }


    fun onCancelButton(v : View){
        leaveMM()
        uiToSetup()
    }

    fun onPublicLobbySearchButton(v : View){
        lobbyType = "public"
        db.referenceGet("MM/$gamemode/$map/$lobbyType","first").addOnSuccessListener { f ->
            val first = f.value
            if(first != null) {
                indexedSearch(first as Long)
            }else{
                indexedSearch(0)
            }
        }
    }


    fun onPrivateLobbyJoinButton(v : View){
        lobbyType = "private"
        //join private lobby

        val id = findViewById<EditText>(R.id.lobbyIdInsert).text
        val lobby = "L_$id"
        db.referenceGet("MM/$gamemode/$map/$lobbyType", lobby).addOnSuccessListener { i ->
            val lobbyId = i.value as HashMap<String,Any>?
            if(lobbyId != null) {

                db.getDbReference("MM/$gamemode/$map/$lobbyType/$lobby")
                    .runTransaction(object : Transaction.Handler {
                        override fun doTransaction(currentData: MutableData): Transaction.Result {

                            val p = currentData.value as MutableMap<String, Any>? ?: return Transaction.success(currentData)
                            p["p_count"] = p["p_count"] as Long + 1
                            val ls = (p["p_list"] as ArrayList<Long>?) ?: return Transaction.success(currentData)
                            ls.add(1)
                            p["p_list"] = ls
                            currentData.value = p
                            return Transaction.success(currentData)

                        }

                        override fun onComplete(
                            error: DatabaseError?,
                            committed: Boolean,
                            currentData: DataSnapshot?
                        ) {

                            uiToSearch()
                            currentLobbyNumber = lobby
                            isLeader = false
                            myId = 1

                            setupListeners()
      //                      db.getDbReference("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber/p_count")
        //                        .addValueEventListener(counterListener)
          //                  db.getDbReference("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber/launch")
            //                    .addValueEventListener(launchListener)
              //              db.getDbReference("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber/leader")
                //                .addValueEventListener(leaderListener)

                        }

                    })

            }else{
                //LOBBY NOT FOUND
                showToastText("Lobby not found")
            }
        }

    }

    fun onPrivateLobbyCreateButton(v : View){
        lobbyType = "private"
        //create private lobby


        val id = findViewById<EditText>(R.id.lobbyIdInsert).text
        if(id.isEmpty()){
            showToastText("Lobby ID can not be empty")
        }
        val lobby = "L_$id"

        db.referenceGet("MM/$gamemode/$map/$lobbyType", lobby).addOnSuccessListener { i ->
            val lobbyId = i.value as HashMap<String,Any>?
            if(lobbyId == null){
                myId = 0
                isLeader = true

                //val ls : List<Long> = listOf(myId)

                currentLobbyNumber = lobby
                setupLobby()
/*
                db.insert("MM/$gamemode/$map/$lobbyType/$lobby", "p_count", 1)
                db.insert("MM/$gamemode/$map/$lobbyType/$lobby", "max_p", 2)
                db.insert("MM/$gamemode/$map/$lobbyType/$lobby", "p_list", ls)
                db.insert("MM/$gamemode/$map/$lobbyType/$lobby", "launch", false)
                db.insert("MM/$gamemode/$map/$lobbyType/$lobby", "leader", myId)
*/
                //currentLobbyNumber = lobby

//                db.getDbReference("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber/p_count").addValueEventListener(counterListener)
  //              db.getDbReference("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber/launch").addValueEventListener(launchListener)
    //            db.getDbReference("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber/leader").addValueEventListener(leaderListener)
                setupListeners()
                uiToSearch()

            }else{
                //LOBBY ALREADY EXISTS : toast message later
                showToastText("Lobby ID already exists")
            }

        }

    }

    private fun uiToSetup(){
        findViewById<Group>(R.id.setupGroup).visibility = View.VISIBLE
        findViewById<Group>(R.id.waitGroup).visibility = View.INVISIBLE
        if(lobbyType == "private" ){
            findViewById<Group>(R.id.privateGroup).visibility = View.INVISIBLE
        }
    }

    private fun uiToSearch(){
        findViewById<Group>(R.id.setupGroup).visibility = View.INVISIBLE
        findViewById<Group>(R.id.waitGroup).visibility = View.VISIBLE
        if(lobbyType == "private" ){
            findViewById<Group>(R.id.privateGroup).visibility = View.VISIBLE
            findViewById<TextView>(R.id.lobbyIdWaitShowing).text = currentLobbyNumber.subSequence(
                IntRange(2,currentLobbyNumber.length-1)
            )
        }
    }

    private fun showToastText(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }

    private fun setupListeners(){
        db.getDbReference("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber/p_count").addValueEventListener(counterListener)
        db.getDbReference("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber/launch").addValueEventListener(launchListener)
        db.getDbReference("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber/leader").addValueEventListener(leaderListener)

    }

    private fun setupLobby(){
        val ls : List<Long> = listOf(myId)
        db.insert("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber", "p_count", 1)
        db.insert("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber", "max_p", 2)
        db.insert("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber", "p_list", ls)
        db.insert("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber", "launch", false)
        db.insert("MM/$gamemode/$map/$lobbyType/$currentLobbyNumber", "leader", myId)
    }

}