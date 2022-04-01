package com.github.displace.sdp2022.map

import android.util.Log
import com.github.displace.sdp2022.Database
import com.github.displace.sdp2022.gameComponents.Player
import org.osmdroid.util.GeoPoint

/**
 * to send and retrieve position guesses of players
 * @param db database to send/retrieve the pinpoints from
 * @author LeoLgdr
 */
class PinpointsDBCommunicationHandler(private val db: Database, private val gameInstanceName: String) {

    /**
     * to send the player's pinpoints
     * @param player local player
     * @param pinpointsRef local reference to the pinpoints
     */
    fun updateDBPinpoints(player: Player, pinpointsRef: MarkerManager.PinpointsRef){
        val positions = pinpointsRef.get()
        db.update("GameInstance/${gameInstanceName}/id:" + player.uid,"pinpoints",positions.map { p -> listOf(p.latitude,p.longitude) })
    }

    /**
     * to retrieve the opponent's pinpoints (ASYNCHRONOUS UPDATE OF LOCAL REF)
     * @param player opponent (ie, the remote user in game with the local player)
     * @param pinpointsRef local reference to the pinpoints
     */
    fun updateLocalPinpoints(player: Player, pinpointsRef: MarkerManager.PinpointsRef){
        db.referenceGet("GameInstance/${gameInstanceName}/id:" + player.uid,"pinpoints").addOnSuccessListener { r ->
            pinpointsRef.set( (r.value as List<List<Double>>).map{x ->
                GeoPoint(x[0],x[1])
            } )
        }
    }
}