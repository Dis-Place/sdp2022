package com.github.displace.sdp2022.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.profile.achievements.AchViewAdapter
import com.github.displace.sdp2022.profile.friends.FriendViewAdapter
import com.github.displace.sdp2022.profile.history.HistoryViewAdapter
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.profile.messages.MsgViewAdapter
import com.github.displace.sdp2022.profile.settings.AccountSettingsActivity
import com.github.displace.sdp2022.profile.statistics.StatViewAdapter
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class ProfileActivity : AppCompatActivity() {

    private val db : RealTimeDatabase = RealTimeDatabase().instantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        /* Active user Information */
        val app = applicationContext as MyApplication
        val activeUser = app.getActiveUser()
        findViewById<TextView>(R.id.profileUsername).text =
            activeUser?.getPartialUser()?.username ?: "defaultNotLoggedIn"

        /* Achievements */
        val achRecyclerView = findViewById<RecyclerView>(R.id.recyclerAch)
        val achs = activeUser?.getAchievements() ?: mutableListOf()

        val achAdapter =
            AchViewAdapter(applicationContext, achs)
        achRecyclerView.adapter = achAdapter
        achRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        /* Statistics */
        val statRecyclerView = findViewById<RecyclerView>(R.id.recyclerStats)

        val stats = activeUser?.getStats() ?: mutableListOf()

        val statAdapter =
            StatViewAdapter(applicationContext, stats)
        statRecyclerView.adapter = statAdapter
        statRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        /* Games History */
        val historyRecyclerView = findViewById<RecyclerView>(R.id.recyclerHist)

        val hist = activeUser?.getGameHistory() ?: mutableListOf()

        val historyAdapter =
            HistoryViewAdapter(applicationContext, hist)
        historyRecyclerView.adapter = historyAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        /* Friends */
        val friendRecyclerView = findViewById<RecyclerView>(R.id.recyclerFriend)

        val friends = activeUser?.getFriendsList() ?: mutableListOf()
        val friendAdapter = FriendViewAdapter(
            applicationContext,
            friends,
            0
        )
        friendRecyclerView.adapter = friendAdapter
        friendRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        /* Messages */
        var activePartialUser = PartialUser("defaultName","dummy_id")
        if(activeUser != null){
            activePartialUser = activeUser.getPartialUser()
        }
        db.referenceGet( "CompleteUsers/" + activePartialUser.uid, "MessageHistory" ).addOnSuccessListener { msg ->
            val ls = msg.value as ArrayList<HashMap<String,Any>>?
            val messageRecyclerView = findViewById<RecyclerView>(R.id.recyclerMsg)
            val list = mutableListOf<Message>()
            if(ls != null){
                for( map in ls ){
                    val sender = map["sender"] as HashMap<String,Any>
                    val m = Message(map["message"] as String,map["date"] as String, PartialUser(sender["username"] as String,sender["uid"] as String) )
                    list.add(m)
                }
            }
            val messageAdapter = MsgViewAdapter(
                applicationContext,
                list
            )
            messageRecyclerView.adapter = messageAdapter
            messageRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        }

        db.getDbReference("CompleteUsers/" + activePartialUser.uid + "/MessageHistory").addValueEventListener(messageListener())


        /*Set the default at the start*/
        activityStart()

    }

    private fun messageListener() = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val ls = snapshot.value as ArrayList<HashMap<String,Any>>?
            val messageRecyclerView = findViewById<RecyclerView>(R.id.recyclerMsg)
            val list = mutableListOf<Message>()
            if(ls != null){
                for( map in ls ){
                    val sender = map["sender"] as HashMap<String,Any>
                    val m = Message(map["message"] as String,map["date"] as String, PartialUser(sender["username"] as String,sender["uid"] as String) )
                    list.add(m)
                }
            }
            val messageAdapter = MsgViewAdapter(
                applicationContext,
                list
            )
            messageRecyclerView.adapter = messageAdapter
            messageRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }

    private fun activityStart() {
        findViewById<ScrollView>(R.id.ProfileScroll).visibility = View.VISIBLE
        findViewById<ScrollView>(R.id.InboxScroll).visibility = View.GONE
        findViewById<ScrollView>(R.id.FriendsScroll).visibility = View.GONE
    }

    @Suppress("UNUSED_PARAMETER")
    fun profileButton(view: View) {
        activityStart()
    }

    @Suppress("UNUSED_PARAMETER")
    fun inboxButton(view: View) {
        findViewById<ScrollView>(R.id.ProfileScroll).visibility = View.GONE
        findViewById<ScrollView>(R.id.InboxScroll).visibility = View.VISIBLE
        findViewById<ScrollView>(R.id.FriendsScroll).visibility = View.GONE
    }

    @Suppress("UNUSED_PARAMETER")
    fun friendsButton(view: View) {
        findViewById<ScrollView>(R.id.ProfileScroll).visibility = View.GONE
        findViewById<ScrollView>(R.id.InboxScroll).visibility = View.GONE
        findViewById<ScrollView>(R.id.FriendsScroll).visibility = View.VISIBLE
    }

    @Suppress("UNUSED_PARAMETER")
    fun settingsButton(view: View) {
        val intent = Intent(this, AccountSettingsActivity::class.java)
        startActivity(intent)
    }

}
