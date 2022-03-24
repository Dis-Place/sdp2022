package com.github.displace.sdp2022.matchMaking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.displace.sdp2022.GameVersusViewActivity
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.news.NewsActivity
import com.google.firebase.database.*


class MatchMakingActivity : AppCompatActivity() {

    //Database
    private val db = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/") as RealTimeDatabase
    private var currentLobbyNumber = "L_-1"
    private val gamemode = "Versus"
    private val map = "Map1"
   // public var prefix = ""
    private var isLeader = false
    private var pList : ArrayList<Int> = ArrayList()
    private var myId : Long = -1


    private val counterListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            db.referenceGet("MM/$gamemode/$map/$currentLobbyNumber","p_list").addOnSuccessListener { ls ->
                val temp = ls.value as ArrayList<Int>?
                if(temp != null) {
                    pList = temp
                    updateUI()
                }
            }
            val counter = snapshot.value as Long?
            if(counter != null && isLeader){
                db.referenceGet("MM/$gamemode/$map/$currentLobbyNumber","max_p").addOnSuccessListener { m ->
                    val max = m.value as Long?
                    if(max != null && counter >=max){
                        //copy lobby info to game instance - after that is finished : update launch
                        db.update("MM/$gamemode/$map/$currentLobbyNumber","launch",true) //set launch for everyone

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

            db.referenceGet("MM/$gamemode/$map/$currentLobbyNumber","launch").addOnSuccessListener { l ->
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

        //   db.db.getReference("MM/$gamemode/$map").keepSynced(true)

        //single user search : no groups of friends yet
        db.referenceGet("MM/$gamemode/$map","first").addOnSuccessListener { f ->
            val first = f.value
            if(first != null) {
                indexedSearch(first as Long)
            }else{
                indexedSearch(0)
            }
        }
    }


    private fun indexedSearch(idx : Long){

        val lobby = "L_$idx"
        db.referenceGet("MM/$gamemode/$map/$lobby","p_count").addOnSuccessListener { c ->
            val count = c.value
            if(count == null){
                db.referenceGet("MM/$gamemode/$map","last").addOnSuccessListener { last ->
                    val p = last.value as Long?
                    if(p != null){
                        if(idx > p){
                            createLobby(idx)
                        }else{
                            indexedSearch(idx+1)
                        }
                    }else{
                        createLobby(idx)
                    }
                }
            }else{
                db.referenceGet("MM/$gamemode/$map/$lobby","max_p").addOnSuccessListener { m ->
                    val max = m.value
                    db.referenceGet("MM/$gamemode/$map/$lobby", "launch").addOnSuccessListener { l ->
                        val launch = l.value as Boolean?
                        if ( launch == null || max == null || launch== true || count as Long >= (max as Long)) { //this lobby is full or launching : continue searching
                            indexedSearch(idx + 1)    //no need for transaction for this kind of search
                        } else {
                            //use transaction to insert yourself : modify counter AND player list
                            db.getDbReference("MM/$gamemode/$map/$lobby")
                                    .runTransaction(object : Transaction.Handler {
                                        override fun doTransaction(currentData: MutableData): Transaction.Result {
                                            //need to recheck values here : just in case
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
                                            currentLobbyNumber = "$lobby"
                                            isLeader = false
                                            myId = 1

                                            db.getDbReference("MM/$gamemode/$map/$currentLobbyNumber/p_count")
                                                    .addValueEventListener(counterListener)
                                            db.getDbReference("MM/$gamemode/$map/$currentLobbyNumber/launch")
                                                    .addValueEventListener(launchListener)
                                            db.getDbReference("MM/$gamemode/$map/$currentLobbyNumber/leader")
                                                    .addValueEventListener(leaderListener)

                                        }
                                    })

                        }
                    }
                }
            }
        }

    }


    private fun createLobby(idx : Long ){
        val ls : List<Int> = listOf(0)
        val lobby = "L_$idx"

        db.referenceGet("MM/$gamemode/$map","last").addOnSuccessListener { lobbies ->
            val last = lobbies.value
            if(last != null){
                if( idx > last as Long){
                    val updates: MutableMap<String, Any> = HashMap()
                    updates["MM/$gamemode/$map/last"] = ServerValue.increment(idx-last)
                    db.getDbReference().updateChildren(updates)
                }
            }else{
                val updates: MutableMap<String, Any> = HashMap()
                updates["MM/$gamemode/$map/last"] = ServerValue.increment(idx)
                db.getDbReference().updateChildren(updates)
            }

            db.referenceGet("MM/$gamemode/$map","first").addOnSuccessListener {    first ->
                val f = first.value
                if(f != null){
                    if( idx < f as Long){
                        val updates: MutableMap<String, Any> = HashMap()
                        updates["MM/$gamemode/$map/last"] = ServerValue.increment(idx-f)
                        db.getDbReference().updateChildren(updates)
                    }
                }else{
                    val updates: MutableMap<String, Any> = HashMap()
                    updates["MM/$gamemode/$map/first"] = ServerValue.increment(idx)
                    db.getDbReference().updateChildren(updates)
                }
            }

            db.insert("MM/$gamemode/$map/$lobby", "p_count", 1)
            db.insert("MM/$gamemode/$map/$lobby", "max_p", 2)
            db.insert("MM/$gamemode/$map/$lobby", "p_list", ls)
            db.insert("MM/$gamemode/$map/$lobby", "launch", false)
            db.insert("MM/$gamemode/$map/$lobby", "leader", 0)
            currentLobbyNumber = "$lobby"
            isLeader = true
            myId = 0
            db.getDbReference("MM/$gamemode/$map/$currentLobbyNumber/p_count").addValueEventListener(counterListener)
            db.getDbReference("MM/$gamemode/$map/$currentLobbyNumber/launch").addValueEventListener(launchListener)
            db.getDbReference("MM/$gamemode/$map/$currentLobbyNumber/leader").addValueEventListener(leaderListener)
        }

    }

    private fun updateUI(){
        //not for now
    }

    private fun gameScreenTransition(){
        //this is called when a launch occurs
        //add yourself to game instance list (TRANSACTION)

        //if you are the leader
        //if you are alone in the lobby : delete it
        //if its the first lobby : first = first + 1
        //if its the last lobby : last = last - 1
        //if not alone
        //set next player in the list as the new leader
        //delete yourself from players list (TRANSACTION) : currentLobby = "-1"
        //if you are not the leader
        //delete yourself from players list (TRANSACTION) : currentLobby = "-1"
        val intent = Intent(this, GameVersusViewActivity::class.java)
        startActivity(intent)
    }

    //when leaving the MM screen
    private fun leaveMM(){

        db.getDbReference("MM/$gamemode/$map")
                .runTransaction(object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        val p = currentData.value as MutableMap<String, Any>? ?: return Transaction.success(currentData)
                        val currLobby = p[currentLobbyNumber] as MutableMap<String,Any>? ?: return Transaction.success(currentData)

                        if(!isLeader){
                            val ls = currLobby["p_list"] as ArrayList<Long>? ?: return Transaction.success(currentData)
                            ls.remove(myId)
                        }else{
                            val count = currLobby["p_count"] as Long? ?: return Transaction.success(currentData)
                            if(count == 1L){
                                val first = p["first"] as Long
                                val last = p["last"] as Long
                                if(first != last) {
                                    if (first.toString() == currentLobbyNumber) {
                                        p["first"] = first + 1L
                                    }else if (last.toString() == currentLobbyNumber) {
                                        p["last"] = first - 1L
                                    }
                                }
                                //delete the lobby here
                            }else{
                                val ls = currLobby["p_list"] as ArrayList<Long>? ?: return Transaction.success(currentData)
                                ls.remove(myId)
                                currLobby["p_list"] = ls
                                currLobby["leader"] = ls[0]
                                //assign a new leader
                            }
                        }
                        p[currentLobbyNumber] = currLobby
                        currentData.value = p
                        return Transaction.success(currentData)
                    }

                    override fun onComplete(
                            error: DatabaseError?,
                            committed: Boolean,
                            currentData: DataSnapshot?
                    ) {
                      //  currentLobbyNumber = "L_-1"
                      //  myId = -1
                     //   pList = ArrayList()
                     //   isLeader = false
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


}