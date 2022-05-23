package com.github.displace.sdp2022.database

import android.content.Intent

/**
 * Used to remove the guest users from the database, since they become useless when we leave the application
 */
object CleanUpGuests {

    /**
     * Method that removes all guests from the database
     * @param intent is used by the DatabaseFactory to specify if the database should be mocked and/or in debug mode or not
     */
    fun cleanUpDatabaseFromGuests(intent: Intent) {
        val db = DatabaseFactory.getDB(intent)

        db.getThenCall<HashMap<String, *>>("CompleteUsers") { usrs ->
            for (id in usrs?.keys!!) {
                if(id.contains("guest")) {
                    db.delete("CompleteUsers/$id")
                }
            }
        }
    }
}