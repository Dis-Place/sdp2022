package com.github.displace.sdp2022

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.matchMaking.MatchMakingActivity
import com.github.displace.sdp2022.profile.messages.MessageHandler


class GameListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)

    }


    //send the user to the Play screen : start a match
    @Suppress("UNUSED_PARAMETER")
    fun playVs2Button(view: View) {
        val intent = Intent(this, MatchMakingActivity::class.java)
        intent.putExtra("nbPlayer",2L)
        intent.putExtra("gameMode","Versus")
        startActivity(intent)
    }

    //send the user to the Play screen : start a match
    @Suppress("UNUSED_PARAMETER")
    fun playVs3Button(view: View) {
        val intent = Intent(this, MatchMakingActivity::class.java)
        intent.putExtra("nbPlayer",3L)
        intent.putExtra("gameMode","Versus3P")
        startActivity(intent)
    }

    //send the user to the Play screen : start a match
    @Suppress("UNUSED_PARAMETER")
    fun playVs5Button(view: View) {
        val intent = Intent(this, MatchMakingActivity::class.java)
        intent.putExtra("nbPlayer",5L)
        intent.putExtra("gameMode","Versus5P")
        startActivity(intent)
    }

}