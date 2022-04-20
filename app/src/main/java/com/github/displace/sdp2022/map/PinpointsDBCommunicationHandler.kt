package com.github.displace.sdp2022.map

import android.util.Log
import android.widget.TextView
import com.github.displace.sdp2022.*
import com.github.displace.sdp2022.gameComponents.Player
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime
import kotlin.reflect.typeOf

/**
 * to send and retrieve position guesses of players
 * @param db database to send/retrieve the pinpoints from
 * @author LeoLgdr
 */
class PinpointsDBCommunicationHandler(private val db: RealTimeDatabase, private val gameInstanceName: String) {

    /**
     * to send the player's pinpoints
     * @param player local player
     * @param pinpointsRef local reference to the pinpoints
     */
    fun updateDBPinpoints(player: String, pinpointsRef: MarkerManager.PinpointsRef){
        val positions = pinpointsRef.get()
        db.update("GameInstance/${gameInstanceName}/id:${player}","pinpoints",positions.map { p -> listOf(p.latitude,p.longitude) })
    }

    /**
     * to retrieve the opponent's pinpoints (ASYNCHRONOUS UPDATE OF LOCAL REF)
     * @param player opponent (ie, the remote user in game with the local player)
     * @param pinpointsRef local reference to the pinpoints
     */
    fun updateLocalPinpoints(other: String, pinpointsRef: MarkerManager.PinpointsRef){
        val pinpointListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val r = dataSnapshot.getValue()
                pinpointsRef.set(
                    ((r ?: listOf<List<Double>>()) as List<List<Double>>).map { l ->
                        GeoPoint(l[0], l[1])
                    })
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        db.addList("GameInstance/${gameInstanceName}/id:${other}","pinpoints",pinpointListener)
    }

    fun start(player: String){
        db.update("GameInstance/${gameInstanceName}/id:${player}","pinpoints", arrayListOf(listOf(0.0,0.0)))
    }
}