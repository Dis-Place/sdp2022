package com.github.displace.sdp2022.map

import android.app.Activity
import android.util.Log
import com.github.displace.sdp2022.database.GoodDB
import com.github.displace.sdp2022.util.listeners.Listener
import org.osmdroid.util.GeoPoint

/**
 * to send and retrieve position guesses of players
 * @param db database to send/retrieve the pinpoints from
 * @param gameInstanceName
 */
class PinpointsDBHandler(private val db: GoodDB, private val gameInstanceName: String, private val activity: Activity) {
    private fun path(playerID: String): String  {
        return "GameInstance/${gameInstanceName}/id:${playerID}/pinpoints"
    }
    /**
     * to send the player's pinpoints
     * @param playerID local player's unique id
     * @param pinpointsRef local reference to the pinpoints
     */
    fun updateDBPinpoints(playerID: String, pinpointsRef: PinpointsManager.PinpointsRef){
        val positions = pinpointsRef.get()
        db.update(path(playerID),
            listOf(DUMMY_HEAD).plus(positions.map { p -> listOf(p.latitude,p.longitude) })
        )
    }

    /**
     * to retrieve the opponent's pinpoints, with automatic updates in a PinpointsRef
     * @param playerID opponent's ID (ie, the remote user in game with the local player)
     * @param pinpointsRef local reference to the pinpoints
     */
    fun enableAutoupdateLocalPinpoints(playerID: String, pinpointsRef: PinpointsManager.PinpointsRef) {
        val pinpointListener = object : Listener<List<List<Double>>?> {

            override fun invoke(value: List<List<Double>>?) {
                if(activity.isDestroyed){
                    db.removeListener(path(playerID), this)
                } else if(value != null && value.size > 1) {
                    pinpointsRef.set(
                        value.subList(1,value.size).map { l ->
                            GeoPoint(l[0], l[1])
                        })
                } else {
                    pinpointsRef.clear()
                }
            }
        }

        db.addListener(path(playerID),pinpointListener)
        /*db.referenceGet("GameInstance/${gameInstanceName}/id:${playerID}","pinpoints").addOnSuccessListener { r ->
            val pinpointsPosList = (r.value ?: listOf<List<Double>>()) as List<List<Double>>
            if(pinpointsPosList.size > 1) {
                pinpointsRef.set( pinpointsPosList.subList(1,pinpointsPosList.size).map{ l ->
                    GeoPoint(l[0],l[1])
                } )
            } else {
                pinpointsRef.clear()
            }
        }*/
    }

    /**
     * initialize pinpoints list on DB with a Dummy Value
     * @param playerID corresponding to the issuer of the Pinpoints
     */
    fun initializePinpoints(playerID: String){
        db.update(path(playerID), listOf(DUMMY_HEAD))
    }

    companion object {
        val DUMMY_HEAD = listOf(0.0,0.0)
    }
}