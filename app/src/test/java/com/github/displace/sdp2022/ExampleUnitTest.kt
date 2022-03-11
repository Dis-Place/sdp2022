package com.github.displace.sdp2022

import GameComponents.Player
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
    fun PlayerTest(){
        val play = Player(3.0,2.0)
        assertEquals(3.0,play.pos[0],0.0)
        assertEquals(2.0,play.pos[1],0.0)
        play.pos = listOf(4.0,5.0)
        assertEquals(4.0,play.pos[0],0.0)
        assertEquals(5.0,play.pos[1],0.0)
    }
}
