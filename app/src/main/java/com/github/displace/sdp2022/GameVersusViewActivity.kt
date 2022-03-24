package com.github.displace.sdp2022

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.github.displace.sdp2022.gameComponents.GameEvent
import com.github.displace.sdp2022.gameComponents.Point
import com.github.displace.sdp2022.gameVersus.GameVersusViewModel
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView


class GameVersusViewActivity : AppCompatActivity() {

    var goal = Point(3.0, 4.0)
    val game = GameVersusViewModel()
    private val ZOOM = 16.0
    private val EPFL_POS = GeoPoint(46.52048, 6.56782)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game_versus)
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        val map = findViewById<MapView>(R.id.map)
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);

        //to identify our app when downloading the com.github.displace.sdp2022.map tiles (ie. pieces of the com.github.displace.sdp2022.map)
        getInstance().setUserAgentValue(this.getPackageName())

        map.controller.setCenter(EPFL_POS)
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map.setTilesScaledToDpi(true) //scaling tiles in order to see them well at any zoom scale

        //setting zoom
        map.getController().setZoom(ZOOM)

        game.handleEvent(GameEvent.OnStart(goal, 3,3)) //add a pop up with goal and photo info

        val tryTextView =  findViewById<TextView>(R.id.TryText).apply { text =
            "neutral"
        }
    }


    //close the screen
    @Suppress("UNUSED_PARAMETER")
    fun closeButton(view: View) {
        val res = game.handleEvent(GameEvent.OnSurrend(3))
        if(res == 3) {
            val intent = Intent(this, GameListActivity::class.java)
            startActivity(intent)
        }
    }

    //close the screen
    @Suppress("UNUSED_PARAMETER")
    fun triButtonFail(view: View) {
        val res = game.handleEvent(GameEvent.OnPointSelected(Point(13.0,14.0)))
        if(res == 1){
            val tryTextView =  findViewById<TextView>(R.id.TryText).apply { text =
                "fail"
            }
        } else {
            if (res == 2) {
                findViewById<TextView>(R.id.TryText).apply {
                    text =
                        "end of game"
                }
            }
        }
    }

    //close the screen
    @Suppress("UNUSED_PARAMETER")
    fun triButtonWin(view: View) {
        val res = game.handleEvent(GameEvent.OnPointSelected(3, Point(3.0, 5.0)))
        if (res == 0) {
            findViewById<TextView>(R.id.TryText).apply {
                text =
                    "win"
            }
        }
    }

}