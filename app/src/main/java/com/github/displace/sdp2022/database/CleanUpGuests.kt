package com.github.displace.sdp2022.database

import android.content.Intent
import com.github.displace.sdp2022.RealTimeDatabase

/**
 * Used to remove the guest users from the database, since they become useless when we leave the application
 */
object CleanUpGuests {

    private val GUEST_THRESHOLD = 6

    /**
     * Method that updates the guest indexes of all guests in the database, then removes the guests that are above a certain threshold GUEST_THRESHOLD.
     * We suppose that the guests above this threshold are old enough to not be used actively at the moment, and since the guests are only a one-time use, they can be deleted.
     * @param db database
     * @param guestId ID of the active guest
     */
    fun updateGuestIndexesAndCleanUpDatabase(db: RealTimeDatabase, guestId: String) {

        db.referenceGet("CompleteUsers", "").addOnSuccessListener{ usrs ->
            val usrsDB = usrs.value as HashMap<String, *>

            for (id in usrsDB.keys) {
                if(id.contains("guest") && id != guestId) {
                    val guestDB = usrsDB[id] as HashMap<String, *>
                    val guestCompleteUserDB = guestDB["CompleteUser"] as HashMap<String, *>? ?: continue
                    val newIndex = (guestCompleteUserDB["guestIndex"] as Long) + 1
                    if(newIndex >= GUEST_THRESHOLD) {
                        db.delete("CompleteUsers", id)
                    } else {
                        db.update("CompleteUsers/$id/CompleteUser/guestIndex", "", newIndex)
                    }
                }
            }

        }
        // Code for the GoodDB
        /*db.getThenCall<HashMap<String, *>>("CompleteUsers") { usrs ->
            for (id in usrs?.keys!!) {
                if(id.contains("guest") && id != guestId) {
                    val guestDB = usrs[id] as HashMap<String, *>
                    val guestCompleteUserDB = guestDB["CompleteUser"] as HashMap<String, *>
                    val newIndex = (guestCompleteUserDB["guestIndex"] as Long) + 1
                    if(newIndex >= GUEST_THRESHOLD) {
                        db.delete("CompleteUsers/$id")
                    } else {
                        db.update("CompleteUsers/$id/CompleteUser/guestIndex", newIndex)
                    }
                }
            }
        }*/
    }
}