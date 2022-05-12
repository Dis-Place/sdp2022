package com.github.displace.sdp2022

import android.app.Application
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.profile.FriendRequest
import com.github.displace.sdp2022.profile.friends.FriendViewAdapter
import com.github.displace.sdp2022.profile.friends.NewFriendViewAdapter
import com.github.displace.sdp2022.profile.statistics.Statistic
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.FirebaseDatabase

class GameSummaryActivity : AppCompatActivity() {

    lateinit var layout: LinearLayout
    private val db : RealTimeDatabase = RealTimeDatabase().instantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase
    private lateinit var app : MyApplication
    private lateinit var activeUser: CompleteUser
    private lateinit var stats: List<Statistic>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_summary)

        findViewById<Group>(R.id.FriendGroup).visibility = View.INVISIBLE
        layout = findViewById<LinearLayout>(R.id.layoutGameStats)
        val extras = intent.extras
        app = applicationContext as MyApplication
        activeUser = app.getActiveUser()!!
        stats = activeUser.getStats()

        if (extras != null) {
            val roundStats = extras.getStringArrayList(EXTRA_STATS)
            val victory = extras.getBoolean(EXTRA_RESULT)

            if (roundStats != null) {
                for (s in roundStats) {
                    addRoundStat(s)
                }
            }
            updateVictoryText(victory)

            val gameModeText = findViewById<TextView>(R.id.textViewGameMode)
            val mode = extras.getString(EXTRA_MODE)
            if (mode != null) {
                gameModeText.text = mode
            }
        }

        activeUser.updateStats(stats[1].name, stats[1].value + intent.getIntExtra("totalTime",0)!!) // is totalTime
        activeUser.updateStats(stats[2].name, (stats[2].value + intent.getDoubleExtra("totalDist",0.0)!!).toLong()) // is totalDist

        val mainMenuButton = findViewById<Button>(R.id.mainMenuButton)
        val replayButton = findViewById<Button>(R.id.gameListButton)

        mainMenuButton.setOnClickListener { backToMainMenu() }
        replayButton.setOnClickListener { backToGameList() }

        val friendRecyclerView = findViewById<RecyclerView>(R.id.recyclerFriend)

        var others :List<PartialUser> = listOf()
        (intent.getSerializableExtra("others") as List<List<String>>).forEach { x ->
            others = others.plus(PartialUser(x[0],x[1]))
        }

        val friendAdapter = NewFriendViewAdapter(
            applicationContext,
            others,
            1
        )
        friendRecyclerView.adapter = friendAdapter
        friendRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

    }

    fun addRoundStat(info: String) {
        val tv = TextView(applicationContext)
        tv.text = info
        tv.textSize = 25F
        tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
        layout.addView(tv)
    }

    fun updateVictoryText(vict: Boolean) {
        val victoryText = findViewById<TextView>(R.id.textViewResult)
        if(vict) {
            victoryText.text = "VICTORY"
            victoryText.setTextColor(Color.rgb(0,255,0))
            activeUser.updateStats(stats[0].name, stats[0].value + 1) // is nbWin
        } else {
            victoryText.text = "DEFEAT"
            victoryText.setTextColor(Color.rgb(255,0,0))
        }
    }

    fun backToMainMenu() {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }

    fun backToGameList() {
        val intent = Intent(this, GameListActivity::class.java)
        startActivity(intent)
    }


    fun friendInviteToOpponent(View : View) {
        findViewById<Group>(R.id.FriendGroup).visibility = android.view.View.VISIBLE
        findViewById<Group>(R.id.MainScreen).visibility = android.view.View.INVISIBLE
    }

    fun cancelButton(View: View) {
        findViewById<Group>(R.id.FriendGroup).visibility = android.view.View.INVISIBLE
        findViewById<Group>(R.id.MainScreen).visibility = android.view.View.VISIBLE
    }
}