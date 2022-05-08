package com.github.displace.sdp2022

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.github.displace.sdp2022.profile.FriendRequest
import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.achievements.AchievementsLibrary
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.users.CompleteUser
import com.google.firebase.database.FirebaseDatabase

class GameSummaryActivity : AppCompatActivity() {

    lateinit var layout: LinearLayout
    private val db : RealTimeDatabase = RealTimeDatabase().instantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase
    lateinit var app : MyApplication
    var mode : String? = ""
    var victory : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_summary)

        app = applicationContext as MyApplication

        layout = findViewById<LinearLayout>(R.id.layoutGameStats)
        val extras = intent.extras

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
            app.getActiveUser()!!.addGameInHistory(mode!!, app.getCurrentDate(), result)
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
        val distThisGame : Long = 0 //TODO : CHANGE TO THE REAL VALUE
        user.updateStats("Distance Moved" , distance.value+distThisGame)


        user.updateStats("Games Played" , played.value+1)
        //Check for achievements
        AchievementsLibrary.achievementCheck(app,user,played.value+1,AchievementsLibrary.gamesLib)
        if(result == "VICTORY") {
            user.updateStats("Games Won", won.value + 1)
            AchievementsLibrary.achievementCheck(app,user,won.value+1,AchievementsLibrary.victoryLib)
            AchievementsLibrary.achievementCheck(app,user,distThisGame,AchievementsLibrary.gameDistLib)

        }

    }


    fun friendInviteToOpponent(View : View) {

        val otherId = (intent.getSerializableExtra("others") as List<List<String>>)[0][1]
        val db = RealTimeDatabase().noCacheInstantiate(
            "https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",
            false
        ) as RealTimeDatabase
        db.referenceGet("CompleteUsers/$otherId/CompleteUser/partialUser","username").addOnSuccessListener { snapshot ->
            val name = snapshot.value as String? ?: ""
            FriendRequest.sendFriendRequest(this,otherId,FirebaseDatabase.getInstance("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/").reference,
                app.getActiveUser()!!.getPartialUser()
            )
        }
    }
}