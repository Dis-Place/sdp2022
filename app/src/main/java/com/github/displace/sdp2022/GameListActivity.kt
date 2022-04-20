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
    fun playButton(view: View) {
        val intent = Intent(this, MatchMakingActivity::class.java)
        startActivity(intent)

    }

}