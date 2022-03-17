package com.github.blecoeur.bootcamp

import android.annotation.SuppressLint
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
import org.osmdroid.util.GeoPoint


class GameVersusViewActivity : AppCompatActivity() {

    var goal = Point(3.0,4.0)
    val game = GameVersusViewModel()
    private val ZOOM = 16.0
    private val EPFL_POS = GeoPoint(46.52048,6.56782)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game_versus)
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        val map = findViewById<MapView>(R.id.map)
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);

        //to identify our app when downloading the map tiles (ie. pieces of the map)
        getInstance().setUserAgentValue(this.getPackageName())

        map.controller.setCenter(EPFL_POS)
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map.setTilesScaledToDpi(true) //scaling tiles in order to see them well at any zoom scale

        //setting zoom
        map.getController().setZoom(ZOOM)

        game.handleEvent(GameEvent.OnStart(goal, listOf(3.0),3)) //add a pop up with goal and photo info

        val tryTextView =  findViewById<TextView>(R.id.TryText).apply { text =
            "neutral"
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
            val tryTextView =  findViewById<TextView>(R.id.TryText).apply { text =
                "fail"
            }
        }else{
            if(res == 2){
                val tryTextView =  findViewById<TextView>(R.id.TryText).apply { text =
                    "end of game"
                }
            }
        }
    }

    //close the screen
    fun triButtonWin(view: View) {
        val res = game.handleEvent(GameEvent.OnPointSelected(3,Point(3.0,5.0)))
        if(res == 0){
            val tryTextView =  findViewById<TextView>(R.id.TryText).apply { text =
                "win"
            }
        }
    }

}