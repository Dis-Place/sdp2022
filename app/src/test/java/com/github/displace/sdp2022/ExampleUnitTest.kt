package com.github.displace.sdp2022

<<<<<<< HEAD
import gameComponents.GameEvent
import gameComponents.Player
import gameComponents.Point
import gameVersus.GameVersusViewModel
=======
import GameComponents.Player
>>>>>>> main
import org.junit.Test

import org.junit.Assert.*
import java.lang.Error

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
<<<<<<< HEAD
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    val mod = GameVersusViewModel()

    @Test
    fun GameViewLaunch() {
        try {
            mod.OnSurrend(3)
        }catch (error: Error){
            assertEquals(false,true)
        }
        assertNotEquals(null,4)
    }

    @Test
    fun GameViewOnStart(){
        mod.handleEvent(GameEvent.OnStart(Point(6.0,6.0),listOf(3.0,3.0,3.0),3))
    }

    @Test
    fun GameViewOnPointSelectedWin(){
        mod.handleEvent(GameEvent.OnStart(Point(6.0,6.0),listOf(3.0,3.0,3.0),3))
        mod.handleEvent(GameEvent.OnPointSelected(3, Point(6.0,6.0)))
    }

    @Test
    fun GameViewOnPointSelectedLose(){
        mod.handleEvent(GameEvent.OnStart(Point(6.0,6.0),listOf(3.0,3.0,3.0),3))
        mod.handleEvent(GameEvent.OnPointSelected(3, Point(12.0,12.0)))
        mod.handleEvent(GameEvent.OnPointSelected(3, Point(12.0,12.0)))
        mod.handleEvent(GameEvent.OnPointSelected(3, Point(12.0,12.0)))
        mod.handleEvent(GameEvent.OnPointSelected(3, Point(12.0,12.0)))
    }

    @Test
    fun GameViewOnSurrend(){
        mod.handleEvent(GameEvent.OnStart(Point(6.0,6.0),listOf(3.0,3.0,3.0),3))
        mod.handleEvent(GameEvent.OnSurrend(3))
    }

    @Test
    fun PlayerTestPos(){
        val play = Player(3.0,2.0)
        assertEquals(3.0,play.pos.first,0.0)
        assertEquals(2.0,play.pos.second,0.0)
    }

    @Test
    fun PlayerTestId(){
        val play = Player(3.0,2.0,3)
        assertEquals(3,play.uid)
    }
}
