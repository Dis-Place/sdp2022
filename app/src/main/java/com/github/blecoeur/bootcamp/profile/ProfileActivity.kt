package com.github.blecoeur.bootcamp.profile

import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.blecoeur.bootcamp.R
import com.github.blecoeur.bootcamp.profile.achievements.AchViewAdapter
import com.github.blecoeur.bootcamp.profile.achievements.DBAchAdapter
import com.github.blecoeur.bootcamp.profile.friends.DBFriendAdapter
import com.github.blecoeur.bootcamp.profile.friends.FriendViewAdapter
import com.github.blecoeur.bootcamp.profile.statistics.DBStatAdapter
import com.github.blecoeur.bootcamp.profile.statistics.StatViewAdapter


class ProfileActivity : AppCompatActivity() {

    private val dbAchAccess : DBAchAdapter = DBAchAdapter()
    private val dbStatsAccess : DBStatAdapter = DBStatAdapter()
    private val dbHistAccess : DBAchAdapter = DBAchAdapter()
    private val dbMsgsAccess : DBAchAdapter = DBAchAdapter()
    private val dbFriendsAccess : DBFriendAdapter = DBFriendAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        /* Achievements */
        val achRecyclerView = findViewById<RecyclerView>(R.id.recyclerAch)
        val achAdapter = AchViewAdapter( applicationContext , dbAchAccess.getAchList(3))
        achRecyclerView.adapter = achAdapter
        achRecyclerView.layoutManager = LinearLayoutManager(applicationContext);

        /* Statistics */
        val statRecyclerView = findViewById<RecyclerView>(R.id.recyclerStats)
        val statAdapter = StatViewAdapter( applicationContext , dbStatsAccess.getStatList(3))
        statRecyclerView.adapter = statAdapter
        statRecyclerView.layoutManager = LinearLayoutManager(applicationContext);

        /* Friends */
        val friendRecyclerView = findViewById<RecyclerView>(R.id.recyclerFriend)
        val friendAdapter = FriendViewAdapter( applicationContext , dbFriendsAccess.getFriendList(3), dbFriendsAccess )
        friendRecyclerView.adapter = friendAdapter
        friendRecyclerView.layoutManager = LinearLayoutManager(applicationContext);

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


}