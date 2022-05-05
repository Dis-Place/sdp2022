package com.github.displace.sdp2022.database

import android.content.Intent

/**
 * to mock the database in tests
 *
 * @see GoodDB
 * @see DatabaseFactory
 */
object MockDatabaseUtils {

    /**
     * specify in intent that a mock database should be used
     *
     * @param intent
     */
    fun mockIntent(intent: Intent) {
        intent.putExtra(DatabaseFactory.MOCK_DB_EXTRA_ID, "")
    }

    /**
     * specify in intent that the debug mode of a database should be used
     *
     * @param intent
     */
    fun debugIntent(intent: Intent) {
        intent.putExtra(DatabaseFactory.DEBUG_EXTRA_ID, "")
    }
}