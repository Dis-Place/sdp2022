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
import com.github.displace.sdp2022.profile.achievements.AchViewAdapter
import com.github.displace.sdp2022.profile.friends.FriendViewAdapter
import com.github.displace.sdp2022.profile.history.HistoryViewAdapter
import com.github.displace.sdp2022.profile.messages.MsgViewAdapter
import com.github.displace.sdp2022.profile.settings.AccountSettingsActivity
import com.github.displace.sdp2022.profile.statistics.StatViewAdapter
import com.github.displace.sdp2022.R


class ProfileActivity : AppCompatActivity() {

    private lateinit var dbAccess: ProfileDbConnection


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        /* Active user Information */
        val app = applicationContext as MyApplication
        dbAccess = app.getProfileDb()
        findViewById<TextView>(R.id.profileUsername).text = app.getActiveUser().name

        /* Achievements */
        val achRecyclerView = findViewById<RecyclerView>(R.id.recyclerAch)
        val achAdapter =
            AchViewAdapter(applicationContext, dbAccess.getAchList(3, app.getActiveUser().ID))
        achRecyclerView.adapter = achAdapter
        achRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        /* Statistics */
        val statRecyclerView = findViewById<RecyclerView>(R.id.recyclerStats)
        val statAdapter =
            StatViewAdapter(applicationContext, dbAccess.getStatsList(3, app.getActiveUser().ID))
        statRecyclerView.adapter = statAdapter
        statRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        /* Games History */
        val historyRecyclerView = findViewById<RecyclerView>(R.id.recyclerHist)
        val historyAdapter =
            HistoryViewAdapter(applicationContext, dbAccess.getHistList(3, app.getActiveUser().ID))
        historyRecyclerView.adapter = historyAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        /* Friends */
        val friendRecyclerView = findViewById<RecyclerView>(R.id.recyclerFriend)
        val friendAdapter = FriendViewAdapter(
            applicationContext,
            dbAccess.getFriendsList(3, app.getActiveUser().ID),
            dbAccess
        )
        friendRecyclerView.adapter = friendAdapter
        friendRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        /* Messages */
        val messageRecyclerView = findViewById<RecyclerView>(R.id.recyclerMsg)
        val messageAdapter = MsgViewAdapter(
            applicationContext,
            dbAccess.getMsgList(3, app.getActiveUser().ID),
            dbAccess
        )
        messageRecyclerView.adapter = messageAdapter
        messageRecyclerView.layoutManager = LinearLayoutManager(applicationContext)


        /*Set the default at the start*/
        activityStart()

    }

    private fun activityStart() {
        findViewById<ScrollView>(R.id.ProfileScroll).visibility = View.VISIBLE
        findViewById<ScrollView>(R.id.InboxScroll).visibility = View.GONE
        findViewById<ScrollView>(R.id.FriendsScroll).visibility = View.GONE
    }

    fun profileButton(view: View) {
        activityStart()
    }

    fun inboxButton(view: View) {
        findViewById<ScrollView>(R.id.ProfileScroll).visibility = View.GONE
        findViewById<ScrollView>(R.id.InboxScroll).visibility = View.VISIBLE
        findViewById<ScrollView>(R.id.FriendsScroll).visibility = View.GONE
    }

    fun friendsButton(view: View) {
        findViewById<ScrollView>(R.id.ProfileScroll).visibility = View.GONE
        findViewById<ScrollView>(R.id.InboxScroll).visibility = View.GONE
        findViewById<ScrollView>(R.id.FriendsScroll).visibility = View.VISIBLE
    }

    fun settingsButton(view: View) {
        val intent = Intent(this, AccountSettingsActivity::class.java)
        startActivity(intent)
    }


    /*
    TODO IN THIS ACTIVITY
    - See someone else's profile : needs to go back to active user profile (little button to go back) : only see the PROFILE part of it
     */
}
