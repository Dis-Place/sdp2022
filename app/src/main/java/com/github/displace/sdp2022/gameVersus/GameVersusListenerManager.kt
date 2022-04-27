package com.github.displace.sdp2022.gameVersus

import com.github.displace.sdp2022.model.GameVersus

/**
 * to handle multiple GameVersusListeners
 */
class GameVersusListenerManager {
    private val listeners = mutableListOf<GameVersusListener>()

    fun addCall(gameVersusListener: GameVersusListener){
        listeners.add(gameVersusListener)
    }

    fun removeCall(gameVersusListener: GameVersusListener){
        listeners.add(gameVersusListener)
    }

    fun clearAllCalls(){
        listeners.clear()
    }

    fun invokeAll(gameInstance: GameVersus) {
        for(listener in current()) {
            listener.invoke(gameInstance)
        }
    }

    fun current() : List<GameVersusListener> {
        return listeners.toList()
    }
}