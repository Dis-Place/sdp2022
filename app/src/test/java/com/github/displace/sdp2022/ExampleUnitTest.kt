package com.github.displace.sdp2022

import GameComponents.GameEvent
import GameComponents.Player
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

    @Test
    fun PlayerTest(){
        val play = Player(3.0,2.0)
        println(play.pos[0])
        println(play.pos[1])
        play.pos = listOf(4.0,5.0)
        println(play.pos[0])
        println(play.pos[1])
    }
}
