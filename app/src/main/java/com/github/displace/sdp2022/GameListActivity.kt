package com.github.displace.sdp2022

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class GameListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.github.blecoeur.bootcamp.R.layout.activity_game_list)
    }


    //send the user to the Play screen : start a match
    fun playButton(view: View) {
        val intent = Intent(this, com.github.displace.sdp2022.GameVersusViewActivity::class.java)
        startActivity(intent)
    }
}