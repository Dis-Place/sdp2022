package com.github.blecoeur.bootcamp.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.blecoeur.bootcamp.MyApplication
import com.github.blecoeur.bootcamp.R
import com.github.blecoeur.bootcamp.profile.achievements.AchViewAdapter
import com.github.blecoeur.bootcamp.profile.friends.FriendViewAdapter
import com.github.blecoeur.bootcamp.profile.history.HistoryViewAdapter
import com.github.blecoeur.bootcamp.profile.messages.MsgViewAdapter
import com.github.blecoeur.bootcamp.profile.settings.AccountSettingsActivity
import com.github.blecoeur.bootcamp.profile.statistics.StatViewAdapter


class ProfileActivity : AppCompatActivity() {

    private lateinit var dbAccess : ProfileDbConnection


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        /* Active user Information */
        val app = applicationContext as MyApplication
        dbAccess = app.getProfileDb()
        Log.d("test","username is : " + dbAccess.getDbId() )


        /* Achievements */
        val achRecyclerView = findViewById<RecyclerView>(R.id.recyclerAch)
        val achAdapter = AchViewAdapter( applicationContext , dbAccess.getAchList(3, "0"))
        achRecyclerView.adapter = achAdapter
        achRecyclerView.layoutManager = LinearLayoutManager(applicationContext);

        /* Statistics */
        val statRecyclerView = findViewById<RecyclerView>(R.id.recyclerStats)
        val statAdapter = StatViewAdapter( applicationContext , dbAccess.getStatsList(3, "0"))
        statRecyclerView.adapter = statAdapter
        statRecyclerView.layoutManager = LinearLayoutManager(applicationContext);

        /* Friends */
        val friendRecyclerView = findViewById<RecyclerView>(R.id.recyclerFriend)
        val friendAdapter = FriendViewAdapter( applicationContext , dbAccess.getFriendsList(3, "0"), dbAccess )
        friendRecyclerView.adapter = friendAdapter
        friendRecyclerView.layoutManager = LinearLayoutManager(applicationContext);

        /* Messages */
        val messageRecyclerView = findViewById<RecyclerView>(R.id.recyclerMsg)
        val messageAdapter = MsgViewAdapter( applicationContext , dbAccess.getMsgList(3, "0"), dbAccess )
        messageRecyclerView.adapter = messageAdapter
        messageRecyclerView.layoutManager = LinearLayoutManager(applicationContext);

        /* Games History */
        val historyRecyclerView = findViewById<RecyclerView>(R.id.recyclerHist)
        val historyAdapter = HistoryViewAdapter( applicationContext , dbAccess.getHistList(3, "0") )
        historyRecyclerView.adapter = historyAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(applicationContext);

        /*Set the default at the start*/
        activityStart()

    }

    private fun activityStart(){
        findViewById<ScrollView>(R.id.profileScroll).visibility = View.VISIBLE
        findViewById<ScrollView>(R.id.InboxScroll).visibility = View.GONE
        findViewById<ScrollView>(R.id.FriendsScroll).visibility = View.GONE
    }

    fun profileButton(view : View){
        activityStart()
    }

    fun inboxButton( view : View) {
        findViewById<ScrollView>(R.id.profileScroll).visibility = View.GONE
        findViewById<ScrollView>(R.id.InboxScroll).visibility = View.VISIBLE
        findViewById<ScrollView>(R.id.FriendsScroll).visibility = View.GONE
    }

    fun friendsButton( view : View) {
        findViewById<ScrollView>(R.id.profileScroll).visibility = View.GONE
        findViewById<ScrollView>(R.id.InboxScroll).visibility = View.GONE
        findViewById<ScrollView>(R.id.FriendsScroll).visibility = View.VISIBLE
    }

    fun settingsButton(view : View){
        val intent = Intent(this, AccountSettingsActivity::class.java)
        startActivity(intent)
    }

    fun msg(){

    }

    /*
    TODO IN THIS ACTIVITY
    - See someone else's profile : needs to go back to active user profile (little button to go back) : only see the PROFILE part of it
    - Make dialog window to write a message to someone
     */
}
