package com.github.blecoeur.bootcamp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class GameListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)
    }


    //send the user to the Play screen : start a match
    fun playButton(view: View) {
        val intent = Intent(this, GameVersusViewActivity::class.java)
        startActivity(intent)
    }
}