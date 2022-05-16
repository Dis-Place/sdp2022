package com.github.displace.sdp2022

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.matchMaking.MatchMakingActivity
import android.widget.Toast


class GameListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)

        val app = applicationContext as MyApplication
        app.getMessageHandler().checkForNewMessages()
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

    @Suppress("UNUSED_PARAMETER")
    fun infoVsButton(view: View) {
        Toast.makeText(this, "A game of hide and seek with 2 peoples. The first player to find the other win", Toast.LENGTH_LONG).show()
    }

    @Suppress("UNUSED_PARAMETER")
    fun infoVs3Button(view: View) {
        Toast.makeText(this, "A game of hide and seek with 3 peoples. When someone is found, he get expulsed from the game. The winner is the last player in the game.", Toast.LENGTH_LONG).show()
    }

    @Suppress("UNUSED_PARAMETER")
    fun infoVs5Button(view: View) {
        Toast.makeText(this, "A game of hide and seek with 5 peoples. When someone is found, he get expulsed from the game. The winner is the last player in the game.", Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        val app = applicationContext as MyApplication
        app.getMessageHandler().checkForNewMessages()
    }

}