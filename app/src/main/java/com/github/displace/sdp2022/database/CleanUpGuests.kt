package com.github.displace.sdp2022.database

/**
 * Used to remove the guest users from the database, since they become useless when we leave the application
 */
object CleanUpGuests {

    private val GUEST_THRESHOLD = 50

    /**
     * Method that updates the guest indexes of all guests in the database, then removes the guests that are above a certain threshold GUEST_THRESHOLD.
     * We suppose that the guests above this threshold are old enough to not be used actively at the moment, and since the guests are only a one-time use, they can be deleted.
     * @param db database
     * @param guestId ID of the active guest
     */
    fun updateGuestIndexesAndCleanUpDatabase(db: GoodDB, guestId: String) {

        db.getThenCall<Map<String, *>>("CompleteUsers") { usrs ->
            if(usrs != null) {
                for (id in usrs.keys) {
                    if(id.contains("guest") && id != guestId) {     // For each guest in the db, increment the id
                        val guestDB = usrs[id] as Map<String, *>
                        val guestCompleteUserDB = guestDB["CompleteUser"] as Map<String, *>
                        val newIndex = (guestCompleteUserDB["guestIndex"] as Long) + 1
                        if(newIndex >= GUEST_THRESHOLD) {                   // If the id is above a certain threshold, we delete the guest
                            db.delete("CompleteUsers/$id")
                        } else {
                            db.update("CompleteUsers/$id/CompleteUser/guestIndex", newIndex)
                        }
                    }
                }
            }
        }
    }
}