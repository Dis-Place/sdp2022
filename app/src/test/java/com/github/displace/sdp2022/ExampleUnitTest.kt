package com.github.displace.sdp2022

import GameComponents.GameEvent
import GameComponents.Point
import GameVersus.GameVersusViewModel
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
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    val mod = GameVersusViewModel()

    @Test
    fun GameViewLauch() {
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
}