package com.github.displace.sdp2022.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.profile.achievements.AchViewAdapter
import com.github.displace.sdp2022.profile.friendInvites.AddFriendActivity
import com.github.displace.sdp2022.profile.friendInvites.FriendRequestViewAdapter
import com.github.displace.sdp2022.profile.friendInvites.InviteWithId
import com.github.displace.sdp2022.profile.friends.FriendViewAdapter
import com.github.displace.sdp2022.profile.history.HistoryViewAdapter
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.profile.messages.MsgViewAdapter
import com.github.displace.sdp2022.profile.settings.AccountSettingsActivity
import com.github.displace.sdp2022.profile.statistics.StatViewAdapter
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProfileActivity : AppCompatActivity() {

    private val db: RealTimeDatabase = RealTimeDatabase().instantiate(
        "https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",
        false
    ) as RealTimeDatabase

    private lateinit var msgLs: ArrayList<HashMap<String, Any>>

    private lateinit var activePartialUser: PartialUser

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
        var activePartialUser = PartialUser("defaultName", "dummy_id")
        if (activeUser != null) {
            activePartialUser = activeUser.getPartialUser()
        }
        db.referenceGet("CompleteUsers/" + activePartialUser.uid, "MessageHistory")
            .addOnSuccessListener { msg ->
                val ls = msg.value as ArrayList<HashMap<String, Any>>?
                messageList(ls)
            }

        db.getDbReference("CompleteUsers/" + activePartialUser.uid + "/MessageHistory")
            .addValueEventListener(messageListener())


        /*Set the default at the start*/
        activityStart()


        val rootRef = FirebaseDatabase.
        getInstance("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app").reference
        val currentUser = activePartialUser // activeUser?.getPartialUser() ?: PartialUser("dummy", "dummy")

        val recyclerview = findViewById<RecyclerView>(R.id.friendRequestRecyclerView)

        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = FriendRequestViewAdapter( mutableListOf<InviteWithId>())

        RecieveFriendRequests.recieveRequests(rootRef, currentUser)
            .observe(this,  Observer{
                val adapter = FriendRequestViewAdapter(it)

                // Setting the Adapter with the recyclerview
                recyclerview.adapter = adapter

            })


    }

    override fun onResume() {
        super.onResume()
        val app = applicationContext as MyApplication
        val activeUser = app.getActiveUser()
        findViewById<TextView>(R.id.profileUsername).text =
            activeUser?.getPartialUser()?.username ?: "defaultNotLoggedIn"
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
        val app = applicationContext as MyApplication
        val activeUser = app.getActiveUser()

        if (activeUser != null) {
            val intent = Intent(this, AccountSettingsActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "You're in guest mode !", Toast.LENGTH_LONG).show()
        }

    }

    private fun messageListener() = object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            val ls = snapshot.value as ArrayList<HashMap<String, Any>>?
            messageList(ls)
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }

    private fun messageList(ls: ArrayList<HashMap<String, Any>>?) {
        val messageRecyclerView = findViewById<RecyclerView>(R.id.recyclerMsg)

        var list = mutableListOf<Message>()
        if(ls != null){
            list = (applicationContext as MyApplication).getMessageHandler().getListOfMessages(ls)
        }
        val messageAdapter = MsgViewAdapter(
            applicationContext,
            list,
            0
        )
        messageRecyclerView.adapter = messageAdapter
        messageRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
    }

    @Suppress("UNUSED_PARAMETER")
    fun addFriendButton(view: View) {
        startActivity(Intent(this, AddFriendActivity::class.java))
    }


}