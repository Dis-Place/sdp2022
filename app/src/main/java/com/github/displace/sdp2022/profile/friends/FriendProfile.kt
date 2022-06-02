package com.github.displace.sdp2022.profile.friends

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.profile.achievements.AchViewAdapter
import com.github.displace.sdp2022.profile.history.HistoryViewAdapter
import com.github.displace.sdp2022.profile.statistics.StatViewAdapter
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.statistics.Statistic


/**
 * Activity to be to see the profile of a friend
 */
class FriendProfile : AppCompatActivity() {

    /**
     * loads and shows informations about friend from database
     * @param savedInstanceState : the saved instance
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_profile)

        /* Active user Information */
        val friendId = intent.getStringExtra("FriendId").toString()
        val friendName = intent.getStringExtra("FriendUsername").toString()

        val app = applicationContext as MyApplication
        val db = DatabaseFactory.getDB(intent)
        findViewById<TextView>(R.id.friendUsername).text = friendName

        db.getThenCall<Map<String, Any>>("CompleteUsers/$friendId/CompleteUser") { cu ->
            if (cu == null) {
                return@getThenCall
            }

            val achList = mutableListOf<Achievement>()
            if (cu["achievements"] != null) {
                for (map in cu["achievements"] as ArrayList<MutableMap<String, Any>>) {
                    achList.add(
                        Achievement(
                            map["name"] as String,
                            map["description"] as String,
                            map["date"] as String
                        )
                    )
                }
            }
            /* Achievements */
            val achRecyclerView = findViewById<RecyclerView>(R.id.friendRecyclerAch)
            val achAdapter = AchViewAdapter(applicationContext, achList)
            achRecyclerView.adapter = achAdapter
            achRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

            val statList = mutableListOf<Statistic>()
            if (cu["stats"] != null){
                for (map in cu["stats"] as List<Map<String, Any>>) {
                    statList.add(Statistic(map["name"] as String, map["value"] as Long))
                }
            }
            /* Statistics */
            val statRecyclerView = findViewById<RecyclerView>(R.id.friendRecyclerStats)
            val statAdapter = StatViewAdapter(applicationContext, statList)
            statRecyclerView.adapter = statAdapter
            statRecyclerView.layoutManager = LinearLayoutManager(applicationContext)


            val histList = mutableListOf<History>(History("Unknown","Unknown","Unknown"))
            if(cu["gameHistory"] != null) {
                histList.clear()
                for (map in cu["gameHistory"] as List<Map<String, Any>>) {
                    histList.add(
                        History(
                            map["date"] as String,
                            map["map"] as String,
                            map["result"] as String
                        )
                    )
                }
            }
            /* Games History */
            val historyRecyclerView = findViewById<RecyclerView>(R.id.friendRecyclerHist)
            val historyAdapter =
                HistoryViewAdapter(applicationContext, histList)
            historyRecyclerView.adapter = historyAdapter
            historyRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        }

        app.getMessageHandler().checkForNewMessages()

    }

    /**
     * After returning to the activity we check for new messages
     */
    override fun onResume() {
        super.onResume()

        val app = applicationContext as MyApplication
        app.getMessageHandler().checkForNewMessages()
    }
}