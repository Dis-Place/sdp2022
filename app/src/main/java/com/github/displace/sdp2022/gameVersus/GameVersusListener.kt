package com.github.displace.sdp2022.gameVersus

import com.github.displace.sdp2022.model.GameVersus

/**
 * for listeners over a GameVersus game instance
 * @author LeoLgdr
 */
fun interface GameVersusListener {
    fun invoke(gameInstance: GameVersus)
}