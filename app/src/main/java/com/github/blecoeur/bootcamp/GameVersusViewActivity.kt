package com.github.blecoeur.bootcamp

import gameComponents.GameEvent
import gameComponents.Point
import gameVersus.GameVersusViewModel
import android.content.Intent
import android.os.Bundle
import android.view.View
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory


class GameVersusViewActivity : AppCompatActivity() {

    var goal = Point(3.0,4.0)
    var tri = "neutral"
    val game = GameVersusViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game_versus)
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        val map = findViewById<MapView>(R.id.map)
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);

        //to identify our app when downloading the map tiles (ie. pieces of the map)
        getInstance().setUserAgentValue(this.getPackageName())

        game.handleEvent(GameEvent.OnStart(goal, listOf(3.0),3)) //add a pop up with goal and photo info

        val tryTextView =  findViewById<TextView>(R.id.TryText).apply { text =
            "tri : $tri"
        }
    }


    //close the screen
    fun closeButton(view: View) {
        game.handleEvent(GameEvent.OnSurrend(3))
        val intent = Intent(this, GameListActivity::class.java)
        startActivity(intent)
    }

    //close the screen
    fun triButtonFail(view: View) {
        val res = game.handleEvent(GameEvent.OnPointSelected(3,Point(13.0,14.0)))
        if(res == 1){
            tri = "fail" //check if that change or not the message
        }else{
            if(res == 2){
                tri = "end of game" //check if that change or not the message
                val intent = Intent(this, GameListActivity::class.java)
                startActivity(intent)
            }
        }
    }

    //close the screen
    fun triButtonWin(view: View) {
        val res = game.handleEvent(GameEvent.OnPointSelected(3,Point(3.0,5.0)))
        if(res == 0){
            tri = "win" //check if that change or not the message
            val intent = Intent(this, GameListActivity::class.java)
            startActivity(intent)
        }
    }

}