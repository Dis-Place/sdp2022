package com.github.displace.sdp2022

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class GameSummaryActivity : AppCompatActivity() {

    lateinit var layout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_summary)

        layout = findViewById<LinearLayout>(R.id.layoutGameStats)
        val extras = intent.extras

        if(extras != null) {
            val roundStats = extras.getStringArrayList(EXTRA_STATS)
            val victory = extras.getBoolean(EXTRA_RESULT)

            if (roundStats != null) {
                for(s in roundStats) {
                    addRoundStat(s)
                }
            }
            updateScore(extras.getInt(EXTRA_SCORE_P1), extras.getInt(EXTRA_SCORE_P2))

            updateVictoryText(victory)

            val gameModeText = findViewById<TextView>(R.id.textViewGameMode)
            val mode = extras.getString(EXTRA_MODE)
            if(mode != null) {
                gameModeText.text = mode
            }
        }

        val mainMenuButton = findViewById<Button>(R.id.mainMenuButton)
        val replayButton = findViewById<Button>(R.id.gameListButton)

        mainMenuButton.setOnClickListener{backToMainMenu()}
        replayButton.setOnClickListener{backToGameList()}

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
        } else {
            victoryText.text = "DEFEAT"
            victoryText.setTextColor(Color.rgb(255,0,0))
        }
    }

    fun updateScore(scoreP1: Int, scoreP2: Int) {
        val sP1 = findViewById<TextView>(R.id.scoreP1)
        val sP2 = findViewById<TextView>(R.id.scoreP2)

        sP1.text = scoreP1.toString()
        sP2.text = scoreP2.toString()
    }

    fun backToMainMenu() {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }

    fun backToGameList() {
        val intent = Intent(this, GameListActivity::class.java)
        startActivity(intent)
    }
}