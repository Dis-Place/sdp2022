package com.github.displace.sdp2022.profile.friends

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.profile.ProfileDbConnection
import com.github.displace.sdp2022.profile.achievements.AchViewAdapter
import com.github.displace.sdp2022.profile.history.HistoryViewAdapter
import com.github.displace.sdp2022.profile.statistics.StatViewAdapter
import com.github.displace.sdp2022.R

class FriendProfile : AppCompatActivity() {

    private lateinit var dbAccess: ProfileDbConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_profile)

        /* Active user Information */
        val friendId = intent.getStringExtra("FriendId").toString()
        val friendName = intent.getStringExtra("FriendUsername").toString()

        val app = applicationContext as MyApplication
        dbAccess = app.getProfileDb()
        findViewById<TextView>(R.id.friendUsername).text = friendName

        /* Achievements */
        val achRecyclerView = findViewById<RecyclerView>(R.id.friendRecyclerAch)
        val achAdapter = AchViewAdapter(applicationContext, dbAccess.getAchList(3, friendId))
        achRecyclerView.adapter = achAdapter
        achRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        /* Statistics */
        val statRecyclerView = findViewById<RecyclerView>(R.id.friendRecyclerStats)
        val statAdapter = StatViewAdapter(applicationContext, dbAccess.getStatsList(3, friendId))
        statRecyclerView.adapter = statAdapter
        statRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        /* Games History */
        val historyRecyclerView = findViewById<RecyclerView>(R.id.friendRecyclerHist)
        val historyAdapter =
            HistoryViewAdapter(applicationContext, dbAccess.getHistList(3, friendId))
        historyRecyclerView.adapter = historyAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

    }
}