package com.github.displace.sdp2022.map

import android.app.Activity
import com.github.displace.sdp2022.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.osmdroid.util.GeoPoint

/**
 * to send and retrieve position guesses of players
 * @param db database to send/retrieve the pinpoints from
 * @param gameInstanceName
 * @author LeoLgdr
 */
class PinpointsDBCommunicationHandler(private val db: RealTimeDatabase, private val gameInstanceName: String, private val activity: Activity) {

    /**
     * to send the player's pinpoints
     * @param playerID local player's unique id
     * @param pinpointsRef local reference to the pinpoints
     */
    fun updateDBPinpoints(playerID: String, pinpointsRef: PinpointsManager.PinpointsRef){
        val positions = pinpointsRef.get()
        db.update("GameInstance/${gameInstanceName}/id:${playerID}","pinpoints",
            listOf(DUMMY_HEAD).plus(positions.map { p -> listOf(p.latitude,p.longitude) })
        )
    }

    /**
     * to retrieve the opponent's pinpoints, with automatic updates in a PinpointsRef
     * @param playerID opponent's ID (ie, the remote user in game with the local player)
     * @param pinpointsRef local reference to the pinpoints
     */
    fun enableAutoupdateLocalPinpoints(playerID: String, pinpointsRef: PinpointsManager.PinpointsRef){
        val pinpointListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val r = (dataSnapshot.value ?: listOf<List<Double>>()) as List<List<Double>>
                if(activity.isDestroyed){
                    db.removeList("GameInstance/${gameInstanceName}/id:${playerID}","pinpoints",this)
                } else if(r.size > 1) {
                    pinpointsRef.set(
                        r.subList(1,r.size).map { l ->
                            GeoPoint(l[0], l[1])
                        })
                } else {
                    pinpointsRef.clear()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        db.addList("GameInstance/${gameInstanceName}/id:${playerID}","pinpoints",pinpointListener)
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
        db.update("GameInstance/${gameInstanceName}/id:${playerID}","pinpoints", listOf(DUMMY_HEAD))
    }

    companion object {
        val DUMMY_HEAD = listOf(0.0,0.0)
    }
}