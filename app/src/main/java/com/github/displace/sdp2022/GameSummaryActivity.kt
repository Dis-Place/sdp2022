package com.github.displace.sdp2022

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
import com.github.displace.sdp2022.database.DatabaseConstants.DB_URL
import com.github.displace.sdp2022.profile.friends.NewFriendViewAdapter
import com.github.displace.sdp2022.profile.statistics.Statistic
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.PartialUser
import com.github.displace.sdp2022.profile.achievements.AchievementsLibrary
import com.github.displace.sdp2022.util.DateTimeUtil
import java.lang.Exception

class GameSummaryActivity : AppCompatActivity() {

    lateinit var layout: LinearLayout
    private val db : RealTimeDatabase = RealTimeDatabase().instantiate(DB_URL,false) as RealTimeDatabase

    private lateinit var activeUser: CompleteUser
    private lateinit var stats: List<Statistic>

    lateinit var app : MyApplication
    var mode : String? = ""
    var victory : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_summary)

        findViewById<Group>(R.id.FriendGroup).visibility = View.INVISIBLE
        app = applicationContext as MyApplication
        layout = findViewById<LinearLayout>(R.id.layoutGameStats)
        val extras = intent.extras
        app = applicationContext as MyApplication
        activeUser = app.getActiveUser()!!
        stats = activeUser.getStats()

        if (extras != null) {
            val roundStats = extras.getStringArrayList(EXTRA_STATS)
            victory = extras.getBoolean(EXTRA_RESULT)!!

            if (roundStats != null) {
                for (s in roundStats) {
                    addRoundStat(s)
                }
            }

            val gameModeText = findViewById<TextView>(R.id.textViewGameMode)
            mode = extras.getString(EXTRA_MODE)
            if (mode != null) {
                gameModeText.text = mode
            }

            updateVictoryText(victory)

        }

        val mainMenuButton = findViewById<Button>(R.id.mainMenuButton)
        val replayButton = findViewById<Button>(R.id.gameListButton)

        mainMenuButton.setOnClickListener { backToMainMenu() }
        replayButton.setOnClickListener { backToGameList() }

        val friendRecyclerView = findViewById<RecyclerView>(R.id.recyclerFriend)

        var others :List<PartialUser> = listOf()

        try {
            (intent.getSerializableExtra("others")!! as List<List<String>>).forEach { x ->
                others = others.plus(PartialUser(x[0], x[1]))
            }
        }catch(e: Exception){}

        val friendAdapter = NewFriendViewAdapter(
            applicationContext,
            others,
            1
        )
        friendRecyclerView.adapter = friendAdapter
        friendRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        app.getMessageHandler().checkForNewMessages()

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
        val resString = if (vict) "VICTORY" else "DEFEAT"
        victoryText.text = resString

        if(vict) {
            victoryText.setTextColor(Color.rgb(0,255,0))
        } else {
            victoryText.setTextColor(Color.rgb(255,0,0))
        }

        gameHistoryUpdate(resString)

    }

    private fun backToMainMenu() {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }

    private fun backToGameList() {
        val intent = Intent(this, GameListActivity::class.java)
        startActivity(intent)
    }

    /**
     * Adds this result to the users game history
     */
    private fun gameHistoryUpdate(result : String) {
        if( mode != null) {
            app.getActiveUser()!!.addGameInHistory(mode!!, DateTimeUtil.currentDate(), result)
        }
        statsUpdate(result)
    }

    /**
     * Updates the statistics of the user : games played and games won : used to check for achievements
     */
    private fun statsUpdate(result : String){
        val user = app.getActiveUser()!!
        val played = user.getStat("Games Played")
        val won = user.getStat("Games Won")
        val distance = user.getStat("Distance Moved")
        val distThisGame : Long = intent.getDoubleExtra("totalDist",0.0)!!.toLong()
        user.updateStats("Distance Moved" , distance.value+distThisGame)


        user.updateStats("Games Played" , played.value+1)
        //Check for achievements
        AchievementsLibrary.achievementCheck(user,played.value+1,AchievementsLibrary.gamesLib)
        if(result == "VICTORY") {
            user.updateStats("Games Won", won.value + 1)
            AchievementsLibrary.achievementCheck(user,won.value+1,AchievementsLibrary.victoryLib)
            AchievementsLibrary.achievementCheck(user,distThisGame,AchievementsLibrary.gameDistLib)

        }

    }


    fun friendInviteToOpponent(View : View) {
        findViewById<Group>(R.id.FriendGroup).visibility = android.view.View.VISIBLE
        findViewById<Group>(R.id.MainScreen).visibility = android.view.View.INVISIBLE
    }

    fun cancelButton(View: View) {
        findViewById<Group>(R.id.FriendGroup).visibility = android.view.View.INVISIBLE
        findViewById<Group>(R.id.MainScreen).visibility = android.view.View.VISIBLE
    }

    override fun onBackPressed() {
        backToGameList()
    }

    override fun onResume() {
        super.onResume()

        val app = applicationContext as MyApplication
        app.getMessageHandler().checkForNewMessages()
    }
}